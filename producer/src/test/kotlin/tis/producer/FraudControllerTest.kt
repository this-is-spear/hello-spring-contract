package tis.producer

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.toomuchcoding.jsonassert.JsonAssertion
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FraudControllerTest {
    @BeforeEach
    fun setup() {
        RestAssuredMockMvc.standaloneSetup(FraudController())
    }
    @Test
    @Throws(Exception::class)
    fun validate_shouldMarkClientAsFraud() {
        // given:
        val request: MockMvcRequestSpecification = RestAssuredMockMvc.given()
            .header("Content-Type", "application/json")
            .body("{\"client.id\":\"1234567890\",\"loanAmount\":99999}")

        // when:
        val response = RestAssuredMockMvc.given().spec(request)
            .put("/fraudcheck")

        // then:
        Assertions.assertThat(response.statusCode()).isEqualTo(200)
        Assertions.assertThat(response.header("Content-Type")).matches("application/json")
        // and:
        val asString = response.getBody().asString()
        println(asString)
        val parsedJson: DocumentContext = JsonPath.parse(asString)
        JsonAssertion.assertThatJson(parsedJson).field("['fraudCheckStatus']").matches("[A-Z]{5}")
        JsonAssertion.assertThatJson(parsedJson).field("['rejection.reason']").isEqualTo("Amount too high")
    }
}
