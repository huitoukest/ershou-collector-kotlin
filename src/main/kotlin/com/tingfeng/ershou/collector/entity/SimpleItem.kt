package com.tingfeng.ershou.collector.entity

import org.hibernate.validator.constraints.Length
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient
import javax.transaction.TransactionScoped

@Entity
@Table(name = "simpleItem")
class SimpleItem :BaseEntity{
    @Id
    var id:String? = null;
    @Column(length = 100)
    var title:String? = null
    var price:Int ? = 0
    var city:String? = null
    @Column(length = 2048)
    var content:String? = null
    @Column(length = 255)
    var icon:String? = null
    @Column(length = 100)
    var saler:String? = null
    @Column(length = 100)
    var source :String? = null
	@Column(length = 255)
    var canCheck:Int? = 0
    @Column(length = 255)
    var url:String? = null
    var createTime:Date = Date()

    var cityId:String? = null

    constructor() {}
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleItem
        if(other.id == this.id) return true
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var tmp = url
        if(url != null && url!!.length > 50)
        {
            tmp = url!!.substring(0,50)
        }else{
            tmp = ""
        }
        return tmp.hashCode()
    }

    @Transient
    fun getUUID() :String{
        var tmp = url
        if(url != null && url!!.length > 32)
        {
            if(url!!.length > 52){
                tmp = url!!.substring(32,52)
            }else{
                tmp = url!!.substring(32)
            }
        }else {
            tmp = url
        }
        return tmp!!
    }

}