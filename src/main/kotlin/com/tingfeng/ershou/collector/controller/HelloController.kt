package com.tingfeng.ershou.collector.controller

import com.tingfeng.ershou.collector.service.HelloService
import org.apache.catalina.servlet4preview.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletResponse


@RestController
class HelloController(@Autowired val helloService: HelloService) {


    @RequestMapping("/hello-service")
    fun helloKotlinService(): String {
        return helloService.getHello()
    }

    @RequestMapping("/")
    fun home(resp:HttpServletResponse ):Unit {
        resp.sendRedirect("/spring/page/index.html")
    }
    @RequestMapping("/hello")
    fun helloo1():String{
        return "Hello 01!"
    }
}