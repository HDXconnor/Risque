(function () {
    var app = angular.module("gameApp", [
        'ngCookies'
    ]).config(function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    });

    var evtSource = new EventSource("GameServlet");

    app.run(['$rootScope', '$http', function ($rootScope, $http) {
        evtSource.addEventListener("gamestate", function (e) {
            $rootScope.obj = JSON.parse(e.data);
            $rootScope.Game = $rootScope.obj.Game;
            $rootScope.players = $rootScope.Game.Players;
            $rootScope.gameStarted = $rootScope.Game.GameState.LobbyClosed;
            $rootScope.countryCount = $rootScope.Game.GameState.Unassigned;
            $rootScope.phase = $rootScope.Game.GameState.Phase;
            $rootScope.lobbySize = 6;
            $rootScope.playerString = "player";
            $rootScope.CurrentPlayer = $rootScope.Game.GameState;
            $rootScope.board = $rootScope.Game.Board;


            console.log($rootScope.Game + "GAME!");
            var cookie = readCookie();
            console.log(cookie);
            var name = cookie["Username="];
            console.log(name);
//            $rootScope.username=name.replace('Username=', '');


            //sets player 1 to host
            if ($rootScope.players.length !== 0) {
                console.log("host name:? " +$rootScope.players[0].DisplayName);
                $rootScope.host = $rootScope.players[0].name;
                console.log("host DisplayName: " +$rootScope.players[0].DisplayName);
            }


            for (var i = 0; i < $rootScope.players.length; i++) {
                console.log("DisplayName Check: "+$rootScope.players[i].DisplayName);
                console.log("Username Check: "+$rootScope.username);
                if ($rootScope.players[i].DisplayName === $rootScope.username) {
                    $rootScope.thisUserNumber = i;
                }
                else{console.log("NO USER NUMBER FOR YOU!");
                }

                if ($rootScope.CurrentPlayer === $rootScope.players[i].PlayerOrder){
                    $rootScope.currentUserName = $rootScope.players[i].DisplayName;
                    $rootScope.troopsToDeploy = $rootScope.players[i].troopsToDeploy;
                }
            }

            if ($rootScope.host === $rootScope.username) {
                if ($rootScope.Game.GameState.Phase === "Setup" && $rootScope.Game.GameState.Unassigned === 0) {
                    var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                    postData(endPhaseData);
                }
            }
            //sends out end phase when the last player has finished deploying
            if ($rootScope.Game.GameState.Phase === "Deploy" && $rootScope.players[$rootScope.players.length - 1].troopsToDeploy === 0) {
                var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                postData(endPhaseData);
            }
            //if current players trooptodeploy has diminished, switch player
            if($rootScope.Game.GameState.Phase==="Deploy" && $rootScope.players[$rootScope.CurrentPlayer].troopsToDeploy===0) {
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
            function readCookie() {
                var x = document.cookie;
                return x.split("; ");
            }
//            color($rootScope);
        }, false);
    }]);

    function color($rootScope) {
        angular.forEach($rootScope.Game.Board, function (country) {
            if (country.Owner === -1) {
                $rootScope.mapList[country.CountryID].attr("fill", "black")
                    .attr("stroke", "#aaa")
                    .attr("stroke-width", 1)
                    .attr("stroke-linejoin", "round")
                    .attr("cursor", "pointer");
            }
            else if (country.Owner === 0) {
                $rootScope.mapList[country.CountryID].attr(redCountry);
            }
            else if (country.Owner === 1) {
                $rootScope.mapList[country.CountryID].attr(greenCountry);
            }
            else if (country.Owner === 2) {
                $rootScope.mapList[country.CountryID].attr(yellowCountry);
            }
            else if (country.Owner === 3) {
                $rootScope.mapList[country.CountryID].attr(pinkCountry);
            }
            else if (country.Owner === 4) {
                $rootScope.mapList[country.CountryID].attr(brownCountry);
            }
            else if (country.Owner === 5) {
                $rootScope.mapList[country.CountryID].attr(blueCountry);
            }
        });
   }

    function moveTroops(troops, id1, id2) {
        angular.forEach($rootScope.Game.Board, function (index) {
            if (index.CountryID === id1) {
                index.Troops = index.Troops - troops;
            }
            if (index.CountryID === id2) {
                index.Troops = index.Troops + troops;
            }
        });
    }

    function deploy(troops, id1) {
        angular.forEach($rootScope.obj.Game.Board, function (index) {
            if (index.CountryID === id1) {
                index.Troops = index.Troops + troops;
            }
        });
    }

    function writeCookie(key, value) {
        document.cookie = key + "=" + value + "; ";
    }

    function appendCookie(key, value) {
        var x = document.cookie;
        document.cookie = x + key + "=" + value + "; ";
    }

    function webSockConnect() {
        var socketURI = "ws://" + document.location.host + "GameSocket";
        var ws = new WebSocket(socketURI);

        ws.onmessage = function (evt) {
            webSockRecv(evt);
        };

        ws.onerror = function (evt) {
            console.log(evt);
        };

        return ws;
    }

    function webSockSend(json) {
        if (conn.readyState !== 1) {
            conn.send(json);
        } else {
            console.log("Not connected to websocket, cannot send.");
        }
    }

    function webSockRecv(evt) {
        console.log("Server says " + evt.data());
    }

    var redCountry = {
        fill: "#FF3B30",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

    var yellowCountry = {
        fill: "#ff5400",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

    var pinkCountry = {
        fill: "#4A4A4A",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

    var brownCountry = {
        fill: "#6f3ed6",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

    var greenCountry = {
        fill: "#00ff1b",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

    var blueCountry = {
        fill: "#007AFF",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

    var hoverCountry = {
        fill: "#fff",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };


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