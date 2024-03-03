package tis.consumer

import com.fasterxml.jackson.annotation.JsonProperty

data class FraudCheckRequest(
    @JsonProperty("client.id")
    val clientId: String,
    val loanAmount: Double
)

