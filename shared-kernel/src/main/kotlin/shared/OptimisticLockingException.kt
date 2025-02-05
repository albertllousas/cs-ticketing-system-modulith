package shared

import java.util.UUID

data class OptimisticLockingException(val entity: String, val id: UUID): RuntimeException(
    "Failed to update $entity with id = '$id', possibly due to a concurrent modification"
)