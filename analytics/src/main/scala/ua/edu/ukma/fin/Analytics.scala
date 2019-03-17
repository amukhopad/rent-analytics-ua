package ua.edu.ukma.fin

import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.ml.regression.{GBTRegressor, LinearRegression}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import scala.reflect.io.Path
import scala.util.Try

object Analytics extends App {
  val spark = SparkSession
    .builder()
    .appName("analytics")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  val df = spark.read.format("csv")
    .option("header", "true")
    .option("inferSchema", "true")
    .load("/Users/enginebreaksdown/dev/rent-analytics-ua/apartments_kyiv.csv")

  df.show(5)
  df.printSchema()

  val dataset = df
    .withColumn("labels", 'price.cast("Double"))
    .select(
      'district, 'rooms, 'area, 'wall_type, 'metro, 'view, 'jacuzzi, 'labels)
    .cache()

  dataset.show(5, truncate = false)

  val Array(training, test) = dataset.randomSplit(Array(0.70, 0.30))

  val stringFields = Array("district", "metro", "wall_type")

  val indexers = stringFields
    .map { columnName =>
      new StringIndexer()
        .setInputCol(columnName)
        .setOutputCol(columnName + "_idx")
        .setHandleInvalid("skip")
    }

  val ohe = new OneHotEncoderEstimator()
    .setInputCols(stringFields.map(_ + "_idx"))
    .setOutputCols(stringFields.map(_ + "_vec"))

  val features = dataset.columns
    .filterNot("labels".equals)
    .filterNot(stringFields.contains)
    .filterNot(_.contains("_idx"))

  val assembler = new VectorAssembler()
    .setInputCols(Array("area", "district_vec"))
    .setOutputCol("assembled_features")
    .setHandleInvalid("skip")

  val scaler = new MinMaxScaler()
    .setInputCol("assembled_features")
    .setOutputCol("features")

  val regressor = new LinearRegression()
    .setFeaturesCol("features")
    .setLabelCol("labels")
    .setMaxIter(10)

  val pipeline = new Pipeline().setStages(
    indexers ++ Array(
      ohe,
      assembler,
      scaler,
      regressor
    ))


  val evaluator = new RegressionEvaluator()
    .setLabelCol("labels")
    .setPredictionCol("prediction")
    .setMetricName("rmse")

  val paramGrid = new ParamGridBuilder()
    .addGrid(regressor.regParam, Array(0.01, 0.25, 0.5, 1.0))
    .addGrid(regressor.tol, Array(1e-9, 1e-6, 0.1, 1, 3))
    .addGrid(regressor.elasticNetParam, Array(0.0, 0.01, 0.1, 1))
    .addGrid(regressor.epsilon, Array(1.000001, 1.1, 1.35, 1.5, 2, 3))
    .addGrid(regressor.standardization, Array(true, false))
    .build()

  val model = new CrossValidator()
    .setEstimatorParamMaps(paramGrid)
    .setEstimator(pipeline)
    .setEvaluator(evaluator)
    .fit(training)

  val metrics = model.getEstimatorParamMaps
    .zip(model.avgMetrics)
    .maxBy(_._2)
    ._1

  println(s"avg metrics: ${metrics}")

  val best = model.bestModel.asInstanceOf[PipelineModel]
  val predicted = best.transform(test)

  predicted
  .select('features, 'labels, 'prediction)
    .show(2500)

  val mean = predicted.select(avg('prediction)).first().getDouble(0)

  val rmse = evaluator.evaluate(predicted)

  val variation = rmse / mean * 100

  println(s"\nRoot Mean Squared Error (RMSE) on test data = $rmse")
  println(s"Mean is $mean\n")
  printf(s"Coeficient of variation is %.2f%%\n\n", variation)


  val modelLocation = "/Users/enginebreaksdown/dev/rent-analytics-ua/ml_model"
  Try(Path(modelLocation).deleteRecursively())
  model.save(modelLocation)
}
