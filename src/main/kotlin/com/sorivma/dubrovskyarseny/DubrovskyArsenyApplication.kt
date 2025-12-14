package com.sorivma.dubrovskyarseny

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class DubrovskyArsenyApplication

fun main(args: Array<String>) {
    runApplication<DubrovskyArsenyApplication>(*args)
}
