

angular.module('gameApp')
        .controller('LobbyController', ['$rootScope', '$http', function ($rootScope, $http) {
                this.isGamesListLoading = function(){
                    if ($rootScope.lobbyList == null) {
                            return true;}
                         else {
                            return false;
                        }
                    };
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
                    if ($rootScope.lobbyList != null) {
                        if ($rootScope.lobbyList.length === 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                };

                this.gameType = function(password) {
                    if (password == false) {
                        return "Public";
                    } else {
                        return "Private";
                    }
                };

                this.quickStart = function(){
                    var quickStartData = JSON.stringify({Command: "QuickStart", Data: {}});
                    postData(quickStartData);
                    $rootScope.troopCountersFirst($rootScope);
                };
                   
                this.setGameName= function () {
                    var gameName = document.getElementById("create-text").value;
                    var password = document.getElementById("password-text").value;
                    var startGameData = JSON.stringify({Command: "Create", Data: {GameName: gameName, GamePassword: password}});
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
                    var quitGame = JSON.stringify({Command: "Quit", Data: {}});
                    postData(quitGame);
                    $rootScope.obj = null;
                };

                this.joinGame = function (gameID) {
                    var password = document.getElementById("private-text").value;
                    var startGameData = JSON.stringify({Command: "Join", Data: {GameID: gameID, GamePassword: password}});
                    postData(startGameData);
                };

                this.logOut = function () {
                    if ($rootScope.obj!=null) {
                        this.quitGame();
                    }
                    var logoutData = JSON.stringify({Command:"Logout",Data:{}});
                    postData(logoutData);
                    $rootScope.obj = null;
                    $rootScope.nameObj = null;
                    $rootScope.lobbyObj = null;
                    $rootScope.chatObj = null;
                    $rootScope.userName = null;
                };

                this.delCookie = function () {
                    name.replace('Username=', '');
                    var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: $rootScope.thisPlayer}});
                    document.cookie = 'Username=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
                    postData(quitData);
                };

                this.sendChat = function () {
                    var chatString = document.getElementById("chatbox").value.trim();
                    if (chatString) {
                        var chatData = JSON.stringify({Data: {Username: $rootScope.userName, Message: chatString}});
                    $rootScope.chatMessages.push({"Username": $rootScope.userName,"Message": chatString});
                        $http({method: 'POST', url: 'ChatServlet', headers: {'Content-Type': 'application/json'}, data: chatData}).success(function () {
                            document.getElementById("chatbox").value = "";
                        });
                    }
                };

                this.quickStartButton = function () {
                    console.log("QUICKSTART MODE");
                    var quickStartData = JSON.stringify({Command: "QuickStart", Data: {}});
                    postData(quickStartData);
                };

                function readCookie() {
                    var x = document.cookie;
                    var keyArray = x.split("; ");
                    return keyArray;
                };

                function postData(data) {
                    $http({
                        method: 'POST',
                        url: 'GameServlet',
                        headers: {'Content-Type': 'application/json'},
                        data: data
                    }).success(function (output) {
                        $rootScope.lobbyObj = output;
                    });
                }

            }]);