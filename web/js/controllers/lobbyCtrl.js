'use strict';

angular.module('gameApp')
    .controller('LobbyController', ['$rootScope', '$http', function ($rootScope, $http) {

        this.lobbyVis = function () {
            return(document.cookie.indexOf("Username") >= 0 && $rootScope.gameStarted !== true);
        };

        this.startGame = function () {
            var startGameData = JSON.stringify({Command: "StartGame", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
            postData(startGameData);
        };

        this.delCookie = function () {
            console.log("Deleting cookie...");
            var cookie = readCookie();
//            for (var index in cookie) {
//                //var name = cookie[index];
//                index.replace('Username=','');
//            }
            //name.replace('Username=', '');
            var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
            document.cookie = 'Username=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
            postData(quitData);
        };

        this.magicButton = function () {
            console.log($rootScope.host);
            console.log($rootScope.username);
        };

        this.debugButton = function () {
            console.log("STARTING DEBUG");
            var debugData = JSON.stringify({Command: "Debug", Data: {}});
            postData(debugData);
        };

        function readCookie() {
            var x = document.cookie;
            var keyArray = x.split("; ");
            return keyArray;
        }

        function postData(data) {
            $http({
                method: 'POST',
                url: 'GameServlet',
                headers: {'Content-Type': 'application/json'},
                data: data
            }).error();
        };
    }]);