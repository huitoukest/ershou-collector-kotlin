package com.tingfeng.ershou.collector

import com.tingfeng.ershou.collector.controller.CollectorController
import com.tingfeng.ershou.collector.dao.CityDao
import com.tingfeng.ershou.collector.dao.SimpleItemDao
import com.tingfeng.ershou.collector.main.AppMain
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(AppMain::class))
class WebCollectorTest {

    @Autowired
    private lateinit var collectorController: CollectorController

    @Test
    fun testStart(){
       val result =  collectorController.start("华为MATE10",1800,15000,5,1)
        System.`in`.read()//加入该代码，让主线程不挂掉
        println(result)
    }


}
