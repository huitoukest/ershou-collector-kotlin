var app = new Vue({
  el: '#accountDiv',
  data: {
    message: 'Hello world!',
    account:{
        money:null,
        name:null,
    },
    accountList:[],
    listContent:"显示列表",
    accountSearchList:[],
    searchName:''
  },
  methods:{
      addAccount:function(){
        console.info(app.account);
        $.ajax({
            type:'post',
            url:"/spring/account/add",
            data:app.account,
            success:function(str){
                alert("add success:" + str);
            }
        });
      },
      showList:function(){
        if(app.accountList.length > 0){
            app.listContent = '隐藏列表';
            app.accountList = [];
            return;
        }else{
            app.listContent = '显示列表';
        }
        $.ajax({
            type:'post',
            dataType:'json',
            url:"/spring/account/list",
            data:app.account,
            success:function(json){
                app.accountList = json;
            }
        });
      },
      searchAccount:function(){
           if(app.searchName.length <= 0){
                return;
           }
           $.ajax({
              type:'post',
              dataType:'json',
              url:"/spring/account/search",
              data:{'name':app.searchName},
              success:function(json){
                  app.accountSearchList = json;
              }
          });
      }
  }
});