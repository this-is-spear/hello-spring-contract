package tis.producer

import com.fasterxml.jackson.annotation.JsonProperty


class LoanRequest {
    @JsonProperty("client.id")
    var clientId: String? = null
    var loanAmount: Long? = null
}
