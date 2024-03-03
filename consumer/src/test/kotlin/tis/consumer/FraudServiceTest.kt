package tis.consumer

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties

@SpringBootTest
@AutoConfigureStubRunner(ids = ["tis.producer:producer:+:stubs:6565"], stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class FraudServiceTest {
    @Autowired
    lateinit var fraudService: FraudService

    @Test
    fun checkFraud() {
        val response = fraudService.sendRequestToCheckFraudDetection("1234567890", 99999.0)
        assert(response.fraudCheckStatus == FraudCheckStatus.FRAUD)
        assert(response.rejectionReason == "Amount too high")
    }

    @Test
    fun checkNotFraud() {
        val response = fraudService.sendRequestToCheckFraudDetection("1234567890", 123.123)
        assert(response.fraudCheckStatus == FraudCheckStatus.OK)
        assert(response.acceptanceReason == "Amount OK")
    }
}
