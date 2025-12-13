package com.sorivma.dubrovskyarseny.controller

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
class IndexRestController {

    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): String {
        val resource = ClassPathResource("static/index.html")
        return resource.inputStream.use { it.readBytes().toString(StandardCharsets.UTF_8) }
    }
}