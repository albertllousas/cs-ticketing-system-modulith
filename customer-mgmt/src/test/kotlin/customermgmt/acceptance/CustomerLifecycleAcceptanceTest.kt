package customermgmt.acceptance

import io.restassured.RestAssured
import io.kotest.matchers.shouldBe
import io.restassured.http.ContentType.JSON
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import shared.AgentConnected
import shared.AgentCreated
import shared.AgentDisconnected
import shared.CustomerCreated

@Tag("acceptance")
class CustomerLifecycleAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `should complete an agent lifecycle`() {
        RestAssured
            .given()
            .body(
                """ { "email": "jane.doe@gmail.com", "fullName": "Jane Doe", "preferredLang": "en", "tier": "PREMIUM" } """
            )
            .contentType(JSON)
            .accept(JSON)
            .port(servicePort)
            .`when`()
            .post("/customers")
            .then()
            .log().all()
            .assertThat().statusCode(201)

        testEventListener.receivedEvents.map { it::class } shouldBe listOf(
            CustomerCreated::class,
        )
    }
}
