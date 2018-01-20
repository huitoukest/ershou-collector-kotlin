package com.tingfeng.ershou.collector

import com.tingfeng.ershou.collector.dao.CityDao
import com.tingfeng.ershou.collector.dao.SimpleItemDao
import com.tingfeng.ershou.collector.entity.City
import com.tingfeng.ershou.collector.entity.SimpleItem
import com.tingfeng.ershou.collector.main.AppMain
import org.junit.Test
import org.junit.runner.RunWith
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import org.openqa.selenium.WebElement
import java.util.concurrent.TimeUnit



@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(AppMain::class))
class DbSaveTest {

    @Autowired
    private var cityDao: CityDao? = null
    @Autowired
    private var simpleItemDao:SimpleItemDao? = null

    fun getChromeDriver(): ChromeDriver {
        //-----------------------------打开火狐浏览器------------------------------------------------
        //WebDriver my_dr = new FirefoxDriver();// 打开火狐浏览器  原生支持的浏览器，但是不支持火狐高级的版本
        //-----------------------------打开Chrome浏览器---------------------------------------------
        val file_chrome = File("E:/drivers/chromedriver.exe")
        System.setProperty("webdriver.chrome.driver", file_chrome.getAbsolutePath())
        var my_dr =  ChromeDriver()// 打开chrome浏览器
        return my_dr
    }

    fun getCitys():List<Pair<String,String>>{
        val indexUrl = "http://www.58.com/changecity.html";
        var my_dr =  getChromeDriver()// 打开chrome浏览器

        my_dr.get(indexUrl)

        var xpathAllCity = "//div[@class='content-cities']/a";
        xpathAllCity = "//div[@class='content-cities']/a[1]";

        //Thread.sleep(2000)//等待数据加载
        my_dr.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS) //显示等待数据加载
        //获取省会
        val elements = my_dr.findElements(By.xpath(xpathAllCity))
        val content =  elements.map{Pair(it.getAttribute("href"),it.text)}

        my_dr.quit()
        return content
    }

    fun getHotCitys():List<Pair<String,String>>{
        val indexUrl = "http://www.58.com/changecity.html";
        var my_dr =  getChromeDriver()// 打开chrome浏览器

        var xpathAllCity = "//div[@class='content-cities']/a";
        xpathAllCity = "//div[@id='hot']/a[@class='hot-city']";

        my_dr.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS) //显示等待数据加载
        //获取热门城市
        val elements = my_dr.findElements(By.xpath(xpathAllCity))
        val content =  elements.map{Pair(it.getAttribute("href"),it.text)}

        my_dr.quit()
        return content
    }

    fun testSaveToDb(citys:List<Pair<String,String>>){
        citys.forEach{
          val city = City(it.first,it.second,0)
            var count = cityDao!!.findById(it.first)
            if(null == count){
                cityDao!!.save(city)
            }else{
                println("${it.second} 已经存在了！")
            }
        };
    }


    @Test
    fun testSaveToDb(){
        var citys = getCitys()
        testSaveToDb(citys)

        citys = getHotCitys()
        testSaveToDb(citys)
    }

    @Test
    fun testSaveSimpleItem(){
        val startTime = System.currentTimeMillis()
        val items = SeleniumTest().getSimpleItemData()
        items.forEach{
            var count = simpleItemDao!!.findTitleById(it.id!!)
            if(null == count){
                simpleItemDao!!.save(it)
            }else{
                println("${it.id} 已经存在了！")
            }
        }
        val useTime =  ( System.currentTimeMillis() - startTime ) / 100 / 10.0
        println("useTime:$useTime")
    }
}