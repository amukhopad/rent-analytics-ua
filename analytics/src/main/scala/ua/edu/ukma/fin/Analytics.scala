package ua.edu.ukma.fin

import org.apache.spark.sql.SparkSession

object Analytics extends App {
  val spark = SparkSession
    .builder()
    .appName("analytics")
    .master("local[*]")
    .getOrCreate()

  val df = spark.read.format("csv")
    .option("header", "true")
    .option("inferSchema", "true")
    .load("/Users/enginebreaksdown/dev/rent-analytics-ua/apartments.csv")

  df.show(5)
}
