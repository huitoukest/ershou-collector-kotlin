//工具集合Tools
window.T = {};
// 获取请求参数
// 使用示例
// location.href = http://localhost/index.html?id=123
// T.p('id') --> 123;
T.p = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
};

//请求前缀
var baseURL = "/ershou/";

//jquery全局配置
$.ajaxSetup({
    dataType: "json",
    cache: false,
    async: false,
    headers: {
    },
    xhrFields: {
        withCredentials: true
    },
    complete: function (xhr) {
    }
});


//重写alert
window.alert = function (msg, callback) {
    parent.layer.alert(msg, function (index) {
        parent.layer.close(index);
        if (typeof(callback) === "function") {
            callback("ok");
        }
    });
}

//重写confirm式样框
window.confirm = function (msg, callback) {
    parent.layer.confirm(msg, {btn: ['确定', '取消']},
        function () {//确定事件
            if (typeof(callback) === "function") {
                callback("ok");
            }
        });
}
//判断是否为空
function isBlank(value) {
    return !value || !/\S/.test(value)
}

function ajax(url, data, type, async, cache) {
    var deferred = $.Deferred();
    $.ajax({
        url: url,
        type: type,
        timeout: 60000,
        data: data,
        cache: cache,
        dataType: 'json',
        async: async,
        contentType: 'application/json',
        success: function (msg) {
            deferred.resolve(msg)
        },
        error: function (error) {
            deferred.reject(error);
        }
    });
    return deferred.promise()
}


function post(url, data) {
    var deferred = $.Deferred();
    this.ajax(url, data, 'post', true, false).done(function (e) {
        deferred.resolve(e);
    }).fail(function (e) {
        deferred.reject(e);
    });
    return deferred.promise()
}

String.prototype.trim = function() {
  return this.replace(/(^\s*)|(\s*$)/g, '');
};

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
//扩展常用工具
var tools = new function(){
    var self = this;
    self.isUrl= function (name) {
        return url(name);
    };
    self.isBlank=function (content) {
        return isBlank(content);
    };
    self.isNull=function (content) {
        if (typeof content === 'undefined' || null === content) {
            return true;
        }
        if ($.trim(content) === '') {
            return true;
        }
        if (0 != content && !content) {
            return true;
        }
        return false;
    };
    self.isMobile=function (content) {
        if (!(/^1[34578]\d{9}$/.test(content))) {
            return false;
        }
        return true;
    };
    self.isNumber=function(content) {
        return (new RegExp(/^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/).test(content));
    };
    self.isInRange=function (content, min, max) {
        if (!self.isNumber(content)) {
            return false;
        }
        if (min <= content && max >= content) {
            return true;
        }
        return false;
    };
    self.range = self.isInRange;
    self.isPositiveInteger=function(content){
        return (new RegExp(/^[1-9]\d*$/).test(content));
    };
    self.lenth=function(content,isTrim){//识别中英文字符差异的长度
        if(typeof content == 'undefined'){
            content = '';
        }
        if(typeof isTrim != 'undefined' && isTrim){
            content = $.trim(content);
        }
        var len = 0;
        for(var i=0; i<content.length; i+=1) {
            var c = content.charCodeAt(i);
            if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) {
                len+=1;
            }
            else {
                len+=2;
            }
         }
         return len;
    };
    self.length = self.lenth;
    self.isInteger=function(content){
        return (new RegExp(/^\d+$/).test(content));
    };
    self.trans=function(content) {
        return content.replace(/&lt;/g, '<').replace(/&gt;/g,'>').replace(/&quot;/g, '"');
    };
    self.replaceAll=function(content,os, ns) {
        return content.replace(new RegExp(os,"gm"),ns);
    };
    self.skipChar=function(content,ch) {
        if (!content || content.length===0) {return '';}
        if (content.charAt(0)===ch) {return content.substring(1).skipChar(ch);}
        return content;
    };
    self.isValidMail=function(content){
        return(new RegExp(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/).test($.trim(content)));
    };
    self.isSpaces=function(content) {
        for(var i=0; i<content.length; i+=1) {
            var ch = content.charAt(i);
            if (ch!=' '&& ch!="\n" && ch!="\t" && ch!="\r") {return false;}
        }
        return true;
    };
    self.isPhone=function(content) {
        return (new RegExp(/(^([0-9]{3,4}[-])?\d{3,8}(-\d{1,6})?$)|(^\([0-9]{3,4}\)\d{3,8}(\(\d{1,6}\))?$)|(^\d{3,8}$)/).test(content));
    };
    self.isURL=function(content){
        return (new RegExp(/^[a-zA-z]+:\/\/(\w+(-\w+)*)(\.(\w+(-\w+)*))*(\?\S*)?$/).test(content));
    };
    self.isIP=function(content){
        return (new RegExp(/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/).test(content));
    };
    self.isPercent=function(content){//0-100以内的，最多两位小数的数字
         return new RegExp(/^[1-9]\d{0,1}\.\d{1,2}$|^[1-9]\d{0,1}$|^0\.\d{1,2}&|^0$/).test(content);
    };
    self.getParams= T.p;
    self.getConfigsByKey=function (category,callback) {
        $.ajax({
            url: baseURL + "jqconfig/getMap?category=" + category,
            dataType: 'json',
            async: false,
            contentType: 'application/json',
            success: function (msg) {
                if(msg.code == 0){
                    callback(msg.data);
                }else {
                    alert("操作失败:" + msg.msg);
                }
            },
            error: function (error) {
                alert("error:" + error);
            }
        });
    };
    self.trim=function(str){
        if(tools.isNull(str)){
            str = '';
        }
        return str.replace(/(^\s*)|(\s*$)/g, '');
    };
    self.openBigImg=function(img){
         if(!tools.isNull(img)){
            var url = $(img).attr('src');
            if(!tools.isNull(url)){
                window.open(url,"_blank");
            }else{
                console.debug("error img src:" + url);
            }
         }
    };
}();
window.tools = tools;
