package com.tingfeng.ershou.collector.dao

import com.tingfeng.ershou.collector.entity.City
import org.springframework.stereotype.Repository

@Repository
interface CityDao :BaseDao<City, Long> {

    fun findByNameOrderBySortValue(name:String):List<City>
    fun findById(id:String):City
}