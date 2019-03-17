package services

import entities.ApartmentData
import javax.inject._
import model.ApartmentRepository
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.Constants

@Singleton
class DefaultPriceService @Inject()(
    repository: ApartmentRepository) extends PriceService {

  override def calculatePrice(apt: ApartmentData): Double = {
    val model = repository.retrieveModel(apt)
    val df = buildDfFromData(apt)

    predictPrice(model, df)
  }

  private def buildDfFromData(apt: ApartmentData)(implicit spark: SparkSession): DataFrame =
    spark.createDataFrame(Seq(
      (apt.area, apt.district)
    )).toDF("area", "district")

  private def predictPrice(model: PipelineModel, df: DataFrame): Double = {
    model.transform(df)
      .select("prediction")
      .first()
      .getDouble(0)
  }

  implicit val spark: SparkSession = SparkSession
    .builder()
    .appName(Constants.AppName)
    .master(Constants.SparkMaster)
    .getOrCreate()
}
