package com.measurinator.api.dao.accessors
import java.util.UUID

import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, Query}
import com.measurinator.api.dao.entities.LocationByClient

@Accessor
trait LocationsByClientAccessor {
  @Query("SELECT * FROM locations_by_client WHERE client_id = ?")
  def findByClient(clientId: UUID): Result[LocationByClient]
}
