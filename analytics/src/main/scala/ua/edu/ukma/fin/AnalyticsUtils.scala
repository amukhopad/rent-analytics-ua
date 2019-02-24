package ua.edu.ukma.fin

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.DataFrame

object AnalyticsUtils {

  /**
    * Add indexed columns for given columns containing string features
    *
    * @param columns array of columns with string values
    * @param df      original DataFrame
    * @return new DataFrame
    */
  def indexStringLabels(columns: Array[String], df: DataFrame): DataFrame =
    new Pipeline()
      .setStages(columns.map(columnIndexer))
      .fit(df).transform(df)


  private def columnIndexer(columnName: String): StringIndexer =
    new StringIndexer()
      .setInputCol(columnName)
      .setOutputCol(s"${columnName}_idx")
      .setHandleInvalid("skip")
}

