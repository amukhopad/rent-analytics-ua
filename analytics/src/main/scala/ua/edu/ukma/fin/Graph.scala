package ua.edu.ukma.fin

import org.apache.spark.sql.{Column, DataFrame}
import vegas.DSL.{OptArg, Vegas}
import vegas.sparkExt._
import vegas._
import vegas.spec.Spec


object Graph {
  def plot(df: DataFrame, x: Column, y: Column,
      width: Double = 800,
      height: Double = 600,
      mark: Spec.Mark = Point): Unit = {
    Vegas(width = OptArg[Double](width),
      height = OptArg[Double](height))
      .withDataFrame(df.select(x, y))
      .mark(mark)
      .encodeX(x.toString(), Quant)
      .encodeY(y.toString(), Quant)
      .show
  }
}
