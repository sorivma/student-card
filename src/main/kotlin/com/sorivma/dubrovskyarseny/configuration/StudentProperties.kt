package com.sorivma.dubrovskyarseny.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix ="student")
data class StudentProperties(
    var fullName: String = "",
    var fullNameLink: String? = null,
    var group: String = "",
    var groupLink: String? = null,
    var institute: String = "",
    var instituteLink: String? = null
)
