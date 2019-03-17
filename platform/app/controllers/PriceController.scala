package controllers

import entities.ApartmentData
import javax.inject._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import services.DefaultPriceService

import scala.util.{Failure, Success, Try}

@Singleton
class PriceController @Inject()(
    cc: ControllerComponents,
    priceService: DefaultPriceService
) extends AbstractController(cc) {

  def predictPrice = Action { implicit request =>
    val aptForm = Form(
      mapping(
        "area" -> bigDecimal,
        "district" -> text,
        "city" -> text
      )(ApartmentData.apply)(ApartmentData.unapply)
    )
    val apt = aptForm.bindFromRequest().get

    Try(priceService.calculatePrice(apt)) match {
      case Failure(ex) => BadRequest(ex.getMessage)
      case Success(r) => Ok(r.toString)
    }
  }
}
