package com.tingfeng.ershou.collector.dao

import com.tingfeng.ershou.collector.entity.SimpleItem
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface SimpleItemDao :BaseDao<SimpleItem, Long> {
    fun findById(id:String): SimpleItem
    fun countById(id: String): Long
	@Query("delete from SimpleItem item where item.createTime < :date")
	fun deleteOldItem(@Param("date")date:Date):Long
}
