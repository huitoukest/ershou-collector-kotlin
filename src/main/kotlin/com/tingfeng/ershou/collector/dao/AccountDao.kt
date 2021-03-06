package com.tingfeng.ershou.collector.dao

import com.tingfeng.ershou.collector.entity.Account
import org.springframework.stereotype.Repository

@Repository
 interface AccountDao:BaseDao<Account, Long> {

    fun findByName(name:String):List<Account>
}
