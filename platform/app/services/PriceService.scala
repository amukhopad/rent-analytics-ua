package services

import entities.ApartmentData

trait PriceService {
  def calculatePrice(apt: ApartmentData): Double
}
