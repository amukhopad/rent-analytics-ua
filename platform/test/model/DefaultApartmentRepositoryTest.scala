package model

import entities.ApartmentData
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.scalatest.FlatSpec
import play.api.Configuration
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.spark_project.guava.cache.LoadingCache


class DefaultApartmentRepositoryTest extends FlatSpec with MockitoSugar {
  private val conf = mock[Configuration]
  private val aptData = mock[ApartmentData]
  private val cache = mock[LoadingCache[String, CrossValidatorModel]]
  private val repo = new DefaultApartmentRepository(conf)

  repo.setCache(cache)

  "Model" should "be called from Configuration" in {
    repo.retrieveModel(aptData)

    verify(conf).get[String]("app.model.location")
  }

  "Cache" should "be called" in {
    when(conf.get[String]("app.model.location")) thenReturn "test/data"

    repo.retrieveModel(aptData)

    verify(cache).get("test/data")
  }
}
