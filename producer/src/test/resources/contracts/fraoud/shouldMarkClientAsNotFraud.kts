package fraud

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = PUT
        url = url("/fraudcheck")
        body = body(
            "client.id" to value(consumer(regex("[0-9]{10}")), producer("1234567890")),
            "loanAmount" to 123.123
        )
        headers {
            contentType = "application/json"
        }
    }
    response {
        status = OK
        body = body(
            "fraudCheckStatus" to "OK",
            "acceptance.reason" to "Amount OK"
        )
        headers {
            contentType = "application/json"
        }
    }
}
