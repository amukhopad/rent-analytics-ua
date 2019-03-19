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
    val apartmentForm = bindApartmentForm
    if (apartmentForm.errors.isEmpty) {
      val response = priceService.calculatePrice(apartmentForm.get)
      Try(response) match {
        case Failure(ex) =>
          ex.printStackTrace()
          BadRequest(views.html.index(
          errors = Seq(ex.getMessage)))
        case Success(result) => Ok(views.html.index(
          predictedPrice = f"$result%.2f"))
      }
    } else {
      BadRequest(views.html.index(
        errors = apartmentForm.errors.flatMap(_.messages)))
    }
  }

  private def bindApartmentForm(implicit request: Request[_]): Form[ApartmentData] = {
    val aptForm = Form(
      mapping(
        "area" -> bigDecimal,
        "district" -> text,
        "metro" -> text,
        "wallType" -> text,
        "city" -> text
      )(ApartmentData.apply)(ApartmentData.unapply)
    )
    aptForm.bindFromRequest()
  }


}
