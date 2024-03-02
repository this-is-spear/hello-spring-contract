package tis.producer

import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeEach


open class FraoudBase {
    @BeforeEach
    fun setup() {
        RestAssuredMockMvc.standaloneSetup(FraudController())
    }
}
