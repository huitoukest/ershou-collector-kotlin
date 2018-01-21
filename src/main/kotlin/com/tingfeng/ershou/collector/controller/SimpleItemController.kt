package com.tingfeng.ershou.collector.controller


import com.tingfeng.ershou.collector.dao.SimpleItemDao
import com.tingfeng.ershou.collector.dto.Pager
import com.tingfeng.ershou.collector.entity.SimpleItem
import com.tingfeng.ershou.collector.service.SimpleItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Controller
@RestController
class SimpleItemController() {
    @Autowired
    lateinit var simpleItemDao: SimpleItemDao
    @Autowired
    lateinit var simpleItemService: SimpleItemService

    @RequestMapping("/getItems")
    fun searchItem(pager: Pager<SimpleItem>,simpleItem: SimpleItem,keyWordsString:String?,minPrice:Int?,maxPrice:Int?): Pager<SimpleItem> {
       val keyWords = keyWordsString?.split(",")
       val pageInfo = this.simpleItemService.findPagers(simpleItem,pager,keyWords,minPrice,maxPrice)
        return  pageInfo!!;
    }

}