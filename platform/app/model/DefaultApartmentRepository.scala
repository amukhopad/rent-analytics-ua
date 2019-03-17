package model

import java.util.concurrent.TimeUnit

import entities.ApartmentData
import javax.inject._
import org.apache.spark.ml.PipelineModel
import org.spark_project.guava.cache.{CacheBuilder, CacheLoader}
import utils.Constants

@Singleton
class DefaultApartmentRepository extends ApartmentRepository {
  private val cache = CacheBuilder.newBuilder()
    .maximumSize(2)
    .refreshAfterWrite(24, TimeUnit.HOURS)
    .build(
      new CacheLoader[String, PipelineModel] {
        def load(path: String): PipelineModel = {
          PipelineModel.load(path)
        }
      }
    )

  override def retrieveModel(apt: ApartmentData): PipelineModel = {
    cache.get(Constants.ModelBaseLocation)
  }
}
