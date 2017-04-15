package com.measurinator.api.dao.entities

import java.util.UUID

import com.datastax.driver.mapping.annotations.Table

@Table(name = "measurements")
case class Measurement(locationId: Int,
                       yearMonth: String,
                       id: UUID,
                       measurement: java.lang.Double,
                       signalStrength: java.math.BigDecimal,
                       voltage: java.math.BigDecimal)
