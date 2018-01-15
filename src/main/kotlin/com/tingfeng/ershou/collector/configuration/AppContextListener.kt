package com.tingfeng.ershou.collector.configuration

import com.tingfeng.ershou.collector.service.CollectorService
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class AppContextListener : ApplicationListener<ContextRefreshedEvent> {

    companion object {
        var collectorService: CollectorService? = null
    }

    var applicationContext: ApplicationContext? = null

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        applicationContext = event.applicationContext
        collectorService = (applicationContext?.getBean("CollectorService")) as CollectorService

    }



}
