

angular.module('gameApp')
        .controller('LobbyController', ['$rootScope', '$http', function ($rootScope, $http) {

                this.lobbyVis = function () {
                    return(typeof $rootScope.userName !== "undefined"  && $rootScope.gameStarted !== true);
                };

                this.closeLobby = function () {
                    var startGameData = JSON.stringify({Command: "CloseLobby", Data: {}});
                    postData(startGameData);
                };

                this.delCookie = function () {
                    name.replace('Username=', '');
                    var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: $rootScope.thisPlayer}});
                    document.cookie = 'Username=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
                    postData(quitData);
                };

                this.magicButton = function () {
                    console.log($rootScope.host);
                    console.log($rootScope.username);
                }

                this.debugButton = function () {
                    console.log("STARTING DEBUG");
                    var debugData = JSON.stringify({Command: "Debug", Data: {}});
                    postData(debugData);
                }

                this.hostControls = function () {
                    if ($rootScope.username === $rootScope.host) {
                        return true;
                    } else {
                        return false;
                    }
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
                }
                ;
            }]);