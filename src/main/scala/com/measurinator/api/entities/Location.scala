package com.measurinator.api.entities

import java.util.UUID

case class Location(clientId: UUID,
                    id: Int,
                    quantity: String,
                    description: String)
