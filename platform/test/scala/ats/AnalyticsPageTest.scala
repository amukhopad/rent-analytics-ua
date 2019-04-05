package ats

import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.HtmlUnit
import org.scalatest.{FlatSpec, Matchers}
import java.util.concurrent.TimeUnit

import org.openqa.selenium.WebDriver
import org.scalatest.time.{Seconds, Span}

class AnalyticsPageTest extends FlatSpec with Matchers with HtmlUnit with Eventually {
  val host = "http://rentanalytics.amukhopad.com"

  val selectMetro = "metro"
  val selectDist = "district"
  val selectWall = "wallType"

  "Rent analytics name" should "have the correct title" in {
    go to host

    pageTitle should be("Rent Analytics Kyiv")
  }

  "Count " should "be present after submit" in {
    go to host


    selectDropDown(selectDist, "Днепровский")

    selectDropDown(selectMetro, "Дарниця")
    selectDropDown(selectWall, "кирпич")

    click on id("submit")

    waitForPageToLoad()


    find(className("count")).get should not be None
  }

  "Metro station selector" should "not be set when district is not selected" in {
    go to host

    find(id("select2-metro-container")).get.text should be("Оберіть район")
  }

  it should "be set and populated when district is selected" in {
    go to host
    waitForPageToLoad()

    selectDropDown(selectDist, "Днепровский")

    find(id("select2-district-container")).get.text should be("Днепровский")
    find(id("select2-metro-container")).get.text should be("--- немає метро поруч ---")
  }


  def waitForPageToLoad(sec: Int = 30)(implicit webDriver: WebDriver): Unit =
    webDriver.manage.timeouts.pageLoadTimeout(sec, TimeUnit.SECONDS)

  def selectDropDown(listId: String, option: String)(implicit webDriver: WebDriver): Unit = {
    println(executeScript(s"jQuery('#${listId}').select2('open')"))
    implicitlyWait(Span(10, Seconds))
    click on xpath(s"//li[substring(@id,string-length(@id) - string-length('${option}') + 1) = '${option}']")
  }

  def numberOfOptions(id: String): Int =
    findAll(xpath(s"//select[@id='${id}']/option")).length
}
