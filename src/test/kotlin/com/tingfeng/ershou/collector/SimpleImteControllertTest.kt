package com.tingfeng.ershou.collector

import com.google.gson.Gson
import com.tingfeng.ershou.collector.controller.SimpleItemController
import com.tingfeng.ershou.collector.dto.Pager
import com.tingfeng.ershou.collector.entity.SimpleItem
import com.tingfeng.ershou.collector.main.AppMain
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(AppMain::class))
class SimpleImteControllertTest {
    @Autowired
    lateinit var simpleItemControllelr: SimpleItemController


    @Test
    fun testFindPage(){
        val item = SimpleItem()
        item.canCheck = null
        item.title = "米"
        val keyWords = "顶配,"
        val result = simpleItemControllelr.searchItem(Pager(),item,keyWords)
        println(Gson().toJson(result))
    }

}