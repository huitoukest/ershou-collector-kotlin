package com.tingfeng.ershou.collector.service

interface CollectorService {
    fun start(title:String="华为MATE10",minPrice:Int=0,maxPrice:Int=30000,threadSize:Int = 2,hot:Int = 1):Boolean
    fun pause():Unit
    fun stop():Unit
}