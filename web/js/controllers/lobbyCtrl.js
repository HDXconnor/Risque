

angular.module('gameApp')
        .controller('LobbyController', ['$rootScope', '$http', function ($rootScope, $http) {
                this.isHost = function() {
                    if ($rootScope.obj != null) {
                        if ($rootScope.userName == $rootScope.players[0].DisplayName) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
                this.isGamesListEmpty = function(){
                    if ($rootScope.lobbyList.length === 0) {
                        return true;
                    } else {
                        return false;
                    }
                };

                this.gameType = function(password) {
                    if (password == null) {
                        return "Public";
                    } else {
                        return "Private";
                    }
                };

                this.quickStart = function(){
                    var quickStartData = JSON.stringify({Command: "QuickStart", Data: {}});
                    postData(quickStartData);
                };
                   
                this.setGameName= function () {
                    var gameName = document.getElementById("create-input").value;
                    var startGameData = JSON.stringify({Command: "Create", Data: {GameName: gameName, GamePassword: ""}});
                    postData(startGameData);
                    
                };
                
                this.lobbyVis = function () {
                    return($rootScope.userName != null  && $rootScope.gameStarted !== true);
                };
                
                this.closeLobby = function () {
                    var startGameData = JSON.stringify({Command: "CloseLobby", Data: {}});
                    postData(startGameData);
                };

                this.quitGame = function () {
                    var startGameData = JSON.stringify({Command: "Quit", Data: {}});
                    postData(startGameData);
                    $rootScope.obj = null;
            };

                this.joinGame = function (gameID) {
                    var startGameData = JSON.stringify({Command: "Join", Data: {GameID: gameID, GamePassword: ""}});
                    postData(startGameData);
                };

                this.logOut = function () {
                    if ($rootScope.obj!=null) {
                        this.quitGame();
                    }
                    var startGameData = JSON.stringify({Command:"Logout",Data:{}});
                    $rootScope.userName = null;
                    postData(startGameData);
                };

                this.delCookie = function () {
                    name.replace('Username=', '');
                    var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: $rootScope.thisPlayer}});
                    document.cookie = 'Username=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
                    postData(quitData);
                };

                this.magicButton = function () {
                    console.log($rootScope.lobbyList);
                }

                this.quickStartButton = function () {
                    console.log("QUICKSTART MODE");
                    var quickStartData = JSON.stringify({Command: "QuickStart", Data: {}});
                    postData(quickStartData);
                }

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