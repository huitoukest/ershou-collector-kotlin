package com.tingfeng.ershou.collector.dao

import com.tingfeng.ershou.collector.entity.SimpleItem
import org.springframework.stereotype.Repository

@Repository
interface SimpleItemDao : BaseDao<SimpleItem, String> {
    fun findById(id:String): SimpleItem
    fun findTitleById(id: String): String
}
