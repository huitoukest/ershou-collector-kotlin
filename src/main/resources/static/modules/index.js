var app = new Vue({
  el: '#indexDiv',
  data: {
    message: 'Hello world!',
    simpleItem:{
        tile:'',
        canCheck:'',
        keyWordsString:'',
        cityId:'',
        currentPage:1,
        pageSize:30,
        minPrice:1,
        maxPrice:15000,
    },
    citys:[{name:'全部',id:''}],
    collector:{
        title:'',
        threadSize:5,
        minPrice:1,
        maxPrice:15000,
        hot:1
    },
    simpleItems:[],
    pageInfo:{'pageSize':'30',currentPage:1,totalRow:0,totalPage:0}
  },
  methods:{
    goToAccount: function() {
        top.location.href = "/spring/page/account.html";
    },
    getCitys:function(){
        $.ajax({
          type:'get',
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
    searchContent: function(pageNo){
        if(typeof pageNo !='undefined' && null != pageNo){
            app.simpleItem.currentPage = pageNo;
        }
        if(app.simpleItem.currentPage < 1){
            app.simpleItem.currentPage = 1
        }

        $.ajax({
              type:'get',
              dataType:'json',
              url:"/ershou/getItems",
              data:app.simpleItem,
              success:function(json){
                  app.simpleItems = [];
                  app.pageInfo = json;
                  app.pageInfo.totalPage = app.pageInfo.totalRow / app.pageInfo.pageSize + ( app.pageInfo.totalRow % app.pageInfo.pageSize > 0 ? 1 : 0 )
                  app.simpleItems = json.dataList;
                  /*for(var i = 0;i<json.dataList.length;i++){
                    app.simpleItems.push(json.dataList[i]);
                  }*/
              }
          });
    }
  }
});

app.getCitys();
app.searchContent();
