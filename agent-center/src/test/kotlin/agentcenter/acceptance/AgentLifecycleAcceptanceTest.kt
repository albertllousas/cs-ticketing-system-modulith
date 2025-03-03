package agentcenter.acceptance

import io.restassured.RestAssured
import io.kotest.matchers.shouldBe
import io.restassured.http.ContentType.JSON
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import shared.AgentConnected
import shared.AgentCreated
import shared.AgentDisconnected

@Tag("acceptance")
class AgentLifecycleAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `should complete an agent lifecycle`() {
        val agentId: String = RestAssured
            .given()
            .body(
                """ { 
                    |"email": "jane.doe@gmail.com", 
                    |"fullName": "Jane Doe", 
                    |"skills": ["Technical background"], 
                    |"languages": ["EN"]
                    |} """.trimMargin()
            )
            .contentType(JSON)
            .accept(JSON)
            .port(servicePort)
            .`when`()
            .post("/agents")
            .then()
            .log().all()
            .assertThat().statusCode(201)
            .extract().path("id")

        RestAssured
            .given()
            .port(servicePort)
            .`when`()
            .patch("/agents/$agentId/online")
            .then()
            .assertThat()
            .statusCode(200)

        RestAssured
            .given()
            .port(servicePort)
            .`when`()
            .patch("/agents/$agentId/offline")
            .then()
            .assertThat()
            .statusCode(200)

        testEventListener.receivedEvents.map { it::class } shouldBe listOf(
            AgentCreated::class,
            AgentConnected::class,
            AgentDisconnected::class
        )
    }
}
