package com.tingfeng.ershou.collector.service.impl

import com.google.gson.Gson
import com.tingfeng.ershou.collector.dao.CityDao
import com.tingfeng.ershou.collector.dao.SimpleItemDao
import com.tingfeng.ershou.collector.entity.City
import com.tingfeng.ershou.collector.entity.SimpleItem
import com.tingfeng.ershou.collector.service.CollectorService
import com.tingfeng.ershou.collector.util.ChromeDriverUtil
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import cn.wanghaomiao.xpath.model.JXDocument
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


@Service
class CollectorServiceImpl : CollectorService {

     companion object {
        var isPause  = false
        var isStop = false
        var isRun = false
        var isCityUpdated = false
        var threadPool = Executors.newFixedThreadPool(15)
        val runingCount = AtomicInteger (0)
         var status = Thread{
                 while (true) {
                     synchronized(CollectorServiceImpl.javaClass){
                         if (threadPool.isShutdown || threadPool.isTerminated || runingCount.get() <= 0) {
                             isStop = true
                             isRun = false
                             isPause = false
                         }
                         Thread.sleep(2000)
                 }
             }
         }.start()
    }

    @Autowired
    lateinit var cityDao: CityDao
    @Autowired
    lateinit var simpleItemDao: SimpleItemDao

    override @Synchronized fun getStatus(): Int {
        var status = 0;
        if(threadPool.isShutdown || runingCount.get() <= 0){
            return 0
        }
        if(isStop && !threadPool.isShutdown){
            return 3
        }
        if(isPause){
            return 2
        }
        if(isRun){
            return 1
        }
        return status
    }

    override @Synchronized  fun pause() {
        if(!isPause) {
            isPause = true
        }
    }

    override @Synchronized  fun stop() {
       if(!isStop) {
           isStop = true
           isRun = false
           isPause = false
           threadPool.shutdown()
       }
    }


    fun getChromeDriver(): ChromeDriver {
        return ChromeDriverUtil.getChromeDriver(false)
    }

    fun closeChromeDriver(driver:ChromeDriver?){
        if(null != driver){
            ChromeDriverUtil.closeChromeDriver(driver)
        }
    }

