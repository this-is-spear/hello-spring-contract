package tis.hellospringcontract

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelloSpringContractApplication

fun main(args: Array<String>) {
	runApplication<HelloSpringContractApplication>(*args)
}
