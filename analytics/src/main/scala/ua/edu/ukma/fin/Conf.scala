package ua.edu.ukma.fin

import java.util.Properties


class Conf(props: Properties) {

  def get(property: String): String = {
    props.getProperty(property)
  }

  def getOrDefault(property: String, default: String): String = {
    props.getProperty(property, default)
  }
}

object Conf {
  def apply(): Conf = apply("app.properties")

  def apply(file: String): Conf = {
    val props = new Properties()
    props.load(getClass.getResourceAsStream(s"/$file"))
    new Conf(props)
  }
}
