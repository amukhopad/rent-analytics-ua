package ua.edu.ukma.fin

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.DecisionTreeRegressor
import org.apache.spark.sql.SparkSession

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
    .load("/Users/enginebreaksdown/dev/rent-analytics-ua/apartments.csv")

  df.show(5)

  val indexed = AnalyticsUtils.indexStringLabels(
    Array("wall_type", "district", "metro", "metro_branch", "city", "state"),
    df).cache()

  val dataset = indexed
    .withColumn("with_view", 'view.cast("Int"))
    .withColumn("labels", 'price.cast("Double"))
    .select(
      'square, 'rooms, 'floor, 'total_floors, 'furnished,
      'heating, 'repaired,'balcony,'jacuzzi,'with_view,
      'wall_type_idx, 'district_idx, 'metro_idx, 'metro_branch_idx,
      'city_idx, 'state_idx, 'labels)
    .filter(r => !r.anyNull)
    .cache()

  dataset.show(5)

  dataset.printSchema()

  val features = dataset.columns.filterNot(_.equals("labels"))

  val readyData = new VectorAssembler()
    .setInputCols(features)
    .setOutputCol("features")
    .setHandleInvalid("skip")
    .transform(dataset)
    .select("features", "labels")
    .cache()


  readyData.show(20, truncate = false)



  val Array(training, validation) = readyData.randomSplit(Array(0.75, 0.25))

  val regression = new DecisionTreeRegressor()
    .setMaxBins(512)
    .setMaxDepth(16)
    .setFeaturesCol("features")
    .setLabelCol("labels")

  val pipe = new Pipeline()
    .setStages(Array(regression))

  val model = pipe.fit(training)

  val predictions = model.transform(validation)

  predictions.show(2500, truncate = false)

  model.save("/Users/enginebreaksdown/dev/rent-analytics-ua/ml_model")

}
