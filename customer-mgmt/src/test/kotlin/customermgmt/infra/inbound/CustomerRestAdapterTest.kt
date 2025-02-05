package customermgmt.infra.inbound

import com.ninjasquad.springmockk.MockkBean
import customermgmt.app.CreateCustomerService
import customermgmt.domain.Tier.PREMIUM
import io.mockk.every
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import shared.RootApp
import java.util.Locale
import java.util.UUID

@Tag("integration")
@WebMvcTest(CustomerRestAdapter::class)
@ContextConfiguration(classes = [RootApp::class])
class CustomerRestAdapterTest(@Autowired private val mvc: MockMvc) {

    @MockkBean
    private lateinit var createCustomer: CreateCustomerService

    @Test
    fun `should create a customer`() {
        val customerId = UUID.randomUUID()
        every { createCustomer.invoke("jane.doe@gmail.com", "Jane Doe", Locale.ENGLISH, PREMIUM) } returns customerId

        val response = mvc.perform(
            post("/customers")
                .contentType("application/json")
                .content(
                    """ { "email": "jane.doe@gmail.com", "fullName": "Jane Doe", "preferredLang": "en", "tier": "PREMIUM" } """
                )
        )

        response.andExpect(status().isCreated)
        response.andExpect(content().json(""" { "id": "$customerId" } """))
    }
}
