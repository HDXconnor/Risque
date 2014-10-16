(function () {
    var app = angular.module("gameApp", []).config(function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    });

    var evtSource = new EventSource("GameServlet");
    var chatSource = new EventSource("ChatServlet");

    app.run(['$rootScope', '$http', function ($rootScope, $http) {
            $rootScope.userName = null;
            $rootScope.amountTroopsToMove = 0;
            $rootScope.gameStarted = false;
            evtSource.addEventListener("username", function (e) {
                $rootScope.nameObj = JSON.parse(e.data);
                $rootScope.userName = $rootScope.nameObj.Username;
                $rootScope.$apply();
            }, false);

            evtSource.addEventListener("gamelist", function (e) {
                $rootScope.lobbyObj = JSON.parse(e.data);
                $rootScope.lobbyList = $rootScope.lobbyObj.GameList;
                $rootScope.$apply();
            }, false);

            chatSource.addEventListener("messages", function (e) {
                $rootScope.chatObj = JSON.parse(e.data);
                $rootScope.chatMessages = $rootScope.chatObj.ChatMessages;
                $rootScope.gameMessages = $rootScope.chatObj.GameMessages;
                if($rootScope.gameMessages.length-1 != $rootScope.maxMessage) {
                    $rootScope.currentNotification = $rootScope.gameMessages.length-1;
                }
                $rootScope.maxMessage = $rootScope.gameMessages.length-1;

                $rootScope.$apply();
            }, false);

        $rootScope.goForward = function() {
            if ($rootScope.currentNotification != $rootScope.gameMessages.length-1) {
                $rootScope.currentNotification += 1;
            }
        };

        $rootScope.goBack = function () {
            if ($rootScope.currentNotification != 0) {
                $rootScope.currentNotification -= 1;
            }
        };
            $rootScope.goingToAuth = false;
            window.onbeforeunload = function (e) {
                // This function works, but runs when trying to login with oauth. Removed for now.
                console.log($rootScope.goingToAuth);
                if ($rootScope.goingToAuth !== true) {
                    if ($rootScope.obj !== null) {
                        var quitData = JSON.stringify({Command: "Quit", Data: {}});
                        $http({method: 'POST', url: 'GameServlet', headers: {'Content-Type': 'application/json'}, data: quitData}).error();
                        $rootScope.obj = null;
                    }
                    var logoutData = JSON.stringify({Command: "Logout", Data: {}});
                    $rootScope.userName = null;
                    $http({method: 'POST', url: 'GameServlet', headers: {'Content-Type': 'application/json'}, data: logoutData}).error();
                    for (i = 0; i <= 1000; i++) {
                        console.log("There's probably a better way to do this");
                    }
                }
                return;
            };

            evtSource.addEventListener("gamestate", function (e) {
                $rootScope.obj = JSON.parse(e.data);
                $rootScope.Game = $rootScope.obj.Game;
                $rootScope.GameState = $rootScope.Game.GameState;
                $rootScope.players = $rootScope.Game.Players;
                $rootScope.board = $rootScope.Game.Board;
                $rootScope.gameStarted = $rootScope.GameState.LobbyClosed;
                $rootScope.countryCount = $rootScope.GameState.Unassigned;
                $rootScope.phase = $rootScope.GameState.Phase;
                $rootScope.CurrentPlayer = $rootScope.GameState.CurrentPlayer;
                $rootScope.lobbySize = 6;
                $rootScope.playerString = "player";

                //sets first player to host
//                if ($rootScope.players.length !== 0) {
//                    $rootScope.host = $rootScope.players[0].DisplayName;
//                }
                
                for (var i = 0; i < $rootScope.players.length ; i++) {
                    if ($rootScope.players[i].DisplayName === $rootScope.userName) {
                        $rootScope.thisUserNumber = i;
                    }                  

                    if ($rootScope.CurrentPlayer === $rootScope.players[i].PlayerOrder) {
                        $rootScope.currentUserName = $rootScope.players[i].DisplayName;
                        $rootScope.troopsToDeploy = $rootScope.players[i].TroopsToDeploy;
                        
                    }
                    if($rootScope.players[i].PlayerOrder==="0"){
                        $rootScope.host = $rootScope.players[i].DisplayName;
                    }
                }

                if ($rootScope.host === $rootScope.userName) {
                    if ($rootScope.phase === "Setup" && $rootScope.countryCount === 0) {
                        var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                        postData(endPhaseData);
                        $rootScope.phase = "Deploy";
                    }
                }

                //end phase when last troop deployed
                if ($rootScope.phase === "Deploy" && $rootScope.players[$rootScope.CurrentPlayer].TroopsToDeploy === 0) {
                    var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                    postData(endPhaseData);
                    $rootScope.phase = "Attack";
                }

                function postData(data) {
                    $http({
                        method: 'POST',
                        url: 'GameServlet',
                        headers: {'Content-Type': 'application/json'},
                        data: data
                    }).success(function (output) {
                        $rootScope.obj = output;                        
                    });
                };

                color($rootScope);

                $rootScope.$apply();
            }, false);
        }]);


    function color($rootScope) {
        angular.forEach($rootScope.board, function (country) {
            if (country.Owner === -1) {
                $rootScope.mapList[country.CountryID].attr("fill", "black");
                $rootScope.numberList[country.CountryID].text("0")
                    .attr("fill", "white");
            }
            else if (country.Owner === 0) {
                $rootScope.mapList[country.CountryID].attr("fill", "#FF3B30");
                $rootScope.numberList[country.CountryID].text(country.Troops)
                    .attr("fill", "black");
            }
            else if (country.Owner === 1) {
                $rootScope.mapList[country.CountryID].attr("fill", "#00ff1b");
                $rootScope.numberList[country.CountryID].text(country.Troops)
                    .attr("fill", "black");
            }
            else if (country.Owner === 2) {
                $rootScope.mapList[country.CountryID].attr("fill", "#66FFE7");
                $rootScope.numberList[country.CountryID].text(country.Troops)
                    .attr("fill", "black");
            }
            else if (country.Owner === 3) {
                $rootScope.mapList[country.CountryID].attr("fill", "#4A4A4A");
                $rootScope.numberList[country.CountryID].text(country.Troops)
                    .attr("fill", "black");
            }
            else if (country.Owner === 4) {
                $rootScope.mapList[country.CountryID].attr("fill", "#6f3ed6");
                $rootScope.numberList[country.CountryID].text(country.Troops)
                    .attr("fill", "black");
            }
            else if (country.Owner === 5) {
                $rootScope.mapList[country.CountryID].attr("fill", "#007AFF");
                $rootScope.numberList[country.CountryID].text(country.Troops)
                    .attr("fill", "black");
            }
        });
    }

    app.directive("rightBox", function () {
        return{
            restrict: "E",
            templateUrl: "html/right-box.html"
        };
    });
    app.directive("leftBox", function () {
        return{
            restrict: "E",
            templateUrl: "html/left-box.html"
        };
    });
    app.directive("gameLobby", function () {
        return{
            restrict: "E",
            templateUrl: "html/game-lobby.html"
        };
    });
    app.directive("loginBox", function () {
        return{
            restrict: "AEC",
            templateUrl: "html/login-box.html"
        };
    });
})();