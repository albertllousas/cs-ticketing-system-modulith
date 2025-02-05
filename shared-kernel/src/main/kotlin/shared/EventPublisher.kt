package shared

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

interface EventPublisher {
    fun publish(event: DomainEvent)
}

@Component
class SpringInMemoryEventPublisher(private val publisher: ApplicationEventPublisher) : EventPublisher {

    override fun publish(event: DomainEvent) = publisher.publishEvent(event)
}
