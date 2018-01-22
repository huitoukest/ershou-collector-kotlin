var app = new Vue({
  el: '#main',
  data: {
    citys:[{name:'全部',value:''},{name:'热门和省会城市',value:'3'},{name:'2线城市',value:'2'},{name:'3线城市',value:'1'},{name:'其它',value:'0'}],
    threadSizes:[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15],
    collector:{
        title:'',
        threadSize:8,
        minPrice:1,
        maxPrice:15000,
        hot:1
    },
    status:0,
    statusArray:[{name:'停止',value:0},{name:'正在运行',value:1},{name:'暂停',value:2},{name:'正在停止',value:3}],
  },
  methods:{
    startCollect: function(){
        $.ajax({
              type:'get',
              dataType:'json',
              url:"/ershou/collector/start",
              data:app.collector,
              success:function(json){
                 if(true == json){
                    alert("服务器已经开始采集信息...")
                 }else{
                    alert("服务器正在工作中，请等状态停止后再开始...")
                 }
              }
          });
    },
    stopCollect: function(){
            $.ajax({
                  type:'get',
                  dataType:'json',
                  url:"/ershou/collector/stop",
                  data:app.collector,
                  success:function(json){
                     try{
                         if(true == json){
                            alert("服务器已经准备停止采集信息...")
                        }
                     }catch(e){
                        console.debug(e)
                     }
                  }
              });
    },
    getStatus: function(){
        $.ajax({
              type:'get',
              timeout : 4000, //超时时间设置，单位毫秒
              dataType:'json',
              url:"/ershou/collector/getStatus",
              data:app.collector,
              success:function(json){
                app.status = json;
              },
              complete:function(XMLHttpRequest,status){ //请求完成后最终执行参数
                setTimeout("app.getStatus()",5000);
              }
          });
    }
  }
});

app.getStatus();
