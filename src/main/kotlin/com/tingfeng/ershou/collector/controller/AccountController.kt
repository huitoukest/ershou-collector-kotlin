package com.tingfeng.ershou.collector.controller

import org.springframework.stereotype.Controller
import com.tingfeng.ershou.collector.Entity.Account
import com.tingfeng.ershou.collector.dao.AccountDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/account")
class AccountController {
    @Autowired
    lateinit var accountDao: AccountDao

    @ResponseBody
    @RequestMapping(value = "/list")
    fun getAccounts(): List<Account> {
        return accountDao.findAll().toList()
    }

    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
    fun getAccountById(@PathVariable("id") id: Long): Account {
        return accountDao.findOne(id)
    }


    @ResponseBody
    @RequestMapping(value = "/search", method = arrayOf(RequestMethod.GET,RequestMethod.POST))
    fun getAccountByName(name: String): List<Account> {
        return accountDao.findByName(name)
    }

    @RequestMapping(value = "/{id}")
    fun updateAccount(@PathVariable("id") id: Long, @RequestParam(value = "name", required = true) name: String,
                      @RequestParam(value = "money", required = true) money: Double): String {
        val account =  Account()
        account.money = money
        account.name = name
        account.id = id
       // val account1 = accountDao.saveAndFlush<Account>(account)

       // return account1.toString()
        return ""

    }

    @RequestMapping(value = "/add")
    fun addAccount(@RequestParam(value = "name") name: String,
                    @RequestParam(value = "money") money: Double): String {
        val account = Account()
        account.money = money
        account.name = name
        val account1 = accountDao.save(account)
        return account1.toString()

    }
}