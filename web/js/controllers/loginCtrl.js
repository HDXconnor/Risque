'use strict';

angular.module('gameApp')

    .controller('LoginController', ['$rootScope', '$cookieStore', '$http', function ($rootScope, $cookieStore, $http) {
            this.setUser = function () {
                $rootScope.loginInformation = document.getElementById("login-textbox").value;
                var joinData = JSON.stringify({Command: "Join", Data: {CurrentPlayer: $rootScope.loginInformation}});
                postData(joinData);
                writeCookie("Username", $rootScope.loginInformation);
                //$cookieStore.put("Username", $rootScope.username);
            };

            this.loginVis = function () {
                return(document.cookie.indexOf("Username") >= 0);
            };

            this.delCookie = function () {
                var cookie = readCookie();
                for (var index in cookie) {
                    var name = cookie[index];
                }
                name = name.replace('Username=', '');
                var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                document.cookie = "Username=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                postData(quitData);
                $rootScope.$apply();
            };

            function postData(data) {
                $http({
                    method: 'POST',
                    url: 'GameServlet',
                    headers: {'Content-Type': 'application/json'},
                    data: data
                }).error();
            }
            function writeCookie(key, value) {
                document.cookie = key + "=" + value + "; ";
    };
    }]);