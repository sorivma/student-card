package com.sorivma.dubrovskyarseny.controller

import com.sorivma.dubrovskyarseny.configuration.StudentProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDate

@Controller
class IndexRestController(
    private val student: StudentProperties
) {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("student", student)
        model.addAttribute("year", LocalDate.now().year)
        return "index"
    }
}