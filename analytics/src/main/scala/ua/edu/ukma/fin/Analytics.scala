package ua.edu.ukma.fin

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.ml.regression.GBTRegressor
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Analytics extends App {
  val appConf = Conf()

  val spark = SparkSession
    .builder()
    .appName(appConf.get("app.name"))
    .master(appConf.get("app.spark.master"))
    .config("spark.hadoop.fs.s3a.endpoint", appConf.get("fs.s3.endpoint"))
    .config("spark.hadoop.fs.s3a.access.key", appConf.get("fs.s3.access.key"))
    .config("spark.hadoop.fs.s3a.secret.key", appConf.get("fs.s3.secret.key"))
    .config("spark.hadoop.fs.s3a.path.style.access", appConf.get("fs.s3.path.style.access"))
    .config("spark.hadoop.fs.s3a.impl", appConf.get("fs.s3.impl"))
    .getOrCreate()

  import spark.implicits._

  val df = spark.read.format("csv")
    .option("header", "true")
    .option("inferSchema", "true")
    .load("/Users/enginebreaksdown/dev/rent-analytics-ua/apartments_kyiv.csv")

  val dataset = df
    .withColumn("labels", 'price.cast("Double"))
    .select('district, 'area, 'metro, 'wall_type, 'labels)
    .where('metro.isNotNull && 'district.isNotNull)
    .cache()

  dataset.show(10, truncate = false)

  val stringFields = Array("district", "metro", "wall_type")

  val indexers = stringFields
    .map { columnName =>
      new StringIndexer()
        .setInputCol(columnName)
        .setOutputCol(columnName + "_idx")
        .setHandleInvalid("keep")
    }

  val ohe = new OneHotEncoderEstimator()
    .setInputCols(stringFields.map(_ + "_idx"))
    .setOutputCols(stringFields.map(_ + "_vec"))

  val assembler = new VectorAssembler()
    .setInputCols("area" +: stringFields.map(_ + "_vec"))
    .setOutputCol("assembled_features")
    .setHandleInvalid("skip")

  val scaler = new MinMaxScaler()
    .setInputCol("assembled_features")
    .setOutputCol("features")
    .setMin(0)
    .setMax(1)

  val regressor = new GBTRegressor()
    .setFeaturesCol("features")
    .setLabelCol("labels")
    .setFeatureSubsetStrategy("auto")
    .setMaxDepth(30)
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
    .addGrid(regressor.minInfoGain, Array(0, 1e-10, 0.01, 0.1, 1, 10, 100))
    .addGrid(regressor.stepSize, Array(0.01, 0.1, 0.25, 0.5, 0.7, 1))
    .build()

  val model = new CrossValidator()
    .setEstimatorParamMaps(paramGrid)
    .setEstimator(pipeline)
    .setEvaluator(evaluator)
    .setNumFolds(7)
    .fit(dataset)


  val sample = model.transform(dataset.sample(10.0 / dataset.count())).cache()

  sample.select("features", "labels", "prediction").show(10)

  val metrics = model.getEstimatorParamMaps
    .zip(model.avgMetrics)
    .maxBy(_._2)
    ._1

  println(s"Avg metrics: ${metrics}")

  val rmse = evaluator.evaluate(sample)

  val mean = sample.select(avg('labels)).first().getDouble(0)

  println(
    s"""
       |RMSE: ${rmse}
       |MEAN: ${mean}
       |CV: ${rmse / mean}
       |""".stripMargin)

  val modelLocation = appConf.get("app.model.location")
  model.write.overwrite().save(modelLocation)
}
