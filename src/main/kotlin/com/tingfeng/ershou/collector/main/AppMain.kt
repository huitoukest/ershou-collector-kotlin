package com.tingfeng.ershou.collector.main

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement



fun main(args: Array<String>){
    SpringApplication.run(AppMain::class.java, *args)
}

@ComponentScan(basePackages= arrayOf("com.tingfeng"))
@EnableJpaRepositories(basePackages= arrayOf("com.tingfeng.ershou.collector.dao"))//dao层对应的包路径
@EntityScan(basePackages = arrayOf("com.tingfeng.ershou.collector.entity"))//entity对应的包路径
@SpringBootApplication
@EnableTransactionManagement
class AppMain {

}