package com.tingfeng.ershou.collector.service.impl

import com.tingfeng.ershou.collector.dao.SimpleItemDao
import com.tingfeng.ershou.collector.dto.Pager
import com.tingfeng.ershou.collector.entity.SimpleItem
import com.tingfeng.ershou.collector.service.SimpleItemService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import javax.persistence.criteria.Predicate
import java.util.ArrayList



@Service
class SimpleItemServiceImpl : SimpleItemService {
    @Autowired
    lateinit var simpleItemDao:SimpleItemDao

    override fun findPagers(simpleItem: SimpleItem, pager: Pager<SimpleItem>,keyWords:List<String>?): Pager<SimpleItem> {
        val specification = Specification<SimpleItem> { root, query, cb ->
            /**
             * 构造断言
             * @param root 实体对象引用
             * @param query 规则查询对象
             * @param cb 规则构建对象
             * @return 断言
             */
            val predicates = ArrayList<Predicate>() //所有的断言
            if (StringUtils.isNotBlank(simpleItem.cityId)) { //添加断言
                val likeNickName = cb.like(root.get<Any>("cityId").`as`(String::class.java), simpleItem.cityId)
                predicates.add(likeNickName)
            }
            if (null != simpleItem.canCheck) { //添加断言
                val likeNickName = cb.equal(root.get<Any>("canCheck").`as`(Integer::class.java), simpleItem.canCheck)
                predicates.add(likeNickName)
            }
            if (StringUtils.isNotBlank(simpleItem.title)) { //添加断言
                val likeNickName = cb.like(root.get<Any>("title").`as`(String::class.java),"%" +  simpleItem.title + "%")
                predicates.add(likeNickName)
            }
            if(null != keyWords) {
                keyWords.forEach {
                    if (StringUtils.isNotBlank(it)) {
                        val likeNickName = cb.like(root.get<Any>("content").`as`(String::class.java), "%" + it + "%")
                        predicates.add(likeNickName)
                    }
                }
            }
            cb.and(*predicates.toTypedArray())
        }
        val sort = Sort(Sort.Direction.DESC, "createTime")
        val pageable = PageRequest(pager.getCurrentPage() - 1, pager.getPageSize(), sort)
        val items = simpleItemDao.findAll(specification, pageable)
        pager.setTotalRow(items.totalElements.toInt())
        pager.setDataList(items.getContent())
        return pager
    }
}