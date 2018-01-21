package com.tingfeng.ershou.collector.dto

class  Pager<T>{
    private var currentPage = 1 // 当前页
    private var pageSize = 30 // 每页多少行
    private var totalRow: Int = 0 // 共多少行
    private var start: Int = 0// 当前页起始行
    private var end: Int = 0// 结束行
    private var totalPage: Int = 0 // 共多少页
    private var dataList :List<T> = ArrayList(0)

    constructor(){}

    constructor(currentPage: Int) {
        this.currentPage = currentPage
    }

    constructor(currentPage: Int, pageSize: Int) {
        this.currentPage = currentPage
        this.pageSize = pageSize
    }


    fun getCurrentPage(): Int {
        return currentPage
    }

    fun setCurrentPage(currentPage: Int) {
        this.currentPage = currentPage
    }

    fun getStart(): Int {
        if (currentPage < 1) {
            currentPage = 1
        } else {
            start = pageSize * (currentPage - 1)
        }
        return start
    }

    fun getEnd(): Int {
        this.end = getStart() * this.pageSize
        return this.end
    }

    fun getPageSize(): Int {
        return pageSize
    }

    fun setPageSize(pageSize: Int) {
        this.pageSize = pageSize
    }

    fun getTotalRow(): Int {
        return totalRow
    }

    fun setTotalRow(totalRow: Int) {
        this.totalRow = totalRow
    }

    fun getTotalPage(): Int {
        var total =  this.totalRow / this.pageSize
        if(this.totalRow % this.pageSize != 0){
            total ++
        }
        return this.totalPage
    }

    fun getDataList():List<T>{
        return this.dataList
    }

    fun setDataList(dataList: List<T>){
        this.dataList = dataList
    }

}
