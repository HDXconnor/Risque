(function () {
    var app = angular.module("gameApp", []).config(function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    });

    var evtSource = new EventSource("GameServlet");
    var me;

    app.run(['$rootScope', '$http', function ($rootScope, $http) {
        $rootScope.userName = null;
        evtSource.addEventListener("username", function (e) {
            $rootScope.nameObj = JSON.parse(e.data);
            $rootScope.userName = $rootScope.nameObj.Username;
            $rootScope.$apply();
        }, false);
        
        evtSource.addEventListener("gamelist", function (e) {
            $rootScope.lobbyObj = JSON.parse(e.data);
//            $rootScope.lobbyList = $rootScope.lobbyObj[0].GameName;
            $rootScope.$apply();
        }, false);

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
            if ($rootScope.players.length !== 0) {
                $rootScope.host = $rootScope.players[0].DisplayName;
            }

            for (var i = 0; i < $rootScope.players.length; i++) {
                if ($rootScope.players[i].DisplayName === $rootScope.userName) {
                    $rootScope.thisUserNumber = i;
                }
                else {
                    console.log("NO USER NUMBER FOR YOU!");
                }

                if ($rootScope.CurrentPlayer === $rootScope.players[i].PlayerOrder) {
                    $rootScope.currentUserName = $rootScope.players[i].DisplayName;
                    $rootScope.troopsToDeploy = $rootScope.players[i].troopsToDeploy;
                }
            }

            if ($rootScope.host === $rootScope.userName) {
                if ($rootScope.phase === "Setup" && $rootScope.countryCount === 0) {
                    var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                    postData(endPhaseData);
                }
            }
            //sends out end phase when the last player has finished deploying
            if ($rootScope.phase === "Deploy" && $rootScope.players[$rootScope.players.length - 1].troopsToDeploy === 0) {
                var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                postData(endPhaseData);
            }
            //if current players trooptodeploy has diminished, switch player
            if ($rootScope.phase === "Deploy" && $rootScope.players[$rootScope.CurrentPlayer].troopsToDeploy === 0) {
                var endTroopDeployData = JSON.stringify({Command: "EndTurn", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                postData(endTroopDeployData);
            }
            $rootScope.$apply();

            function postData(data) {
                $http({
                    method: 'POST',
                    url: 'GameServlet',
                    headers: {'Content-Type': 'application/json'},
                    data: data
                }).error();
            }
            function discoverOrderNo(name) {
                angular.forEach($rootScope.players, function (index) {
                    if (index.DisplayName === name) {
                        me = index.PlayerOrder;
                    }
                });
            }
            color($rootScope);
        }, false);
    }]);

    function color($rootScope) {
        angular.forEach($rootScope.board, function (country) {
            if (country.Owner === -1) {
                $rootScope.mapList[country.CountryID].attr("fill", "black")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
            }
            else if (country.Owner === 0) {

                $rootScope.mapList[country.CountryID].attr("fill", "#FF3B30")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
            }
            else if (country.Owner === 1) {

                $rootScope.mapList[country.CountryID].attr("fill", "#00ff1b")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
            }
            else if (country.Owner === 2) {
                $rootScope.mapList[country.CountryID].attr("fill", "#ff5400")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
            }
            else if (country.Owner === 3) {
                $rootScope.mapList[country.CountryID].attr("fill", "#4A4A4A")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
            }
            else if (country.Owner === 4) {
                $rootScope.mapList[country.CountryID].attr("fill", "#6f3ed6")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
            }
            else if (country.Owner === 5) {
                $rootScope.mapList[country.CountryID].attr("fill", "#007AFF")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
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