package com.tingfeng.ershou.collector.service

interface CollectorService {
    fun start(title:String="华为MATE10",minPrice:Int=0,maxPrice:Int=30000,threadSize:Int = 2,hot:Int = 1):Boolean
    fun pause():Unit
    fun stop():Unit
    /**
     * 获取当前状态，0=停止,1=运行,2=暂停,3=正在停止
     */
    fun getStatus(): Int
}