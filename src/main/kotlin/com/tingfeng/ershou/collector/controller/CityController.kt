package com.tingfeng.ershou.collector.controller

import com.tingfeng.ershou.collector.dao.CityDao
import com.tingfeng.ershou.collector.entity.City
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CityController{

    @Autowired
    private lateinit var cityDao:CityDao

    @RequestMapping("/getCitys")
    fun getCitys(city:City):List<City>{
        return cityDao.findAllByOrderBySortValueDesc()?:ArrayList<City>()
    }
}