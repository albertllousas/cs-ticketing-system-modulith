package fixtures

import org.springframework.context.event.EventListener
import shared.DomainEvent

class TestEventListener(var receivedEvents: MutableList<DomainEvent> = mutableListOf()) {

    @EventListener
    fun handle(event: DomainEvent) {
        receivedEvents.add(event)
    }

    fun clear() {
        receivedEvents = mutableListOf()
    }
}
