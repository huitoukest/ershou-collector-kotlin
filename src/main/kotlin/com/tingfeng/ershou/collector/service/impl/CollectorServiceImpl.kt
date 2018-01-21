package com.tingfeng.ershou.collector.service.impl

import com.google.gson.Gson
import com.tingfeng.ershou.collector.dao.CityDao
import com.tingfeng.ershou.collector.dao.SimpleItemDao
import com.tingfeng.ershou.collector.entity.City
import com.tingfeng.ershou.collector.entity.SimpleItem
import com.tingfeng.ershou.collector.service.CollectorService
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class CollectorServiceImpl : CollectorService {

    companion object {
        var isPause  = false
        var isStop = false
        var isRun = false
        var isCityUpdated = false
        var threadPool = Executors.newFixedThreadPool(20)
    }

    @Autowired
    lateinit var cityDao: CityDao
    @Autowired
    lateinit var simpleItemDao: SimpleItemDao


    override @Synchronized  fun pause() {
        if(!isPause) {
            isPause = true
        }
    }

    override @Synchronized  fun stop() {
       if(!isStop) {
           isStop = true

           threadPool.shutdown()
       }
    }


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
        my_dr.get(indexUrl)

        var xpathAllCity = "//div[@id='hot']/a[@class='hot-city']";
        my_dr.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS) //显示等待数据加载
        //获取热门城市
        val elements = my_dr.findElements(By.xpath(xpathAllCity))
        val content =  elements.map{Pair(it.getAttribute("href"),it.text)}

        my_dr.quit()
        return content
    }

    fun testSaveToDb(citys:List<Pair<String,String>>,isHot:Boolean = false){
        citys.forEach{
            val city = City(it.first,it.second,0)
            var count = cityDao!!.findById(it.first)
            if(null == count){
                if(isHot) city.sortValue = 1
                cityDao!!.save(city)
            }else{
                println("${it.second} 已经存在了！")
            }
        };
    }


    fun getSimpleItemData(maxWait:Long = 15,url:String):List<SimpleItem>{
        var content = StringBuffer(5000)
        var my_dr =  getChromeDriver()// 打开chrome浏览器
        my_dr.get(url)
        my_dr.manage().timeouts().implicitlyWait(maxWait, TimeUnit.SECONDS) //显式等待数据加载
        //得到当前页面的item信息
        var xpathAllCity = "//div[@class='infocon']/table[@class='tbimg']//tr";//获取所有的item行
        val elements = my_dr.findElements(By.xpath(xpathAllCity))
        val items = elements.map {
            try{
                val item = SimpleItem()
                item.icon = it.findElement(By.xpath("./td[@class='img']/a/img"))?.getAttribute("src")
                val show = it.findElement(By.xpath("./td[@class='t']"))

                item.url = show.findElement(By.xpath("./a[@class='t']"))?.getAttribute("href")
                item.title = show.findElement(By.xpath("./a[@class='t']"))?.text

                val price = show.findElement(By.xpath("./span[@class='pricebiao']/span[@class='price']"))?.text

                if(price == null)
                {
                    item.price = -1
                }else if(price == "面议"){
                    item.price = -2;
                }else{
                    try {
                        item.price = price.toInt()
                    }catch (e:Exception){
                        item.price = -3
                        e.printStackTrace()
                    }
                }
                item.content = show.findElement(By.xpath("./span[@class='desc']"))?.text
                try {
                    item.city = show.findElement(By.xpath("./span[@class='fl']"))?.text
                }catch (e:Exception){
                    e.printStackTrace()
                }
                val tc = it.findElement(By.xpath("./td[@class='tc']"))
                val sellerInfo = tc.findElements(By.xpath(".//p[@class='name_add']"))
                try {
                    if(sellerInfo.size > 0){
                        item.saler = sellerInfo.get(0).text
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
                try {
                    if(sellerInfo.size > 1){
                        item.source = sellerInfo.get(1).text
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
                try {
                    val check = tc.findElements(By.xpath(".//p[@class='zhijian']"))
                    if(check.size > 0){
                        item.canCheck  = 1
                    }else{
                        item.canCheck = 0
                    }
                }catch (e:Exception){
                    item.canCheck = 0
                }

                if(null != item.url)
                    item.id = item.getUUID()

                content.append("\n")
                content.append(Gson().toJson(item))
                item
            }catch (e:Exception){
                SimpleItem()
            }
        }.filter{ it.id != null }
        my_dr.quit()
        println(content)
        return items
    }

    override @Synchronized fun start(title: String, minPrice: Int, maxPrice: Int,threadSize:Int,hot:Int):Boolean {
        try {
            var size = 1
            if(threadSize < 1) size = 1
            if(threadSize > 15) size = 15

            if(!isRun){
                isRun = true
                if(!isCityUpdated){
                    threadPool.submit(getCityCollectorThread())
                    isCityUpdated = true
                }

                if(hot == 1) {
                    val hotCitys = this.cityDao.findBySortValue(1)
                    hotCitys.forEach {
                        if (!isStop) {
                            threadPool.submit(getItemCollectorThread(20 * hotCitys.size.toLong(), it, title, minPrice, maxPrice))
                        }
                    }
                }
                if(hot == 0){
                    val  citys = this.cityDao.findBySortValue(0)
                    citys.forEach {
                        if(!isStop) {
                            threadPool.submit(getItemCollectorThread(20 * threadSize.toLong(), it, title, minPrice, maxPrice))
                        }
                    }
                }
            }

            isPause = false
            isStop = false
        }catch (e:Exception){
            return false
        }
        return true
    }

    fun getSearchUrl(city:String="http://bj.58.com",pageNo:Int = 0,key :String ="",cmcskey:String ="", minPrice: Int, maxPrice: Int):String{
        return "$city/shouji/pn$pageNo/?key=$key&cmcskey=$cmcskey&minprice=${minPrice}_$maxPrice";
    }

    fun getItemCollectorThread(maxWait: Long,city: City,title: String, minPrice: Int, maxPrice: Int):Thread{
        val url = getSearchUrl(city.id!!,0,title,title,minPrice,maxPrice)
        return Thread{
            val startTime = System.currentTimeMillis()
            val items = getSimpleItemData(maxWait,url)
            var count = 0
            items.forEach{
                var count = simpleItemDao!!.countById(it.id!!)
                if(null == count || count < 1){
                    it.cityId = city.id
                    simpleItemDao!!.save(it)
                    count = count  + 1
                }
            }
            val useTime =  ( System.currentTimeMillis() - startTime ) / 100 / 10.0
            println("\nin city:$city,save data:$count,useTime:$useTime\n")
        }
    }

    fun getCityCollectorThread():Thread{
        return Thread{
            var citys = getCitys()
            testSaveToDb(citys)

            citys = getHotCitys()
            testSaveToDb(citys,true)
        }
    }

}