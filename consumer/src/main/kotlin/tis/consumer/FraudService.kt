package tis.consumer

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FraudService(
    builder: RestTemplateBuilder
) {
    private var port = 6565
    private val restTemplate: RestTemplate = builder.build()

    fun sendRequestToCheckFraudDetection(request: FraudCheckRequest): FraudCheckResponse {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

        val response =
            restTemplate.exchange(
                "http://localhost:$port/fraudcheck",
                HttpMethod.PUT,
                HttpEntity(request, headers),
                FraudCheckResponse::class.java
            )
        return response.body!!
    }
}
