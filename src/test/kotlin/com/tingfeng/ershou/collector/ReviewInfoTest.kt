package com.tingfeng.ershou.collector

import cn.wanghaomiao.xpath.model.JXDocument
import com.tingfeng.ershou.collector.util.JsoupXpathUtil
import org.jsoup.nodes.Element
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit



/**
 * 评论信息测试
 */
class ReviewInfoTest {
    val baidu = "https://www.baidu.com/";

    companion object {
        val file_chrome = File("E:/drivers/chromedriver.exe")
        val storyTitleNotContains = listOf<String>("动漫","动画","漫画","游戏","手游","网游","电视剧","电影","视频","配音")
        var  chromeService :ChromeDriverService  ;
        init {
            System.setProperty("webdriver.chrome.driver", file_chrome.getAbsolutePath())
            val builder =  ChromeDriverService.Builder();
            chromeService = builder.usingDriverExecutable(file_chrome).usingPort(3333).build();
            chromeService.start();
        }
    }

    fun getChromDriver():ChromeDriver{
        val driver = ChromeDriver(chromeService)
        return driver
    }

    fun closeChromDriver(driver :ChromeDriver){
        if(null != driver){
            driver.quit()
        }
    }

    fun stopChromDriverService():Unit{
        chromeService.stop()
    }
    @Test
    fun getAccountsTests(){
        var chromDriver = getChromDriver()
        try {
            /*val storyList = listOf<String>("遮天","吞噬星空","斗罗大陆",
                    "英雄志","三体","紫川","新宋","圣墟","放开那个女巫",
                    "卡徒","人道至尊","龙蛇演义","奥术神座",
                    "完美世界","武动乾坤","莽荒纪","飞升之后")*/
            /*val storyList = listOf<String>("如何评价天龙八部","如何评价我欲封天","如何评价绝世武神",
                    "如何评价永夜君王","如何评价灵域","如何评价诛仙","如何评价从前有座灵剑山","如何评价盘龙","如何评价龙族",
                    "如何评价星战风暴","如何评价宝鉴","如何评价天珠变","如何评价全职高手")*/
            val storyList = listOf<String>("拍案叫绝的智障桥段");
            getAccountsTest(chromDriver,storyList);
        }finally {
            closeChromDriver(chromDriver);
            stopChromDriverService()
        }
    }

    fun getAccountsTest (chromDriver : ChromeDriver,sotryNames :List<String>) {

        sotryNames.forEach{
           try {
               var zhihuUrls = getZhihuUrls(chromDriver, "$it site:zhihu.com", it)
               val reviewInfos = ArrayList<String>(200)
               zhihuUrls.forEach {
                   reviewInfos.addAll(getZhihuReviewInfo(chromDriver, it))
               }
               val file = File("/home/tmp/", it + "_reviewInfo.txt")
               if (!file.exists()) {
                   file.createNewFile()
               }
               reviewInfos.forEach {
                   file.appendText(it, Charset.forName("UTF-8"))
                   file.appendText("\n")
               }
           }catch (e : Exception){
               e.printStackTrace()
           }
        }
    }


    fun getZhihuUrls(chromDriver : ChromeDriver,title : String ,key :String):List<String>{
        chromDriver.get(baidu);
        chromDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS) //显示等待数据加载
        //定位到百度的输入框
        val element = chromDriver.findElement(By.id("kw")).sendKeys(title)
        Thread.sleep(2000)

        //点击搜索
        chromDriver.findElement(By.id("su")).click()

        //点击一月内 By.cssSelector("search_tool_tf")
        val times = chromDriver.findElements(By.xpath("//span[contains(@class,'search_tool_tf')]"))
        times[0].click()
        Thread.sleep((Math.random() * 2000).toLong())
        val timeMenus = chromDriver.findElements(By.xpath("//div[@class='c-tip-menu c-tip-timerfilter']/ul/li/a"))
        timeMenus.filter { it-> it.text == "一年内"}[0].click()
        Thread.sleep((Math.random() * 3000).toLong())
        //outerHTML innerHTML
        val content = chromDriver.findElement(By.xpath("//div[@id='container']")).getAttribute("innerHTML")

        val jsoupContent = JXDocument(content)

        val zhihuElement= jsoupContent.sel("//div[@id='content_left']/div");
        var zhihuUrls =  ArrayList<String>()
            if(zhihuElement.size > 0){
                zhihuUrls = zhihuElement .map {
                                it as Element
                                var re : String? = null
                                val title = JsoupXpathUtil.getNodeXpath(it,"//h3[@class='t']/a")?.text()
                                if(title != null && title.contains(key)){
                                    var isOk = true
                                    storyTitleNotContains.forEach {
                                        if(isOk && title.contains(it))
                                        {
                                            isOk = false
                                        }
                                    }
                                    if(isOk) {
                                        val href = JsoupXpathUtil.getNodeXpath(it, "//div[@class='f13']/a[1]")!!
                                        if (href.text().contains("zhihu")) {
                                            re = href.attr("href")
                                        }
                                    }
                                }
                                    re
                            }
                            .filter { it-> it != null  } as ArrayList<String>
            }

        //点击搜索
        //chromDriver.findElement(By.id("su")).click()

        return zhihuUrls;
    }

    fun  getZhihuReviewInfo(chromDriver : ChromeDriver,zhihuUrl :String ):List<String>{
        chromDriver.get(zhihuUrl);
        chromDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS) //显示等待数据加载
        Thread.sleep((Math.random() * 10000).toLong());
        //查看所有回答
        val allQues = chromDriver.findElement(By.xpath("//a[@class='QuestionMainAction']"));
        if(allQues != null){
            val content = allQues.text
            try {
                allQues.click();
                val count = content.replace(Regex("[^\\d]"),"").toLong()
                var sleepTime:Long  = count
                if(count < 500){
                    sleepTime = 500
                }else if(count > 2000){
                    sleepTime = 3000
                }
                Thread.sleep(sleepTime)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
         //outerHTML innerHTML
        val content = chromDriver.findElement(By.xpath("//body")).getAttribute("innerHTML")
        val jsoupContent = JXDocument(content)
        val zhihuElements = jsoupContent.sel("//div[@id='QuestionAnswers-answers']//div[@class='List-item']//div[@class='RichContent-inner']")
        var zhihuReviewInfo =  ArrayList<String>()
        if(zhihuElements.size > 0){
            zhihuReviewInfo = zhihuElements.map {
                it as Element
                val re = JsoupXpathUtil.getNodeXpath(it,"//span")?.text()
                re
            }
                    .filter { it-> it != null  } as ArrayList<String>
        }
        return zhihuReviewInfo
    }

    fun testReadZhihu():Unit{
        val file = File("/home/tmp/zhetian_zhihu.txt")
        val content = file.readText(Charset.forName("utf-8"))
        val jsoupContent = JXDocument(content)
        val zhihuElements = jsoupContent.sel("//div[@id='QuestionAnswers-answers']//div[@class='List-item']//div[@class='RichContent-inner']")
        var zhihuReviewInfo =  ArrayList<String>()
        if(zhihuElements.size > 0){
            zhihuReviewInfo = zhihuElements.map {
                it as Element
                val re = JsoupXpathUtil.getNodeXpath(it,"//span")?.text()
                re
            }
                    .filter { it-> it != null  } as ArrayList<String>
        }
        zhihuReviewInfo.forEach { println(it) }
    }

}