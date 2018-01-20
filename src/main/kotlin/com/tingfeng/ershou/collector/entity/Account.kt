package com.tingfeng.ershou.collector.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="account")
data class Account(@Id @GeneratedValue var id:Long?,
                   var name:String?,
                   var money:Double?
                   ) :BaseEntity() {
    constructor(): this(null,null,null){}

}