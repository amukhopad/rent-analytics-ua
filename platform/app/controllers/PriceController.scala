package controllers

import entities.ApartmentData
import javax.inject._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.data.format.Formats._
import services.DefaultPriceService

import scala.util.{Failure, Success, Try}

@Singleton
class PriceController @Inject()(
    cc: ControllerComponents,
    priceService: DefaultPriceService
) extends AbstractController(cc) {

  def predictPrice = Action { implicit request =>
    val form = bindApartmentForm
    if (form.errors.isEmpty) {
      Try(priceService.calculatePrice(form.get)) match {
        case Failure(ex) =>
          ex.printStackTrace()
          BadRequest(views.html.index(form.data,
            errors = Seq(ex.getMessage)))
        case Success(result) =>
          Ok(views.html.index(form.data,
            predictedPrice = f"$result%.2f"))
      }
    } else {
      BadRequest(views.html.index(
        errors = form.errors.flatMap(_.messages)))
    }
  }

  private def bindApartmentForm(
      implicit request: Request[_]): Form[ApartmentData] = {
    val aptForm = Form(
      mapping(
        "area" -> of(doubleFormat),
        "district" -> text,
        "metro" -> text,
        "wallType" -> text,
        "city" -> text
      )(ApartmentData.apply)(ApartmentData.unapply)
        verifying("Помилка при заповнені площі", _.area > 0)
    )
    aptForm.bindFromRequest()
  }
}
