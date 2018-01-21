package com.tingfeng.ershou.collector.main

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner



@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(AppMain::class),
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinDemoApplicationTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun whenCalled_shouldReturnHello() {
        val result = testRestTemplate
                // ...
                .getForEntity("/hello", String::class.java)

        Assert.assertNotNull("fail!",result)
        Assert.assertEquals( HttpStatus.OK,result?.statusCode)
        Assert.assertEquals("Hello 01!",result?.body)
    }

    @Test
    fun whenCalled_shouldReturnHelloService() {
        var result = testRestTemplate
                // ...
                .getForEntity("/hello-service", String::class.java)
        Assert.assertNotNull(result)
        Assert.assertEquals(HttpStatus.OK,result?.statusCode)
        Assert.assertEquals("hello service",result?.body)
    }
}