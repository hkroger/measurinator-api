package com.measurinator.api.dao

import com.measurinator.api.dao.entities.Measurement

/**
  * Created by hkroger on 10/4/2017.
  */
trait DAOConversions {
  implicit def measurement2daoMeasurement(m: com.measurinator.api.entities.Measurement): Measurement = {
    Measurement(
      m.locationId,
      m.yearMonth,
      m.id,
      m.measurement,
      m.signalStrength.bigDecimal,
      m.voltage.bigDecimal
    )
  }
}
