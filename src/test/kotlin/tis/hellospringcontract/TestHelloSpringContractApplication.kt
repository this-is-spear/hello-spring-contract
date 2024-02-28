package tis.hellospringcontract

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestHelloSpringContractApplication

fun main(args: Array<String>) {
	fromApplication<HelloSpringContractApplication>().with(TestHelloSpringContractApplication::class).run(*args)
}
