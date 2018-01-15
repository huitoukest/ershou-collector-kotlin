package com.tingfeng.ershou.collector.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


@Configuration
class MvcConfigurer : WebMvcConfigurerAdapter() {

   /* override fun addViewControllers(registry: ViewControllerRegistry) {
        //registry.addViewController("/").setViewName("/web-inf/page/index.html")
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE)
    }*/

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        super.configurePathMatch(configurer)
        configurer.setUseSuffixPatternMatch(false)//当此参数设置为true的时候，那么/user.html，/user.aa，/user.*都能是正常访问的。
    }
}