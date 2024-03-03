package tis.consumer

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero

data class FraudCheckRequest(
    @JsonProperty("client.id")
    @field:Pattern(regexp = "[0-9]{10}")
    val clientId: String,
    @field:Max(9999)
    @field:PositiveOrZero
    val loanAmount: Double
)

