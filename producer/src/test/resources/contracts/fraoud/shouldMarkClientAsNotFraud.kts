package fraud

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    request {
        method = PUT
        url = url("/fraudcheck")
        body = body(
            "client.id" to value(consumer(regex("[0-9]{10}")), producer("1234567890")),
            // 0 ~ 9999 까지의 double 형
            "loanAmount" to value(consumer(regex("[0-9]{1,4}(\\.\\d{1,3})?")), producer(9999.0))
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
