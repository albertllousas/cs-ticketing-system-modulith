package tickets.acceptance

import io.restassured.RestAssured
import io.kotest.matchers.shouldBe
import io.restassured.http.ContentType.JSON
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import shared.CustomerCreated
import shared.TicketCreated
import java.util.UUID

@Tag("acceptance")
class TicketLifecycleAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `should complete an agent lifecycle`() {
        RestAssured
            .given()
            .body(
                """ { "title": "Access", "description": "I can't access to my account", "type": "ISSUE" } """
            )
            .header("customerId", UUID.randomUUID().toString())
            .contentType(JSON)
            .accept(JSON)
            .port(servicePort)
            .`when`()
            .post("/tickets")
            .then()
            .log().all()
            .assertThat().statusCode(201)

        testEventListener.receivedEvents.map { it::class } shouldBe listOf(
            TicketCreated::class,
        )
    }
}
