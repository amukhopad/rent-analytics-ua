package model

import entities.ApartmentData
import org.apache.spark.ml.tuning.CrossValidatorModel

trait ApartmentRepository {
  def retrieveModel(apt: ApartmentData): CrossValidatorModel
}