    fun getCitys():List<Pair<String,String>>{
        if (isStop) return ArrayList<Pair<String,String>>(0)
        runingCount.incrementAndGet()
         var my_dr : ChromeDriver? = null
        try {
            val indexUrl = "http://www.58.com/changecity.html";
             my_dr =  getChromeDriver()// 打开chrome浏览器

            my_dr.get(indexUrl)

            var xpathAllCity = "//div[@class='content-cities']/a";
            //Thread.sleep(2000)//等待数据加载
            my_dr.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS) //显示等待数据加载
            val htmlContent =   my_dr.findElement(By.xpath("/html[1]")).getAttribute("outerHTML")
            val elements  = JXDocument(htmlContent).sel(xpathAllCity)
            val content =  elements.map {
                it as Element
                Pair(it.attr("href"), it.text())
            }
            closeChromeDriver(my_dr)
            return content
        }catch (e:Exception){
            closeChromeDriver(my_dr)
            throw e
        }finally {
            runingCount.decrementAndGet()

        }
    }

    fun getHotCitys():List<Pair<String,String>>{
        if (isStop) return ArrayList<Pair<String,String>>(0)
        runingCount.incrementAndGet()
        var my_dr =  getChromeDriver()// 打开chrome浏览器
        try {
            val indexUrl = "http://www.58.com/changecity.html";
            my_dr.get(indexUrl)
            var xpathAllCity = "//div[@id='hot']/a[@class='hot-city']";
            my_dr.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS) //显示等待数据加载
            //获取热门城市
            val htmlContent =   my_dr.findElement(By.xpath("/html[1]")).getAttribute("outerHTML")
            val elements  = JXDocument(htmlContent).sel(xpathAllCity)
            val content =  elements.map{
                it as Element
                Pair(it.attr("href"),it.text())
            }
            closeChromeDriver(my_dr)
            return content

        }catch (e:Exception){
            closeChromeDriver(my_dr)
            throw  e
        }finally {
            runingCount.decrementAndGet()
        }


    }

   fun saveToDb(citys:List<Pair<String,String>>,isHot:Boolean = false){
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

    fun getNodeXpath(element: Element,xpath:String,isTable:Boolean = true):Element?{
        val content = if(isTable) getTableContent(element.html()) else element.html()
        val jxDocument = JXDocument(content).sel(xpath)
        if(jxDocument.size <= 0) return null
        return jxDocument.get(0) as Element
    }
    fun getNodesXpath(element: Element,xpath:String,isTable:Boolean = true): List<Element> {
        val content = if(isTable) getTableContent(element.html()) else element.html()
        val jxDocument = JXDocument(content).sel(xpath)
        return jxDocument as List<Element>
    }

    fun getTableContent(element: Element):String{
        return getTableContent( element.html());
    }

    fun getTableContent(content:String):String{
        return "<table>"+ content + "<table/>";
    }

    fun getSimpleItemData(maxWait:Long = 15,url:String):List<SimpleItem>{
        if (isStop) return ArrayList<SimpleItem>(0)
            runingCount.incrementAndGet()
        var content = StringBuffer(5000)
        var my_dr =  getChromeDriver()// 打开chrome浏览器
        try {
            my_dr.get(url)
            my_dr.manage().timeouts().implicitlyWait(maxWait, TimeUnit.SECONDS) //显式等待数据加载

            val htmlContent =   my_dr.findElement(By.xpath("/html[1]")).getAttribute("outerHTML")
            //得到当前页面的item信息
            var xpathAllCity = "//div[@class='infocon']/table[@class='tbimg']//tr";//获取所有的item行
            val jxDocument = JXDocument(htmlContent)
            val elements = jxDocument.sel(xpathAllCity)
            val items = elements.map {
                try{
                    it as Element
                    val item = SimpleItem()
                    item.icon = getNodeXpath(it,"//td[@class='img']/a/img")?.attr("src")
                    val show = "//td[@class='t']"

                    var node = getNodeXpath(it,show + "/a[@class='t']")
                    item.url = node?.attr("href")
                    item.title = node?.text()

                    val price = getNodeXpath(it,show + "/span[@class='pricebiao']/span[@class='price']")?.text()
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
                    item.content = getNodeXpath(it,show + "/span[@class='desc']")?.text()
                    try {
                        item.city = getNodeXpath(it,show + "/span[@class='fl']")?.text()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    val tc = "//td[@class='tc']"
                    val sellerInfo = getNodesXpath(it,tc + "//p[@class='name_add']")
                    try {
                        if(sellerInfo.size > 0){
                            item.saler = sellerInfo.get(0).text()
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    try {
                        if(sellerInfo.size > 1){
                            item.source = sellerInfo.get(1).text()
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    try {
                        val check = getNodesXpath(it,tc + "//p[@class='zhijian']")
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
            closeChromeDriver(my_dr)
            println(content)
            return items
        }catch (e:Exception){
            closeChromeDriver(my_dr)
            throw e
        }finally {
            runingCount.decrementAndGet()
        }

        
    }

    override @Synchronized fun start(title: String, minPrice: Int, maxPrice: Int,threadSize:Int,hot:Int):Boolean {
        try {
            var size = 1
            if(threadSize < 1) size = 1
            if(threadSize > 15) size = 15
            ChromeDriverUtil.maxSize = threadSize
            if(!isRun && runingCount.get() <= 0){
                threadPool = Executors.newFixedThreadPool(threadSize)
                isRun = true
                isPause = false
                isStop = false

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
            }else{
                return false
            }
            isPause = false
            isStop = false
        }catch (e:Exception){
            runingCount.set(0)
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
            runingCount.incrementAndGet()
            try {
                val startTime = System.currentTimeMillis()
                val items = getSimpleItemData(maxWait,url)
                var dataCount = 0
                items.forEach{
                    var count = simpleItemDao!!.countById(it.id!!)
                    if(null == count || count < 1){
                        it.cityId = city.id
                        simpleItemDao!!.save(it)
                        dataCount = dataCount  + 1
                    }
                }
                val useTime =  ( System.currentTimeMillis() - startTime ) / 100 / 10.0
                println("\nin city:$city,save data:$dataCount,useTime:$useTime\n")
            }catch (e:Exception)
            {
                e.printStackTrace()
            }finally {
                runingCount.decrementAndGet()
            }


        }
    }

    fun getCityCollectorThread():Thread{
        return Thread{
            runingCount.incrementAndGet()
            try {
                var citys = getCitys()
                saveToDb(citys)
                citys = getHotCitys()
                saveToDb(citys,true)
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                runingCount.decrementAndGet()
            }
        }
    }

}