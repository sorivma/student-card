package com.sorivma.dubrovskyarseny.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix ="student")
data class StudentProperties(
    var fullName: String = "",
    var group: String = "",
    var institute: String = "",
)
