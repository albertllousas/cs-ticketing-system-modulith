package shared

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("acceptance")
class RootAppLoadAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `should load the app`() {
        // this test will fail if app fails to load with all the modules and dependencies
    }
}
