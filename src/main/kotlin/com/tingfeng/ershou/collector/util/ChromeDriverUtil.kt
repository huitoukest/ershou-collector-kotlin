package com.tingfeng.ershou.collector.util

import com.tingfeng.ershou.collector.dto.ChromeDriverInfo
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * 模拟一个ChromDriver的线程池，最多50个线程
 */
object ChromeDriverUtil {
    public var maxSize = 10
    private val runingCount = AtomicInteger (0)
    private val pool = ArrayList<ChromeDriverInfo>(25)//当的ChromDriver
    private val removeEletemts = ArrayList<ChromeDriverInfo>(25)
    init{
        val file_chrome = File("E:/drivers/chromedriver.exe")
        System.setProperty("webdriver.chrome.driver", file_chrome.getAbsolutePath())
        Thread{
           while(true) {
               synchronized(ChromeDriverUtil) {
                   try {
                       removeEletemts.clear()
                       for (driver in pool) {
                           try {
                               val useTime = System.currentTimeMillis() - driver.updateTime
                               if ((useTime > 15000 && driver.isUse == false) || useTime > 60000) {//60秒钟没有使用，或者10秒闲置则停止
                                   removeEletemts.add(driver)
                               }
                           } catch (e: Exception) {
                               e.printStackTrace()
                           }
                       }
                       for (driver in removeEletemts) {
                           try {
                               driver.chromeDrier!!.quit()
                               pool.remove(driver)
                               runingCount.decrementAndGet()
                           } catch (e: Exception) {
                               e.printStackTrace()
                           }
                       }
                       Thread.sleep(1000)
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }
               }
           }
        }.start()
        println("ChromeDriverUtil init...")
    }

    fun closeChromeDriver(driver:ChromeDriver){
        synchronized(ChromeDriverUtil){
            for(item in pool)
            {
                if(item.chromeDrier == driver){
                    item.isUse = false
                    break
                }
            }
        }
    }

    fun getChromeDriver(isHeadLess : Boolean,getCount:Int=0): ChromeDriver {
            if(getCount > 120){
                throw Exception("out time:" + 120 * 5 + "second");
            }
             if(runingCount.get() > maxSize){
                        Thread.sleep(5000)
                        return getChromeDriver(isHeadLess,getCount + 1)
             }
            var my_dr: ChromeDriver = getUseChromDriver(isHeadLess)
            return my_dr
    }

    private  fun getUseChromDriver(isHeadLess : Boolean): ChromeDriver{
        var my_dr: ChromeDriver? = null
        if(pool.size <= 0 ){
            my_dr = getNewChromeDriver(isHeadLess)
            val driver = ChromeDriverInfo()
            driver.chromeDrier = my_dr
            driver.isHeadLess = isHeadLess
            driver.isUse = true
            pool.add(driver)
            runingCount.incrementAndGet()
        }else{
            for(driver in pool)
            {
                if(driver.isHeadLess == isHeadLess && !driver.isUse) {
                    my_dr = driver.chromeDrier
                    driver.updateTime = System.currentTimeMillis()
                    driver.isUse = true
                    break
                }

            }
            if(my_dr == null && pool.size < maxSize){
                my_dr = getNewChromeDriver(isHeadLess)
                val driver = ChromeDriverInfo()
                    driver.chromeDrier = my_dr
                    driver.isHeadLess = isHeadLess
                    driver.isUse = true
                    pool.add(driver)
                    runingCount.incrementAndGet()
            }
        }
        return my_dr!!
    }

    private fun getNewChromeDriver(isHeadLess : Boolean): ChromeDriver {
        //-----------------------------打开Chrome浏览器---------------------------------------------
        var my_dr: ChromeDriver? = null
        if (isHeadLess) {
            val chromeOptions = ChromeOptions()
//        设置为 headless 模式 （必须）
            chromeOptions.addArguments("--headless")
            chromeOptions.addArguments("--disable-gpu")
            chromeOptions.addArguments("lang=zh_CN.UTF-8")
//        设置浏览器窗口打开大小  （非必须）
            chromeOptions.addArguments("--window-size=1920,1080")
             my_dr = ChromeDriver(chromeOptions)// 打开chrome浏览器
        } else {
            my_dr = ChromeDriver()
        }
        return my_dr!!
    }

}