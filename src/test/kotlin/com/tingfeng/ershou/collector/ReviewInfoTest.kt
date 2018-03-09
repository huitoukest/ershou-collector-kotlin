package com.tingfeng.ershou.collector

import cn.wanghaomiao.xpath.model.JXDocument
import com.tingfeng.ershou.collector.util.JsoupXpathUtil
import org.jsoup.nodes.Element
import org.junit.Test
import org.openqa.selenium.By
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
    fun getAccountsTest() {
        var chromDriver = getChromDriver()
        try {
            val storyName = "圣墟";
            val filePrefix = storyName;
            var zhihuUrls = getZhihuUrls(chromDriver,"如何评价$storyName site:zhihu.com", storyName)
            val reviewInfos = ArrayList<String>(200)
            zhihuUrls.forEach {
                reviewInfos.addAll(getZhihuReviewInfo(chromDriver,it))
            }
            val file = File("/home/tmp/", filePrefix + "_reviewInfo.txt")
            if (!file.exists()) {
                file.createNewFile()
            }
            reviewInfos.forEach {
                file.appendText(it, Charset.forName("UTF-8"))
                file.appendText("\n")
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            closeChromDriver(chromDriver);
            stopChromDriverService()
        }
    }


    fun getZhihuUrls(chromDriver : ChromeDriver,title : String ,key :String):List<String>{
        chromDriver.get(baidu);
        chromDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS) //显示等待数据加载
        //定位到百度的输入框
        val element = chromDriver.findElement(By.id("kw")).sendKeys(title)
        Thread.sleep(1000)

        //点击搜索
        chromDriver.findElement(By.id("su")).click()

        //点击一月内 By.cssSelector("search_tool_tf")
        val times = chromDriver.findElements(By.xpath("//span[contains(@class,'search_tool_tf')]"))
        times[0].click()
        val timeMenus = chromDriver.findElements(By.xpath("//div[@class='c-tip-menu c-tip-timerfilter']/ul/li/a"))
        timeMenus.filter { it-> it.text == "一年内"}[0].click()

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
                                    val href = JsoupXpathUtil.getNodeXpath(it,"//div[@class='f13']/a[1]")!!
                                    if(href.text().contains("zhihu")) {
                                        re =   href.attr("href")
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
        chromDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS) //显示等待数据加载
        Thread.sleep((Math.random() * 10000).toLong());
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