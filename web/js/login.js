(function () {

    var app = angular.module("loginApp", [])

    // var googleLogin =
    //var facebookLogin =

   .controller("loginController", ["$http",function($http) {
        var login=this;
        this.setUser= function(){
            $http.post('192.168.20.6:8080/RisqueServer/game/0', { param: {"Game":{"Players":"-1","Phase":"Create","Data":null}} });
        }

   }]);


})();