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
            $rootScope.players = $rootScope.obj.Game.Players;
            $rootScope.gameStarted = $rootScope.obj.Game.GameState.LobbyClosed;
            $rootScope.countryCount = $rootScope.obj.Game.GameState.Unassigned;
            $rootScope.phase = $rootScope.obj.Game.GameState.Phase;
            $rootScope.lobbySize = 6;
            $rootScope.playerString = "player";
            $rootScope.CurrentPlayer = $rootScope.obj.Game.GameState;
            var cookie = readCookie();
            for (index in cookie) {
                var name = cookie[index];
            }
            $rootScope.username=name.replace('Username=', '');

            if ($rootScope.obj.Game.Players.length !== 0) {
                $rootScope.host = $rootScope.obj.Game.Players[0].DisplayName;
            }

            for (var i = 0; i < $rootScope.obj.Game.Players.length; i++) {
                if ($rootScope.obj.Game.Players[i].DisplayName === $rootScope.username) {
                    $rootScope.thisUserNumber = i;
                }

                if ($rootScope.obj.Game.GameState.CurrentPlayer === $rootScope.obj.Game.Players[i].PlayerOrder){
                    $rootScope.currentUserName = $rootScope.obj.Game.Players[i].DisplayName;
                    $rootScope.troopsToDeploy = $rootScope.obj.Game.Players[i].TroopsToDeploy;
                }
            }

            if ($rootScope.host === $rootScope.username) {
                if ($rootScope.obj.Game.GameState.Phase === "Setup" && $rootScope.obj.Game.GameState.Unassigned === 0) {
                    var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                    postData(endPhaseData);
                }
            }
            //sends out end phase when the last player has finished deploying
            if ($rootScope.obj.Game.GameState.Phase === "Deploy" && $rootScope.obj.Game.Players[$rootScope.obj.Game.Players.length - 1].TroopsToDeploy === 0) {
                var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                postData(endPhaseData);
            }
            //if current players trooptodeploy has diminished, switch player
            if($rootScope.obj.Game.GameState.Phase==="Deploy" && $rootScope.obj.Game.Players[$rootScope.obj.Game.GameState.CurrentPlayer].TroopsToDeploy===0) {
                var endTroopDeployData = JSON.stringify({Command: "EndTurn", Data: {CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                postData(endTroopDeployData);
            }
            $rootScope.$apply();
            angular.forEach($rootScope.obj.Game.Board, function (index) {
                countryOwner[index.Owner].push($rootScope.mapList[index.CountryID]);
            });

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
                var keyArray = x.split("; ");
                return keyArray;
            }
//            color($rootScope);
        }, false);
    }]);

////    function color($rootScope) {
////        angular.forEach($rootScope.obj.Game.Board, function (country) {
////            if (country.Owner === -1) {
////                $rootScope.mapList[country.CountryID].attr(blackCountry);
////            }
////            else if (country.Owner === 0) {
////                $rootScope.mapList[country.CountryID].attr(redCountry);
////            }
////            else if (country.Owner === 1) {
////                $rootScope.mapList[country.CountryID].attr(greenCountry);
////            }
////            else if (country.Owner === 2) {
////                $rootScope.mapList[country.CountryID].attr(yellowCountry);
////            }
////            else if (country.Owner === 3) {
////                $rootScope.mapList[country.CountryID].attr(pinkCountry);
////            }
////            else if (country.Owner === 4) {
////                $rootScope.mapList[country.CountryID].attr(brownCountry);
////            }
////            else if (country.Owner === 5) {
////                $rootScope.mapList[country.CountryID].attr(blueCountry);
////            }
////        });
////   }

    function moveTroops(troops, id1, id2) {
        angular.forEach($rootScope.obj.Game.Board, function (index) {
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

    var countryOwner = {};
    countryOwner["-1"] = [];
    countryOwner["0"] = [];
    countryOwner["1"] = [];
    countryOwner["2"] = [];
    countryOwner["3"] = [];
    countryOwner["4"] = [];
    countryOwner["5"] = [];

    var MapWidth = 900;
    var MapHeight = 900;
    var animationSpeed = 500;

    var defaultCountry = {
        fill: "#ddd",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

    var blackCountry = {
        fill: "black",
        stroke: "#aaa",
        "stroke-width": 1,
        "stroke-linejoin": "round",
        cursor: "pointer"
    };

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

//    map.setViewBox(0, innerHeight / 2, window.innerWidth / 1.5, window.innerHeight / 1.5, true);
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