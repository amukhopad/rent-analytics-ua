package ua.edu.ukma.fin

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

  val dropped = df
    .drop("street")
    .drop("date")
    .drop("id")
    .drop("metro_branch")
    .cache()

  dropped.show(5)

  val dataset = AnalyticsUtils.indexStringLabels(
    Array("wall_type", "district", "metro", "city", "state"),
    dropped).cache()

  dataset.show(5)



  val Array(training, validation) = dataset.randomSplit(Array(0.75, 0.25))
}
