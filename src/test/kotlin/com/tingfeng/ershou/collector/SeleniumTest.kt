package com.tingfeng.ershou.collector

import com.fasterxml.jackson.databind.util.JSONPObject
import com.google.gson.Gson
import org.junit.Test
import org.openqa.selenium.By
import com.tingfeng.ershou.collector.entity.SimpleItem
import org.junit.Assert
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File
import java.util.concurrent.TimeUnit
import javax.persistence.Tuple


class SeleniumTest {

    fun getChromeDriver():ChromeDriver{
        //-----------------------------打开火狐浏览器------------------------------------------------
        //WebDriver my_dr = new FirefoxDriver();// 打开火狐浏览器  原生支持的浏览器，但是不支持火狐高级的版本
        //-----------------------------打开Chrome浏览器---------------------------------------------
        val file_chrome = File("E:/drivers/chromedriver.exe")
        System.setProperty("webdriver.chrome.driver", file_chrome.getAbsolutePath())
        var my_dr =  ChromeDriver()// 打开chrome浏览器
        return my_dr
    }

    @Test
    fun testBaiDu(){
        var my_dr =  getChromeDriver()// 打开chrome浏览器
        //-----------------------------打开IE浏览器--------------------------------------------------
        //val file_ie = File("C:\\Program Files\\Internet Explorer\\IEDriverServer.exe")
        //System.setProperty("webdriver.ie.driver", file_ie.getAbsolutePath())

        //为 Internet Explorer 设置安全性功能,否则会遇到一个安全问题提示："Protected Mode must be set to the same value (enabled or disabled) for all zones"
        //DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
        //caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        //caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
        //WebDriver my_dr = new InternetExplorerDriver(caps);// 打开ie浏览器

        //val my_dr = InternetExplorerDriver()// 打开ie浏览器
        //---------------------------------------------------------------------------------------
        //打开百度
        my_dr.get("http://www.baidu.com")

        Thread.sleep(1000)
        //定位到百度的输入框
        my_dr.findElement(By.id("kw")).sendKeys("G7物流地图")

        Thread.sleep(1000)
        //点击搜索
        my_dr.findElement(By.id("su")).click()

        Thread.sleep(1000)
        //打印页面标题
        println(my_dr.title)
        println(my_dr.findElementsByCssSelector("body"))

        val elements = my_dr.findElements(By.xpath("/html/body"));
        elements.forEach{
            print(it.text)
        }

        //验证页面标题是否符合预期
        Assert.assertEquals(my_dr.title, "G7物流地图_百度搜索")

        Thread.sleep(1000)
        // 关闭所有webdriver进程，退出
        my_dr.quit()
    }

    @Test
    fun test58(){
        val indexUrl = "http://www.58.com/changecity.html";
        var my_dr =  getChromeDriver()// 打开chrome浏览器
        //打开百度
        my_dr.get(indexUrl)
        var content = StringBuffer(5000)

        Thread.sleep(1000)
        content.append("\n")
        content.append("\n")
        //热门城市
        var elements = my_dr.findElements(By.xpath("//div[@id='hot']/a[@class='hot-city']"))
        elements.forEach{
            content.append(it.getAttribute("href"));
            content.append(it.text)
            content.append("\n")
        }
        content.append("\n")
        var xpathAllCity = "//div[@class='content-cities']/a";
            xpathAllCity = "//div[@class='content-cities']/a[1]";

        //获取省会
        getCitys().forEach{
            content.append(it.first,it.second);
        }
        content.append("\n")
        content.append("\n")
        print(content)
        Thread.sleep(1000)
        // 关闭所有webdriver进程，退出
        my_dr.quit()
    }

    fun getCitys():List<Pair<String,String>>{
        val indexUrl = "http://www.58.com/changecity.html";

        var my_dr =  getChromeDriver()// 打开chrome浏览器
        my_dr.get(indexUrl)

        Thread.sleep(2000)
        var xpathAllCity = "//div[@class='content-cities']/a";
        xpathAllCity = "//div[@class='content-cities']/a[1]";

        //获取省会
        val elements = my_dr.findElements(By.xpath(xpathAllCity))
        return elements.map{Pair(it.getAttribute("href"),it.text)}
    }


    fun getSearchUrl(city:String="http://bj.58.com",pageNo:Int = 0,key :String ="",cmcskey:String =""):String{
        return "$city/shouji/pn$pageNo/?key=$key&cmcskey=$cmcskey";
    }

    fun getSimpleItemData():List<SimpleItem>{
        var content = StringBuffer(5000)

        val indexUrl = getSearchUrl("http://bj.58.com",1,"小米","小米")

        var my_dr =  getChromeDriver()// 打开chrome浏览器
        my_dr.get(indexUrl)

        my_dr.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS) //显式等待数据加载

        //得到当前页面的item信息
        var xpathAllCity = "//div[@class='infocon']/table[@class='tbimg']//tr";//获取所有的item行
        val elements = my_dr.findElements(By.xpath(xpathAllCity))
        val items = elements.map {
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
                    item.price = -3
                }catch (e:Exception){
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
        }
        my_dr.quit()
        println(content)
        return items
    }

    @Test
    fun getSimpleItem(){
        getSimpleItemData();
    }

}