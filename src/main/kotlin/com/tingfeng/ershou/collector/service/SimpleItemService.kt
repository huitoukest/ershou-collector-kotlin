package com.tingfeng.ershou.collector.service

import com.tingfeng.ershou.collector.dto.Pager
import com.tingfeng.ershou.collector.entity.SimpleItem
import org.springframework.data.domain.Page

interface SimpleItemService {

    fun findPagers(simpleItem:SimpleItem,pager: Pager<SimpleItem>,keyWords:List<String>?,minPrice:Int?,maxPrice:Int?): Pager<SimpleItem>

}