package com.tingfeng.ershou.collector.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
class HelloController() {

    @RequestMapping("/")
    fun home(resp:HttpServletResponse ):Unit {
        resp.sendRedirect("/ershou/page/index.html")
    }
    @RequestMapping("/hello")
    fun helloo1():String{
        return "Hello 01!"
    }
}