package model

import java.util.concurrent.TimeUnit

import entities.ApartmentData
import javax.inject._
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.spark_project.guava.cache.{CacheBuilder, CacheLoader}
import play.api.Configuration

@Singleton
class DefaultApartmentRepository @Inject()(
    appConf: Configuration) extends ApartmentRepository {
  private val cache = CacheBuilder.newBuilder()
    .maximumSize(2)
    .refreshAfterWrite(24, TimeUnit.HOURS)
    .build(
      new CacheLoader[String, CrossValidatorModel] {
        def load(path: String): CrossValidatorModel = {
          CrossValidatorModel.load(path)
        }
      }
    )

  override def retrieveModel(apt: ApartmentData): CrossValidatorModel = {
    cache.get(appConf.get[String]("app.model.location"))
  }
}
