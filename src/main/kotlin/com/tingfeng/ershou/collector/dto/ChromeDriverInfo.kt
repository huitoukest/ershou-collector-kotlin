package com.tingfeng.ershou.collector.dto

import org.openqa.selenium.chrome.ChromeDriver

class ChromeDriverInfo {
    public  var chromeDrier: ChromeDriver? = null
    public  var isUse:Boolean = false
    public  var updateTime:Long = System.currentTimeMillis()
    public var isHeadLess:Boolean = true

    constructor(){}

    constructor(chromeDrier: ChromeDriver, isUse: Boolean, updateTime: Long) {
        this.chromeDrier = chromeDrier
        this.isUse = isUse
        this.updateTime = updateTime
    }

    constructor(chromeDrier: ChromeDriver?, isUse: Boolean, updateTime: Long, isHeadLess: Boolean) {
        this.chromeDrier = chromeDrier
        this.isUse = isUse
        this.updateTime = updateTime
        this.isHeadLess = isHeadLess
    }


}