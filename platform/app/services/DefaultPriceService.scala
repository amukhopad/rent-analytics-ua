package services

import entities.ApartmentData
import javax.inject._
import model.ApartmentRepository
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.sql.{DataFrame, SparkSession}
import play.api.Configuration

@Singleton
class DefaultPriceService @Inject()(
    appConf: Configuration,
    repository: ApartmentRepository) extends PriceService {

  override def calculatePrice(apt: ApartmentData): Double = {
    val model = repository.retrieveModel(apt)
    val df = buildDfFromData(apt)

    predictPrice(model, df)
  }

  private def buildDfFromData(apt: ApartmentData)(implicit spark: SparkSession): DataFrame =
    spark.createDataFrame(Seq(
      (apt.area, apt.wallType, apt.district, apt.metro)
    )).toDF("area", "wall_type", "district", "metro")

  private def predictPrice(model: CrossValidatorModel, df: DataFrame): Double = {
    model.transform(df)
      .select("prediction")
      .first()
      .getDouble(0)
  }

  implicit val spark: SparkSession = SparkSession
    .builder()
    .appName(appConf.get[String]("app.name"))
    .master(appConf.get[String]("app.spark.master"))
    .config("spark.hadoop.fs.s3a.endpoint", appConf.get[String]("fs.s3.endpoint"))
    .config("spark.hadoop.fs.s3a.access.key", appConf.get[String]("fs.s3.access.key"))
    .config("spark.hadoop.fs.s3a.secret.key", appConf.get[String]("fs.s3.secret.key"))
    .config("spark.hadoop.fs.s3a.path.style.access", appConf.get[String]("fs.s3.path.style.access"))
    .config("spark.hadoop.fs.s3a.impl", appConf.get[String]("fs.s3.impl"))
    .getOrCreate()
}
