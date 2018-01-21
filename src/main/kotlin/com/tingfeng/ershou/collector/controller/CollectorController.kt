package com.tingfeng.ershou.collector.controller

import com.tingfeng.ershou.collector.service.CollectorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CollectorController {

    @Autowired
    private lateinit var collectorService:CollectorService

    @RequestMapping("/start")
    fun start(title:String="华为MATE10",minPrice:Int=0,maxPrice:Int=30000,threadSize:Int = 2,hot:Int = 1):Boolean {
        return collectorService.start(title,minPrice,maxPrice,threadSize,hot)
    }
}