var app = new Vue({
  el: '#indexDiv',
  data: {
    message: 'Hello world!',
    simpleItem:{
        tile:'',
        canCheck:'',
        keyWords:'',
        city:Id,
        currentPage:0,
        currentPageSize:30,
    },
    citys:[{name:'全部',id:''}],
    collector:{
        title:'',
        threadSize：5,
        minPrice:1,
        maxPrice:15000,
        hot:1
    },
    simpleItems:[]

  },
  methods:{
    goToAccount: function() {
        top.location.href = "/spring/page/account.html";
    },
    getCitys:function(){
        $.ajax({
          type:'post',
          dataType:'json',
          url:"/ershou/getCitys",
          data:{},
          success:function(json){
              app.citys = [{name:'全部',id:''}];
              app.citys = app.citys.concat(json);
          }
      });
      return  app.citys
    },
    searchContent: function(){
        $.ajax({
              type:'post',
              dataType:'json',
              url:"/ershou/getItems",
              data:simpleItem,
              success:function(json){
                  debugger;
                  app.simpleItems = dataList;
              }
          });
    }
  }
});

app.getCitys();
app.searchContent();
