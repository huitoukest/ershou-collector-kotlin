package com.tingfeng.ershou.collector.util

import cn.wanghaomiao.xpath.model.JXDocument
import org.jsoup.nodes.Element

object JsoupXpathUtil {
    fun getNodeXpath(element: Element, xpath:String, isTable:Boolean = true): Element?{
        val content = if(isTable) getTableContent(element.html()) else element.html()
        val jxDocument = JXDocument(content).sel(xpath)
        if(jxDocument.size <= 0) return null
        return jxDocument.get(0) as Element
    }
    fun getNodesXpath(element: Element, xpath:String, isTable:Boolean = true): List<Element> {
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

}