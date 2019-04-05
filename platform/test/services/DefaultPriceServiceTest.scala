package services

import entities.ApartmentData
import model.ApartmentRepository
import org.scalatest.FlatSpec
import play.api.Configuration
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar


class DefaultPriceServiceTest extends FlatSpec with MockitoSugar {
  val conf = mock[Configuration]
  val repo = mock[ApartmentRepository]

  val data = ApartmentData(66, "dist", "metro", "brick", "Kyiv")

  val service = new DefaultPriceService(conf, repo)

  "Service" should "call repository" in {
    service.calculatePrice(data)

    verify(repo).retrieveModel(data)
  }
}
