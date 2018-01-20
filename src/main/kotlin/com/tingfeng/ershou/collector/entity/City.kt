package com.tingfeng.ershou.collector.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="city")
class City(@Id var id:String?,
           var name:String?,
           var sortValue:Int? = 0):BaseEntity() {
    constructor(): this(null,null,null){}
}