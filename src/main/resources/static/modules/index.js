var app = new Vue({
  el: '#indexDiv',
  data: {
    message: 'Hello world!'
  },
  methods:{
    goToAccount: function() {
        top.location.href = "/spring/page/account.html";
    },
  }
});