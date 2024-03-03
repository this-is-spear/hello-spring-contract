package tis.consumer

import com.fasterxml.jackson.annotation.JsonProperty

data class FraudCheckResponse(
    val fraudCheckStatus: FraudCheckStatus,
    @JsonProperty("rejection.reason")
    val rejectionReason: String?,
    @JsonProperty("acceptance.reason")
    val acceptanceReason: String?
)
