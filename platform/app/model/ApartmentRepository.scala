package model

import entities.ApartmentData
import org.apache.spark.ml.PipelineModel

trait ApartmentRepository {
  def retrieveModel(apt: ApartmentData): PipelineModel
}
