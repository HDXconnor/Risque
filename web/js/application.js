(function () {
    var app = angular.module("gameApp", ['ngCookies']).config(function ($httpProvider) {
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
                $rootScope.phase=$rootScope.obj.Game.GameState.Phase;
                $rootScope.lobbySize = 6;
                $rootScope.playerString = "player";
                var cookie = readCookie();
                for (index in cookie) {
                    var name = cookie[index];
                }
                $rootScope.username=name.replace('Username=', '');
                
                if ($rootScope.obj.Game.Players.length !== 0) {
                    $rootScope.host = $rootScope.obj.Game.Players[0].DisplayName;
                }
                
                
                
                for (i = 0; i < $rootScope.obj.Game.Players.length; i++) {
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
                        var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: name}});
                        postData(endPhaseData);
                    }
                }
                //sends out end phase when the last player has finished deploying
                if ($rootScope.obj.Game.GameState.Phase === "Deploy" && $rootScope.obj.Game.Players[$rootScope.obj.Game.Players.length - 1].TroopsToDeploy === 0) {
                    var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: name}});
                    postData(endPhaseData);
                }
                //if current players trooptodeploy has diminished, switch player
                if($rootScope.obj.Game.GameState.Phase==="Deploy" && $rootScope.obj.Game.Players[$rootScope.obj.Game.GameState.CurrentPlayer].TroopsToDeploy===0) {
                    var endTroopDeployData = JSON.stringify({Command: "EndTurn", Data: {CurrentPlayer: name}});
                    postData(endTroopDeployData);
                }
                $rootScope.$apply();
                angular.forEach($rootScope.obj.Game.Board, function (index) {
                    countryOwner[index.Owner].push(mapList[index.CountryID]);
                });
                
                function postData(data) {
                    $http({
                        method: 'POST',
                        url: 'GameServlet',
                        headers: {'Content-Type': 'application/json'},
                        data: data
                    }).error();
                }
                color($rootScope);
            }, false);
        }]);

    app.controller("LoginController", ['$rootScope', '$cookieStore', '$http', function ($rootScope, $cookieStore, $http) {
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
                for (index in cookie) {
                    var name = cookie[index];
                }
                name = name.replace('Username=', '');
                var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: name}});
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
        }]);

    app.controller("LobbyController", ['$rootScope', '$http', function ($rootScope, $http) {
            
            this.lobbyVis = function () {
                return(document.cookie.indexOf("Username") >= 0 && $rootScope.obj.Game.GameState.LobbyClosed !== true);
            };

            this.startGame = function () {
                var startGameData = JSON.stringify({Command: "StartGame", Data: {CurrentPlayer: name}});
                postData(startGameData);
            };

            this.delCookie = function () {
                var cookie = readCookie();
                for (index in cookie) {
                    var name = cookie[index];
                }
                name = name.replace('Username=', '');
                var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: name}});
                document.cookie = "Username=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
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
            
            function postData(data) {
                $http({
                    method: 'POST',
                    url: 'GameServlet',
                    headers: {'Content-Type': 'application/json'},
                    data: data
                }).error();
            }
        }]);

    app.controller("GameController", ['$rootScope', '$http', function ($rootScope, $http) {

            this.endphase = function () {
                // Deploy -> Attack -> Move
                if ($rootScope.obj.Game.GameState.Phase === "Setup" || $rootScope.obj.Game.GameState.Phase === "Deploy")
                    $rootScope.obj.Game.GameState.Phase = "Attack";
                else if ($rootScope.obj.Game.GameState.Phase === "Attack")
                    $rootScope.obj.Game.GameState.Phase = "Move";
                else if ($rootScope.obj.Game.GameState.Phase === "Move") {
//                moveTroops();
                    $rootScope.obj.Game.GameState.Phase = "Deploy";
                    // increment currentplayer, mod number of players
                    $rootScope.obj.Game.GameState.CurrentPlayer= ($rootScope.obj.Game.GameState.CurrentPlayer + 1) % $rootScope.obj.Game.Players.length;
                    $rootScope.obj.Game.GameState.CurrentPlayer = $rootScope.obj.Game.Players[$rootScope.obj.Game.GameState.CurrentPlayer].PlayerOrder;
                }
            };

            this.endPhaseVis = function () {
                if ($rootScope.obj.Game.GameState.Unassigned!== 0) {
                    return true;
                } else {
                    return false;
                }
            };
            this.endTurn = function(){
                var endTurnData = JSON.stringify({Command: "endTurn", Data: {CurrentPlayer: name}});
                postData(endTurnData);
            };

            function postData(data) {
                $http({
                    method: 'POST',
                    url: 'GameServlet',
                    headers: {'Content-Type': 'application/json'},
                    data: data
                }).error();
            }
            
            
        }]);

    app.controller("PhaseController", ["$rootScope", '$http', function ($rootScope, $http) {
            this.atkBoxes = function () {
                if ($rootScope.obj.Game.GameState.Phase === "Attack") {
                    return $rootScope.isHidden;
                } else {
                    return true;
                }
            };

            this.deployBoxes = function () {
                if ($rootScope.obj.Game.GameState.Phase === "Deploy") {
                    return $rootScope.isHidden;
                } else {
                    return true;
                }
            };

            this.reinfBoxes = function () {
                if ($rootScope.obj.Game.GameState.Phase === "Move") {
                    return $rootScope.isHidden;
                } else {
                    return true;
                }
            };
            this.attack = function(){
                var send = JSON.stringify({Command: "Attack", Data: {AttackingCountry: $rootScope.attackCountryID, DefendingCountry: $rootScope.defendCountryID, CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                            postData(send);
            };
            function postData(data) {
                $http({
                    method: 'POST',
                    url: 'GameServlet',
                    headers: {'Content-Type': 'application/json'},
                    data: data
                }).error();
            };
        }]);
    
    app.controller("MapController", ["$rootScope", '$http', function ($rootScope, $http) {

            angular.forEach(mapList, function (index) {

                index[0].addEventListener("mouseover", function () {
                    $rootScope.thisCountryID = index.node.id;
                    angular.forEach($rootScope.obj.Game.Board, function (index) {
                        if ($rootScope.thisCountryID === index.CountryID) {
                            $rootScope.countryName = index.CountryName;
                            $rootScope.owner = index.Owner;
                            $rootScope.troops = index.Troops;
                        }
                        angular.forEach($rootScope.obj.Game.Players, function (player) {
                            if ($rootScope.owner === player.PlayerOrder) {
                                $rootScope.playerName = player.DisplayName;
                            }
                        });
                    });
                    angular.forEach($rootScope.obj.Game.Board, function (index){
                        if ($rootScope.thisCountryID === index.CountryID) {
                        if($rootScope.obj.Game.GameState.CurrentPlayer!==index.Owner){
                            $rootScope.defendCountryName = index.CountryName;
                            $rootScope.defendOwner = index.Owner;
                            $rootScope.defendTroops = index.Troops;
                        }else{
                            $rootScope.attackCountryName = index.CountryName;
                            $rootScope.attackOwner = index.Owner;
                            $rootScope.attackTroops = index.Troops;
                        }}
                        angular.forEach($rootScope.obj.Game.Players, function (player) {
                            if ($rootScope.attackOwner === player.PlayerOrder) {
                                $rootScope.attackOwner = player.DisplayName;
                            };
                            if($rootScope.defendOwner === player.PlayerOrder){
                                    $rootScope.defendOwner = player.DisplayName;
                            }
                                
                        });
                    });
                    
                }, true);

                index[0].addEventListener("mouseout", function () {
                }, true);

                index[0].addEventListener("click", function () {
                    $rootScope.thisCountryID = index.node.id;

                    if ($rootScope.obj.Game.GameState.CurrentPlayer === $rootScope.thisUserNumber) {
                        index.animate(defaultCountry, animationSpeed);
                        if ($rootScope.obj.Game.GameState.Phase === "Setup") {
                            var send = JSON.stringify({Command: "Setup", Data: {CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                            postData(send);
                        }
                        if ($rootScope.obj.Game.GameState.Phase === "Deploy") {
                            var send = JSON.stringify({Command: "Deploy", Data: {Troops: 1, CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                            postData(send);
                        }
                        if ($rootScope.obj.Game.GameState.Phase === "Attack") {
                            angular.forEach($rootScope.obj.Game.Board, function (index){
                                if ($rootScope.thisCountryID === index.CountryID) {
                                if($rootScope.obj.Game.GameState.CurrentPlayer === index.Owner){
                                    $rootScope.attackCountryID = $rootScope.thisCountryID;
                                    console.log("attack");
                                }
                                if($rootScope.obj.Game.GameState.CurrentPlayer !== index.Owner){
                                    $rootScope.defendCountryID = $rootScope.thisCountryID;
                                    console.log("defend");
                                }
                            }
                            });
                            
                            
                        }
                        if ($rootScope.obj.Game.GameState.Phase === "Move") {
                            var send = JSON.stringify({Command: "Move", Data: {SourceCountry: $rootScope.prevCountryID, CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                            postData(send);
                        }
                        $rootScope.prevCountryID = index.node.id;

                    }
                    else {
                        console.log("Not your turn");
                    }
                }, true);
            });

            function postData(data) {
                $http({
                    method: 'POST',
                    url: 'GameServlet',
                    headers: {'Content-Type': 'application/json'},
                    data: data
                }).error();
            }
            
            
        }]);




    function color($rootScope) {

        angular.forEach($rootScope.obj.Game.Board, function (country) {
            if (country.Owner === -1) {
                mapList[country.CountryID].attr(blackCountry);
            }
            else if (country.Owner === 0) {
                mapList[country.CountryID].attr(redCountry);
            }
            else if (country.Owner === 1) {
                mapList[country.CountryID].attr(greenCountry);
            }
            else if (country.Owner === 2) {
                mapList[country.CountryID].attr(yellowCountry);
            }
            else if (country.Owner === 3) {
                mapList[country.CountryID].attr(pinkCountry);
            }
            else if (country.Owner === 4) {
                mapList[country.CountryID].attr(brownCountry);
            }
            else if (country.Owner === 5) {
                mapList[country.CountryID].attr(blueCountry);
            }
        });
    };

    function moveTroops(troops, id1, id2) {
        angular.forEach($rootScope.obj.Game.Board, function (index) {
            if (index.CountryID === id1) {
                index.Troops = index.Troops - troops;
            }
            if (index.CountryID === id2) {
                index.Troops = index.Troops + troops;
            }
        });
    };

    function deploy(troops, id1) {
        angular.forEach($rootScope.obj.Game.Board, function (index) {
            if (index.CountryID === id1) {
                index.Troops = index.Troops + troops;
            }
        });
    };
    
    function postData(data) {
        $http({
            method: 'POST',
            url: 'GameServlet',
            headers: {'Content-Type': 'application/json'},
            data: data
        }).error();
    };
    
    function writeCookie(key, value) {
        document.cookie = key + "=" + value + "; ";
    };

    function readCookie() {
        var x = document.cookie;
        var keyArray = x.split("; ");
        return keyArray;
    };

    function appendCookie(key, value) {
        var x = document.cookie;
        document.cookie = x + key + "=" + value + "; ";
    };

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
    };

    function webSockSend(json) {
        if (conn.readyState !== 1) {
            conn.send(json);
        } else {
            console.log("Not connected to websocket, cannot send.");
        }
    };

    function webSockRecv(evt) {
        console.log("Server says " + evt.data());
    };

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

    var map = new Raphael("map", "100%", "100%");
    var mapList = {};

    mapList["AS01"] = map.path("M291.607,211.668c-2.727,0.333-10.94,4.667-13.273,7s-11.878,6.08-16.211,7.747c-1.084,0.417-8.289,0.837-9.789,0.587c-2-0.333-1.667-3,0-6s6-7.334,6-11.667s0.666-0.667,2.833-9.501s12.045-14.157,12.5-16.667c1.458-8.041,4.333-14.333,11.5-17.833s11.666,3.166,17.5,1.333s14.5-8.166,15.167-13.333s8.5-12.667,13-18.167s-3-6.834-4.167-9s-1.667-11.5,0-9.333c0,0-3.001-2.833-4.668-2.499s-1.667-2.333-3-5.333s8.334-5,11.667-7s4-9,6.333-9.333s10.667-4.333,13-7.667s1.333-4.334,14.333-6.667s15.333-0.333,18.333,1.667l0,0c0,0,6.585,4.498,2.585,8.748s-16,4.75-19,10.75s-4,5.75,5.25,5.5c0,0-7,8.5-11.625,10.75s-14,10.875-16.125,15.125S331.625,154,327.625,159s-18.375,18.875-23.125,20.5s-13.625,2.625-10.375,8.625s9.625,5.625,3.125,13.75S291.607,211.668,291.607,211.668z");
    mapList["AS01"].node.id = "AS01";

    mapList["AS02"] = map.path("M371.5,105c9.25-0.25,15,7.5,15.5,10s2,10.25,8,9s5.75-8,8-9.5s14.5-5.75,16.25-4s9.25,6.977,11.75,5.738s13,1.262,15.25,4.262s10,15,11.5,16c0,0-13.083,10.167-20.25,12.5c-4.22,1.374-23.167,1.833-27.833,2.5s-14.5,7.5-30.167,7s-17.333,2.5-31.667,5.5s-20.208-5-20.208-5c4-5,14-23.875,16.125-28.125s11.5-12.875,16.125-15.125S371.5,105,371.5,105L371.5,105z");
    mapList["AS02"].node.id = "AS02";

    mapList["AS03"] = map.path("M291.607,211.668c2.727-0.333,10.061,0.667,11.727,0.333s3-5,10.333,0.333s9,1,10-0.333s8.755-9.455,22.333-2.667c0,0,10.25-18.834,10.25-22.334s-2-15.5,9.25-15.75s11.75-6.5,16.75-1.25s2.5,6.5,1.5,9.5s5.125,5.5,8.938,4.25s12.313-6,9.813-12s-5.25-10-0.75-11.5s9-0.833,9.25-2.833s-1.333-5.917-1.333-5.917l0,0c-4.667,0.667-14.5,7.5-30.167,7s-17.333,2.5-31.667,5.5s-20.208-5-20.208-5l0,0c-4,5-18.375,18.875-23.125,20.5s-13.625,2.625-10.375,8.625s9.625,5.625,3.125,13.75S291.607,211.668,291.607,211.668L291.607,211.668z");
    mapList["AS03"].node.id = "AS03";

    mapList["AS04"] = map.path("M488.784,175.476c-0.143-2.086,0.832-4.397,3.883-6.477c7.334-5,11-9.333,10-14.333s4.666-7.667,1.666-14s1.334-14.667-11-31.667s-7-13.667,7.667-17s0.333-8-1.667-8.333s-11.333-5.334-13.666-8s-26.334,0-28.667-1.333s0-4.833,0-4.833l0,0c-8.321-2.647,2.5,5-21.668,8.501c-3.364,0.487-11.831-3-11.582-8.333s3.751-12.667,0-12.667s-5.751,4.667-7.418,7.667s-5.333,5-9.667,5s-9.333,1.667-13,3.667s-8,8.667-11,6.667c0,0,6.585,4.498,2.585,8.748s-16,4.75-19,10.75s-4,5.75,5.25,5.5s15,7.5,15.5,10s2,10.25,8,9s5.75-8,8-9.5s14.5-5.75,16.25-4s9.25,6.977,11.75,5.738s13,1.262,15.25,4.262s10,15,11.5,16s-0.75,5-1,7.75s3.5,6.75,6.5,6.5s6.25,0.25,8.25,2s1.25,8,3.25,10s8.5,9.25,10,10.5S488.784,175.476,488.784,175.476z");
    mapList["AS04"].node.id = "AS04";

    mapList["AS05"] = map.path("M555.666,221.335c1.333-4,5.334-7.667,4-14.667s4.666-12,2.666-16.333s-1.333-7-3.333-10.333s-0.333-9-0.333-12.667s0.667-12.667,15-10s9.666-0.333,14.333,2.667s4.668,2,6.668,4.667s1.333,7.667,5.333,7.667s3.999-4.667,4.333-6.667s6.333-1.333,7-2.333l0.151-0.228c0,0,1.516-6.607-10.484-13.857s-16.75-8.25-19-12s-5.5-9.75,2.5-9.25s13.25-1,13.25-6.25s-7.75-10.5-10.75-19.5s3.333-6.582,3.333-6.582l0,0c1.503-2.576,6.209-5.771,3.667-11.668c-1.405-3.259-8.305-6.135-11.421-13.045c-7.667-17.001-30.346-25.211-38.912-24.953C510.5,47,517.906,39.917,509,41c-7.722,0.938-7,6.5-14.333,6.668C489.896,47.778,468,73,457,69.5c0,0-2.333,3.5,0,4.833s26.334-1.333,28.667,1.333s11.666,7.667,13.666,8s16.334,5,1.667,8.333s-20.001,0-7.667,17s8,25.334,11,31.667s-2.666,9-1.666,14s-2.666,9.333-10,14.333s-2.668,11.334,0.666,12.667S504,186,505,188s11,6,10.667,9.333c-0.241,2.415,11.667,0.333,12,3.333s0.333,3,0.333,3s2.544-3.363,5.755,0.667c5.578,7,8.378,17.807,9.912,19C546.667,225.667,555.666,221.335,555.666,221.335z");
    mapList["AS05"].node.id = "AS05";

    mapList["AS06"] = map.path("M590.333,95.668c-2.334,4,11,7.667,17.334,6.333s26.333-1.333,30.666,2s-1.666,7.667,8.667,16.667s5.667,9,4.333,13.333c-0.834,2.71,2.375,6.334,5.059,7.689l0,0c0,0-1.067,2.465-1.317,5.215s4.625,4.5,7.625,6.75s4.375,6.25,1.5,11.875s-4-0.875-8.75,0.625s-7.75-2.125-13-2.625s-9.115,1.805-9.115,1.805l0,0c-2.717-3.102-9-0.667-11.666-0.667c-2.059,0-6.704-1.392-9.02-1.564c-0.684-0.051-1.163,0.004-1.163,0.004S613,156.5,601,149.25s-16.75-8.25-19-12s-5.5-9.75,2.5-9.25s13.25-1,13.25-6.25s-7.75-10.5-10.75-19.5S590.333,95.668,590.333,95.668c-2.334,4,11,7.667,17.334,6.333s26.333-1.333,30.666,2s-1.666,7.667,8.667,16.667s5.667,9,4.333,13.333c-0.834,2.71,2.375,6.334,5.059,7.689");
    mapList["AS06"].node.id = "AS06";

    mapList["AS07"] = map.path("M485.667,211.333c0,4.334,2.947,14.37,2.614,19.037s-7.614,11.297-4.281,21.297c3.164,9.492-12.333,14.333-23.333,6S432.001,247,424.334,247s-8.667,6.334-11.667,8.335s-7.668-0.001-7.668-0.001l0,0c-1-3.333-1-8-3-10s-3-4-5.333-10s-8.333,2.666-15-5s-8.666-4.666-12.333-4.333c0,0,2.667-6.251,12.167-4.251s22.75-5.25,28-0.75c0,0,3.609,13,12.93,11.75s18.82-6.5,23.57-7s12-6,14.25-7.75s8.25-5.5,14.25-6.25S485.667,211.333,485.667,211.333L485.667,211.333z");
    mapList["AS07"].node.id = "AS07";

    mapList["AS08"] = map.path("M493.333,181.666c-1.947-0.778-4.349-3.262-4.549-6.19l0,0c0,0-2.534-0.976-4.034-2.226s-8-8.5-10-10.5s-1.25-8.25-3.25-10s-5.25-2.25-8.25-2s-6.75-3.75-6.5-6.5s2.5-6.75,1-7.75l0,0c0,0-13.083,10.167-20.25,12.5c-4.221,1.374-23.167,1.833-27.833,2.5l0,0l0,0c0,0,1.583,3.917,1.333,5.917s-4.75,1.333-9.25,2.833s-1.75,5.5,0.75,11.5s-6,10.75-9.813,12s-9.938-1.25-8.938-4.25s3.5-4.25-1.5-9.5s-5.5,1-16.75,1.25s-9.25,12.25-9.25,15.75S346,209.334,346,209.334l0,0c2.667,1.333-0.667,9,1.333,11s10.666-0.667,12.333,1.667s6,4.333,9.667,4l0,0c0,0,2.667-6.251,12.167-4.251s22.75-5.25,28-0.75c0,0,3.609,13,12.93,11.75s18.82-6.5,23.57-7s12-6,14.25-7.75s8.25-5.5,14.25-6.25s11.167-0.417,11.167-0.417l0,0l0,0c0-4.334,0-4.333,0-4.333s-0.021-12.412,7.666-14.333C497.334,191.667,493.333,181.666,493.333,181.666L493.333,181.666z");
    mapList["AS08"].node.id = "AS08";

    mapList["AS09"] = map.path("M555.666,221.335c-1.333,4,0,9,2,12s4,1.333,12.333,6.667s8.667,7.333,12.667,12.667s8,5.667,8.666,10.667s-3.333,6.667-1,11.333s3.667,11.333-1,13s-12.333,9.667-12.333,14.667s-3,10.667-7.333,11.333s-10.334-6.667-14.334-8.667s-5.333-7.667-3-11.667s0-7.667-2-10s-10.333-1-15-3.667c0,0,4.168-0.168-4.832-7.168s-18,1-15.5-6s2.5-7,2.5-7s2,0,2-2.5s4-10,8-12.5s12.304-11.528,2-20.477c-4.003-3.477-2.5-19.334-2.5-19.334l1-1.022c0,0,2.544-3.363,5.755,0.667c5.578,7,8.378,17.807,9.912,19C546.667,225.667,555.666,221.335,555.666,221.335L555.666,221.335z");
    mapList["AS09"].node.id = "AS09";

    mapList["AS10"] = map.path("M492.332,433.001c0,4.333,8.334,0.333,11.667-3s8.333-2.667,11.667-3s4.834-6.043,9.333-6c12.351,0.118,12.168,5.333,23.501,3.666s16.499,1.334,18.499,4.334s0.667,14.333,1,18s8.667,10,13,10s22,19,22.667,29s6.666,10.334,17,17s0.906,11.397,0.906,14.731s4.761,1.601,6.095,6.935s-13.335,21-15.668,23.667s-3.862,4.547-5,9l0,0c0,0-3.165-3.833-11.165,0s-17,11.499-17,11.499s-0.833-3.249-3.333-0.499s-8.416,1.999-19.25,1.333s-17.916-2.5-13.5-9s1.583-18.501-5.667-25.75s-1.583-12.415-2.25-16.082s-5.499-8.5-5.333-11.5s1.667-9.834-0.5-14.167s2.833-10.333,4.333-11s3.834-7.833,0.167-12.5s-3.667-4.667-3.667-4.667s3.333-0.999,2.5-2.666s-5.334-5.334-10.167-5.667s-4.166-2.833-8.333,0.667s-5.499,4.333-2.833,6.333l-18.167-11.167c0,0,3.499-2.5,1.166-5.5s-3-5.667-2.833-6.333S492.332,433.001,492.332,433.001z");
    mapList["AS10"].node.id = "AS10";

    mapList["AS11"] = map.path("M640.332,705.667c0.667-2,9.667-6.666,10.667-13s-3.333-19.334-3-25.334s0.333-13.667-6-18s-12.999-7-16.333-21.333s-7.666-2.667-8-8s4.999-10.999-0.334-16.333s-9.665-8.666-15.665-10.333s-15.667-4.666-13.001-6.333s17.869-4.255,19.666-6.334c2.347-2.716,10.002-6,0.462-9.992c-8.677-3.631-1.462-9.341-1.795-13.341c0,0-3.165-3.833-11.165,0s-17,11.499-17,11.499s-2.001,16.001-9.334,21.334s-15.667,17.666-20,17.666s-6.333,0.667-6.333,0.667s0.334,8.667,1.667,10.667s0,7.63-7,11.481s-9.667,9.186-9.667,12.519s-6.667,5.333-8,7.333s-4.001,8.667-10.667,12.667c0,0,2,4.333,0,6.083s-5,8.75,1.5,8.25s34-2,38.25,2s15,12,17,14s8,6.5,9.5,6.75s8.5-1.25,11,3.75s2.5,5,2.5,5s5.75-1.5,7.75,0s6,3.5,6.25,2.5s3.25-6.25,6.5-4.75s6.25-2,8.75-3.75s14.188-1.652,17.25-1.25C638.229,702.076,640.332,705.667,640.332,705.667z");
    mapList["AS11"].node.id = "AS11";

    mapList["EU01"] = map.path("M394.333,275.335c1-3.667,10.333-10,13.667-11s-2-5.667-3-9c0,0,4.667,2.001,7.667,0s4-8.335,11.667-8.335s25.333,2.334,36.333,10.667s26.497,3.492,23.333-6c-3.333-10,3.948-16.63,4.281-21.297s-2.614-14.703-2.614-19.037s0-4.333,0-4.333s-0.021-12.412,7.666-14.333c4.001-1,0-11,0-11C496.667,183,504,186,505,188s11,6,10.667,9.333c-0.241,2.415,11.667,0.333,12,3.333s0.333,3,0.333,3l0,0l-1,1.022c0,0-1.503,15.857,2.5,19.334c10.304,8.949,2,17.977-2,20.477s-8,10-8,12.5c0,0-1.584-1.499-2.667,0.084c-1.099,1.606,0.667,2.416,0.667,2.416s0,0-2.5,7s6.5-1,15.5,6s4.832,7.168,4.832,7.168l0,0c-4.667-2.667-6.333,0-12.666,0s-8.334-1.667-10-4.667s-6-1-7.334,3.333s7,10.667,12,10.667s11,2.333,8.667,6s-3.333,9-2.333,11s-1-2,10.666,5s2.334,7.667-3.333,9.667s-7.667-3.667-11.333-4.333s-11.334,2-15,1.667s-1.667-5.333-1.334-6.333s-4.666-4.667-8-4.667c0,0-3.082-9.751-7.832-8.501s-15.5,12.25-23,10.5s-13.5-4.25-19.75,0s-5,12.187-8,12.093c-3-0.094-3.75-2.343-3.5-3.343s5.25-5.75-3.5-6.5c0,0-3.5-5.25-1.25-8.75s2.25-8.25-4.25-11.25s-16.25-3.25-18.75-7s-9.583-1.25-11.167-3.75S394.333,275.335,394.333,275.335z");
    mapList["EU01"].node.id = "EU01";

    mapList["EU02"] = map.path("M426.25,343c0,0,2.375-1.375,3.125-2.75s4.5-0.875,6.5-1.625s1.75-7.125,1.75-7.125s2.125,0.25,3.25-2.75s3.625-5.125,1-6.125s-5.125-1.532-5.125-1.532c3,0.095,1.75-7.843,8-12.093s12.25-1.75,19.75,0s18.25-9.25,23-10.5s7.832,8.502,7.832,8.502l0,0c-3.334,0,2,7,2,11.667s-12,4.334-6,12s0.334,14.666-0.666,17.333s-13.334,4.333-15.667,5s-10.667,6.667-11,10s-9,5.666-12,8.333s-4.444,5.602-5,9c0,0-2.499-7.21-7.249-8.21s-8.125-1-10.75-1.625s-3.5-2.25-3.5-2.25s1.625-1.25,2.375-3.375s-2.625-4.125-2.375-6.75s-1.151-6.875-0.013-8.25s2.013-3.75,1.513-4.75S426.25,343,426.25,343z");
    mapList["EU02"].node.id = "EU02";

    mapList["EU03"] = map.path("M408.667,342.333l-2-3.333c0,0-6.667-2.333-8.833,0c0,0-1-1.833-0.5-3.333s1.333-1.833,3-2.5C402,332.5,402,332.5,402,332.5s0.834-0.667,0.167-5.167s-0.667-9.334-2.5-10.667s-4-8.667-3.5-11.167s-3.501-5.333-4.667-7.5s-3-6.833-3.166-8c-0.167-1.167-0.167-1.167-0.167-1.167s-2.833-0.667-3.833-2.667S382.625,274,382.625,274l0,0c5.333,0,8.875,4.688,11.708,1.335l0,0c0,0-1.584,2.665,0,5.165s8.667,0,11.167,3.75s12.25,4,18.75,7s6.5,7.75,4.25,11.25s1.25,8.75,1.25,8.75c8.75,0.75,3.75,5.5,3.5,6.5s0.5,3.249,3.5,3.343c0,0-2.625,2.532-1.375,4.407s0.5,5,1.375,5.5s0.875,0.5,0.875,0.5s0.25,6.375-1.75,7.125s-5.75,0.25-6.5,1.625S426.25,343,426.25,343s-1.125-2.25-3.5-1.125s-5.614,2.89-5.614,3.64S416.625,347,416.625,347s-2.25-3.5-3.625-3.75S408.667,342.333,408.667,342.333z");
    mapList["EU03"].node.id = "EU03";

    mapList["EU04"] = map.path("M405.667,350.333c1.167-1.667,1.333-7,2.167-7.5s0.833-0.5,0.833-0.5l0,0c0,0,2.958,0.667,4.333,0.917s3.625,3.75,3.625,3.75s-1.125,1.75,0,2.75s1.292,2.833,1.125,4.25s-0.083,4.167,1.167,4.833s1.084,2.083,1.417,4.833s2.25,5.083,2.083,6s-0.583,2.172-0.583,2.172c-1.667,3.417-3.917,3.833-5.167,4.5s-5.333,1.167-5.333,1.167s-3.083-1.672-2.833-3.755s1.75-4.833-0.5-4.833s-5.667,0.833-6.75-1.75s-2.167-7.75,0-9.333s3.917-1.167,4.5-2.667S405.667,350.333,405.667,350.333z");
    mapList["EU04"].node.id = "EU04";

    mapList["EU05"] = map.path("M379,411.5c1.25,0.75,5.75,3.75,8,3.75s14,0.5,17-0.5s5-3.5,11-1c0,0,1-1.75,3.25-1.583s5.167,1.667,6,0.917s2.583-2,1.25-5.917s-0.167-5.417,1.75-6.5s3.833-0.831,3.833-0.831c0.583-1.167,0.167-3.5,1.917-3.917s7.667-2,8.333-1.75s2.917,1.75,6-1.417s3.083-3.167,3.083-3.167l0,0c-1.906-3.718-3.236-7.439-3.417-9.251l0,0c0,0-2.499-7.21-7.249-8.21s-8.125-1-10.75-1.625s-3.5-2.25-3.5-2.25s-2,0.172-3.667,3.589s-3.917,3.833-5.167,4.5s-5.333,1.167-5.333,1.167s-1.25,2.667,0,3.667s4.25,2.917,4.917,3.5s2.417,5.948,0.5,7.141s-3.917,4.526-5.25,2.859s-2.25-5.083-3-6.833s-1.833-3.917-4.417-6.167s-2.667-4.583-5-4.75s-8.583-2.755-8.583-2.755l0,0c3,3.667,1.833,9.833,0,13.5s-7.875,1.833-7.875,1.833l0,0c0,0-1.708-0.167-3.125,0.417s-4.667,6.5-4.667,8.333s1.75,7,2.75,8.333S379,411.5,379,411.5L379,411.5z");
    mapList["EU05"].node.id = "EU05";

    mapList["EU06"] = map.path("M492.332,433.001c0,0-0.998,7-1.165,7.666c0,0-2.251-1.333-5.334-0.75s-8,0.583-9,1.667s-3.416,2.75-3.083,4.083s0.75,3.083,0.75,3.083c-1,1-2.5,4.75-4.25,6s-7.25-3-7.25-3s1.916-1.917,0.833-4.167s-1.916-3.833-4.166-5s-2.333-3.25-1-4.583s1.166-5.75,0.916-7.083s0.001-4.5-1.791-2.833s-3.542,3.512-4.542,4.339s-2.666,2.994-2.083,3.911s-4.75,2.25-5.75,1.333s-0.25-3.167-1.25-4.167s-1.583-1.667-2.083-1.417s-1.885,0.339-1.885,0.339c0.644-1.124,1.341-2.767,1.552-5.923c0.5-7.5-5.75-5.5-8-5.5c0,0,2.833-5.33,1.583-7.997s2.583-3.084,1.417-4.417s-3.333-3-4.667-5s-1.583-2.583-1-3.75s0.167-3.5,1.917-3.917s7.667-2,8.333-1.75s2.917,1.75,6-1.417s3.083-3.167,3.083-3.167l0,0c1.599,3.12,3.699,6.256,4.916,8.082c2.666,4,10.667,8.667,12.667,11.667s3.333,9,11,13.333S492.332,428.668,492.332,433.001L492.332,433.001z");
    mapList["EU06"].node.id = "EU06";

    mapList["AF01"] = map.path("M483.75,452c-4.335,0.5-8.25-4.25-9.25-3.25s-2.5,4.75-4.25,6s-7.25-3-7.25-3s-4-1.5-6.5-1.75s-4.25-4.25-6.413-7.75S442.75,441,442.75,441s-11,4.75-13.75,6.75s-0.25,4-3.25,8.25s-2.75,8.75-1.5,15.75s15.25,13,18.5,14.75s18.25,9.5,20.5,12s5.5,10.25,7.25,10.75s6.75,0.5,8-1.433s-0.25-6.567,6.5-7.067s17,5.5,21.75,6.585s22.751,0,22.751,0c0.166-3,1.667-9.834-0.5-14.167s2.833-10.333,4.333-11s3.834-7.833,0.167-12.5s-3.667-4.667-3.667-4.667S526.75,466.5,524,466.25s-12.999-2.582-12.999-2.582l-18.167-11.167C492.834,452.501,487.994,451.511,483.75,452z");
    mapList["AF01"].node.id = "AF01";

    mapList["AF02"] = map.path("M442.75,441c0,0-11,4.75-13.75,6.75s-0.25,4-3.25,8.25s-2.75,8.75-1.5,15.75s15.25,13,18.5,14.75s18.25,9.5,20.5,12s5.5,10.25,7.25,10.75c0,0-5.25,13.75-11.5,18.25s-7,19.25-7,19.25s-4.5-3.5-8.25,0s-15.25,6-18.5,5.75s-15.75,2.75-13.5,10.75s2.5,12.75-3.25,11s-9.75-7-15.5-7s-7.25,0-7.25,0s-0.75-11.75-2-13.25s-10.75-7.5-13.5-7.75s-6.5,0.25-6.75-2s-2-6.25-8.75-5S343,543,340,541s-4.75-4.25-13.25-4s-18.25-6.5-19-11.25s-3-2.5-3-2.5s-3.25-1.5-3.5-5.25s-4.5-3-4.5-3s1.25-13,1.25-15.5s-5.25-6.5-5.25-9.75s1.25-9.25,5.5-11.5s8-14.75,8.5-16.5s0-12,8.25-14.25s9.5-9.089,14.25-10.794s5.5-4.956,5.5-4.956s6.75,2,8.25,0s5.5-7.75,3.25-8S344,423.5,344,423.5s1.75-3,5.5-2.5s10-4.75,12.75-5.75s15.5-4.5,16.75-3.75s5.75,3.75,8,3.75s14,0.5,17-0.5s5-3.5,11-1s9.25,1,11.25,2.75s5.25,4.5,7.5,4.5s8.5-2,8,5.5s-3.75,6.456-2.5,10.206S442.75,441,442.75,441z");
    mapList["AF02"].node.id = "AF02";

    mapList["AF03"] = map.path("M529.501,507.335c0,0-18.064,1.085-22.814,0s-15-7.085-21.75-6.585s-5.25,5.135-6.5,7.067s-6.187,1.933-7.937,1.433l0,0c0,0-5.25,13.75-11.5,18.25s-7,19.25-7,19.25s-1.454,6.381,10.75,15c0,0,8.5,11.25,7.75,15.25s4.5,5.75,7.5,6s14,3,14.5,5.25s5.5,9.5-5.75,14.25s-20,11-21.5,13.5s-6.75,10.25-5,16s1.75,5.75,1.75,5.75s6.5,1.25,7,3.5s-2.5,9.25-1.25,10.25s4.5,1.5,7.5,1.75s11-0.25,12.25,0s8.5,1.5,10,4.25s3.25,8,3.25,8s2.25-2.5,5-2.5s3.75,0.167,3.75,0.167l0,0c6.666-4,9.334-10.667,10.667-12.667s8-4,8-7.333s2.667-8.667,9.667-12.519s8.333-9.481,7-11.481s-1.667-10.667-1.667-10.667s2-0.667,6.333-0.667s12.667-12.333,20-17.666s9.334-21.334,9.334-21.334l0,0c0,0-0.833-3.249-3.333-0.499s-8.416,1.999-19.25,1.333s-17.916-2.5-13.5-9s1.583-18.501-5.667-25.75s-1.583-12.415-2.25-16.082S529.335,510.335,529.501,507.335L529.501,507.335z");
    mapList["AF03"].node.id = "AF03";

    mapList["AF04"] = map.path("M462,637.75c0,0,0,0-1.75-5.75s3.5-13.5,5-16s10.25-8.75,21.5-13.5s6.25-12,5.75-14.25s-11.5-5-14.5-5.25s-8.25-2-7.5-6s-7.75-15.25-7.75-15.25c-12.203-8.619-10.75-15-10.75-15l0,0c0,0-4.5-3.5-8.25,0s-15.25,6-18.5,5.75s-15.75,2.75-13.5,10.75s2.5,12.75-3.25,11s-9.75-7-15.5-7s-7.25,0-7.25,0s-0.083,3.585-1.5,5.502s-5.75,8.166-4.083,12.5s4.166,7.25,4.833,11.417s-0.5,8.417-0.5,8.417s13.333-0.92,15.667,3.747s0,14.001,6,11.334s3.229-5.387,9.278-3.633c6.874,1.993,7.722,11.699,5.388,17.166s8.333,4.8,11.667,7.8s3,11.001,9.667,12.667s7.667-0.667,5.667-4.334s-4.101-8.4-1-12C451.233,632.726,462,637.75,462,637.75z");
    mapList["AF04"].node.id = "AF04";

    mapList["AF05"] = map.path("M457.5,706c-1.501-0.998-4.5,2.5-7.5,2.5s-7.126-3.953-9,1c-2.333,6.164-5,7.5-4.5,9.5S425,729.5,419,731.5s-10.999,7.164-10.999,7.164s-7.001-1.813-13.667-2.479s0.667-3.5-11.333-4.167s-14.167-5.004-15.167-11.171s6.5-18.667-0.333-20.333s-2.667-12.514-2.223-15.181s1.223-11.333,2.89-16.5s-1-9.833-2.667-14.119s6.5-17.048,8-18.714s9.167-6.167,9.167-7.5s1.667-9.334,3-10.5s-1.166-12.915-1.166-12.915s13.333-0.92,15.667,3.747s0,14.001,6,11.334s3.229-5.387,9.278-3.633c6.874,1.993,7.722,11.699,5.388,17.166s8.333,4.8,11.667,7.8s3,11.001,9.667,12.667s7.667-0.667,5.667-4.334s-4.101-8.4-1-12c4.4-5.107,15.166-0.083,15.166-0.083l0,0c0,0,6.5,1.25,7,3.5s-2.5,9.25-1.25,10.25s4.5,1.5,7.5,1.75s11-0.25,12.25,0s8.5,1.5,10,4.25s3.25,8,3.25,8s-5,0.5-5.5,2.5s0.75,7.25-7,9s-12.5,3.75-13.75,5.5s-8,4-11.25,4s-5,9-4.75,11.25S457.5,706,457.5,706z");
    mapList["AF05"].node.id = "AF05";

    mapList["OC01"] = map.path("M656.392,141.69c1.606,0.811,3.025,0.809,3.275-0.689c0.667-4,6.666-4,8.666-4s13.334,3,17.381,2.667s2.953,7,0,8.333s-3.047,2-2.047,4.333s-3.334,14.667-5.667,17.333s3.333,3.667,0.667,12s-16.667,0.667-18.334-2S648.382,177.48,645,174c-2.917-3.001-8.577-5.138-11.667-8.665c0,0,3.792-2.21,9.042-1.71s8.25,4.125,13,2.625s5.875,5,8.75-0.625s1.5-9.625-1.5-11.875s-7.875-4-7.625-6.75S656.392,141.69,656.392,141.69z");
    mapList["OC01"].node.id = "OC01";

    mapList["OC02"] = map.path("M584.408,775.574c5.326-6.609,10.202-13.637,10.121-17.215c-0.167-7.334-4.946-16.403-5.167-18.5c-0.333-3.167,0.917-6.484,0.917-6.484l0,0c0,0-0.125-6-2.625-6.875s-3.5-5.125-3.625-8.25c-0.076-1.906,2.125-2.5,2.75-6.25c0.268-1.603,2.24-3,2.24-3l0,0c0,0,5.75-1.5,7.75,0s6,3.5,6.25,2.5s3.25-6.25,6.5-4.75s6.25-2,8.75-3.75s14.188-1.652,17.25-1.25c2.479,0.326,4.582,3.917,4.582,3.917l0,0l0,0c-0.667,2,1.564,13.666,4.897,17.333s13,9.333,10.667,16c-0.833,2.38-0.126,4.078,1.169,5.582l0,0c0,0-21.335,3.293-24.085,4.668s-7.625,3-7.625,3s0.125,10.75-1.875,13.125s-3.375,10.25-3.375,10.25L584.408,775.574L584.408,775.574z");
    mapList["OC02"].node.id = "OC02";

    mapList["OC03"] = map.path("M584.408,775.574c-5.592,6.938-11.68,13.415-13.046,14.951c-2.667,3-18.833,12.334-24,13s-6.695,6.809-6.695,6.809l0,0c3,4.5,4.833,0.666,9.833,3.666s8.5,5,8,9.5s3.332,2.332,7.166,1.166s12.838,8.399,21.834,7.834c23.834-1.499,24.166-9.834,28-12s16.5,0,16-5.5s-1.5-11.5,3.5-12.5s9-1.5,11.5-0.5s2.5-2.5,3.5-5.5s8.5,0,11.5-6s11-9,15-11.5s14-32,6.5-36c0,0-7.546-9.099-11.668-7.333c-5.957,2.458-7.333,18-7.333,18c-4.749-4.542-4.83-6.375-7.164-9.085c0,0-21.335,3.293-24.085,4.668s-7.625,3-7.625,3s0.125,10.75-1.875,13.125s-3.375,10.25-3.375,10.25L584.408,775.574z");
    mapList["OC03"].node.id = "OC03";

    mapList["AN01"] = map.path("M588.99,709c0,0,0,0-2.5-5s-9.5-3.5-11-3.75s-7.5-4.75-9.5-6.75s-12.75-10-17-14s-31.75-2.5-38.25-2s-3.5-6.5-1.5-8.25s0.26-6.083,0.26-6.083l0,0c0,0-1-0.167-3.75-0.167s-5,2.5-5,2.5l0,0c0,0-5,0.5-5.5,2.5s0.75,7.25-7,9s-12.5,3.75-13.75,5.5s-8,4-11.25,4s-5,9-4.75,11.25s-1,8.25-1,8.25l0,0c1.501,0.998-2.5,9-2.5,15.5s4.5,11.5,8.5,15l0,0c0,0,1-2.5,2.5-4.75s4.75-0.5,8,0l0,0c0,0,1.167-19.75,3.167-21.083s6.999-2.667,9.833-3.5s9.666-2.833,10-4.5s3.001-7.001,10.834-4.667s15,6.168,19,11.834S528.166,723.5,531,723.5s11,2.168,14.5,3.834s12.668,6.834,14.834,8.5c0,0,10.291-4.709,13.416-4.459s9.75,3.375,10.75,3.125s5.75-1.125,5.75-1.125s-0.125-6-2.625-6.875s-3.5-5.125-3.625-8.25c-0.076-1.906,2.125-2.5,2.75-6.25C587.018,710.397,588.99,709,588.99,709z");
    mapList["AN01"].node.id = "AN01";

    mapList["AN02"] = map.path("M518.5,777c-2-9-10.499-10.666-15.5-9.5l0,0c0,0-4.25-6-2.25-11.25s-1-17.75-11-19s-12.5-5-15.75-5.5c0,0,1.167-19.75,3.167-21.083s6.999-2.667,9.833-3.5s9.666-2.833,10-4.5s3.001-7.001,10.834-4.667s15,6.168,19,11.834S528.166,723.5,531,723.5s11,2.168,14.5,3.834s12.668,6.834,14.834,8.5s6,7.833-1.667,8.333s-18.385,8.124-22.333,11.333c-2.666,2.167-5.334,12.334-9.667,12.5C519.931,768.258,518.5,777,518.5,777z");
    mapList["AN02"].node.id = "AN02";

    mapList["AN03"] = map.path("M590.25,733.375c0,0-4.75,0.875-5.75,1.125s-7.625-2.875-10.75-3.125s-13.416,4.459-13.416,4.459l0,0c2.166,1.666,6,7.833-1.667,8.333s-18.385,8.124-22.333,11.333c-2.666,2.167-5.334,12.334-9.667,12.5c-6.736,0.258-8.167,9-8.167,9l0,0c2,9,1.5,8,6.5,10.5s5.423,10.711,9,12.5c4.667,2.334,3.667,5.834,6.667,10.334c0,0,1.499-6.143,6.666-6.809s21.333-10,24-13s23.334-24.832,23.167-32.166s-4.946-16.403-5.167-18.5C589,736.692,590.25,733.375,590.25,733.375z");
    mapList["AN03"].node.id = "AN03";

    mapList["AN04"] = map.path("M463.5,736.5c4,3.5,6.834,9.414,1.834,11.165S457,754.5,455,751s1.335-18.752-24-16.25c-14.267,1.409-15.854,3.59-12.75,6.75c2.365,2.409,7.289,4.639,10.75,5c14.5,7.5,2.25,5.75,5.75,15.25S441.5,777,446.5,777s8.5,7.5,12,13.5s12.5,6.5,19.5,6s26,1,19.5-9s0.5-18.834,5.5-20c0,0-4.25-6-2.25-11.25s-1-17.75-11-19s-12.5-5-15.75-5.5s-6.5-2.25-8,0S463.5,736.5,463.5,736.5z");
    mapList["AN04"].node.id = "AN04";

    mapList["SA01"] = map.path("M216.917,569.5c20.5-11.875,5.833-27.5,9.833-36.125c1.481-3.194,0-17.5,4.75-20.541c3.286-2.104,12.375-10.143,15.625-4.959c3.167,5.051,21.75-1.125,34.708,9.626c1.868,1.55,10.083-1.167,10.083-1.167s3.167,0.412,4.167,1.912c0.31,0.465,1,0.75-0.083,4s2.125,6.15,2.125,6.15s-5.875,1.75-5.375,3.113s-5.917,6.129-9.417,6.879s-5.083-3.492-4.833-5.492c1.513-12.105-6.732-6.862-12.167-0.341c-2.5,3-1.083,6.166-1.333,9.666s-1.25,5.334-2.5,7.334s-5,0.091-6-1.409s-5.25,2-7,4.25s-0.25,6.75,2.5,14s5.75,8.25,1.5,13.75c0,0-3.25-3.146-5.75-2.146s-8.5,0.5-9.5-1s-1.5-5.75-7.75-4.75s-10.25,1.25-13-1.875L216.917,569.5z");
    mapList["SA01"].node.id = "SA01";

    mapList["SA02"] = map.path("M304.75,523.25c0,0,2.25-2.25,3,2.5s10.5,11.5,19,11.25s10.25,2,13.25,4s8-0.5,14.75-1.75s8.5,2.75,8.75,5s4,1.75,6.75,2s12.25,6.25,13.5,7.75s2,13.25,2,13.25l0,0c0,0-0.083,3.585-1.5,5.502s-5.75,8.166-4.083,12.5s4.166,7.25,4.833,11.417s-0.5,8.417-0.5,8.417l0,0c0,0,2.5,11.749,1.167,12.915s-3,9.167-3,10.5s-7.667,5.834-9.167,7.5s-9.667,14.428-8,18.714s4.333,8.952,2.667,14.119s-2.446,13.833-2.89,16.5l0,0c0,0-4.027,1.916-5.527,2.166s-3.75-3-5.25-5.25s-3.5-3.5-8-3.5s-1.25-6.25,0.5-9.75s-0.5-7.5-0.5-7.5s-4.75-9.25-11.75-11.75s-11.25-9.5-14.5-16.75s-12.75-10.75-13.75-10.25s-5.5-9-8.375-9S286.25,613.5,285,612.5s-5.25-7.27-7.5-7.385S271.25,615,269,614.5s-13-3.5-15-3.25s-6.5-5-7.75-12.25s3-13.25,7.25-18.75s1.25-6.5-1.5-13.75s-4.25-11.75-2.5-14s6-5.75,7-4.25s4.75,3.409,6,1.409s2.25-3.834,2.5-7.334s-1.167-6.666,1.333-9.666c5.434-6.521,13.68-11.765,12.167,0.341c-0.25,2,1.333,6.242,4.833,5.492s9.917-5.516,9.417-6.879s5.375-3.113,5.375-3.113s3.375,1.75,4.625,0S304.75,523.25,304.75,523.25z");
    mapList["SA02"].node.id = "SA02";

    mapList["SA03"] = map.path("M244.5,643c2-1,23-6.049,23,0.226l0,0c0,0,6.75,2.024,11.75,1.274s6.5,11.25,10.25,13.5s9-0.5,14.5-2.75s9.25-0.75,11,0.25s10.75,2.75,13.75,2.25s3.5,8.25,6.5,9s4.25,2.5,7.25,0.25s4-5.5,4-5.5l0,0c0,0-4.75-9.25-11.75-11.75s-11.25-9.5-14.5-16.75s-12.75-10.75-13.75-10.25s-5.5-9-8.375-9S286.25,613.5,285,612.5s-5.25-7.27-7.5-7.385S271.25,615,269,614.5s-13-3.5-15-3.25s-6.5-5-7.75-12.25s3-13.25,7.25-18.75l0,0c0,0-3.25-3.25-5.75-2.25s-8.5,0.5-9.5-1s-1.5-5.75-7.75-4.75s-10.25,1.25-13-1.875l-0.583-0.875l0,0c-1.5,1-2.723,5.133-2.417,7.125c0.233,1.516,0.418,6.765-0.875,8.125c-2.153,2.265-1.875,4.5-1.875,4.5s0,0,1.75,0.25s4,8.75,4,13.5s6,8.25,9,9.25s5,9,5,9l0,0c0,0,4.125-1.125,5.375,0.375c4.775,5.729,5.375,9.125,5.375,9.125s0,0,0.75,0.75S244.5,643,244.5,643L244.5,643z");
    mapList["SA03"].node.id = "SA03";

    mapList["SA04"] = map.path("M365.277,685.334c-0.443,2.667-4.61,13.515,2.223,15.181s-0.667,14.166,0.333,20.333S371,731.352,383,732.019s4.667,3.501,11.333,4.167s13.667,2.479,13.667,2.479l0,0c0,0-4.001,4.337-4.501,8.477C403,751.281,391.5,746,378,747s-27.5-3.5-32.5-8.5s-11-3.5-14-5s-13-9.5-17-11s-25-33-25-40.5s-22-32.499-22-38.774c0,0,6.75,2.024,11.75,1.274s6.5,11.25,10.25,13.5s9-0.5,14.5-2.75s9.25-0.75,11,0.25s10.75,2.75,13.75,2.25s3.5,8.25,6.5,9s4.25,2.5,7.25,0.25s4-5.5,4-5.5s2.25,4,0.5,7.5s-5,9.75-0.5,9.75s6.5,1.25,8,3.5s3.75,5.5,5.25,5.25S365.277,685.334,365.277,685.334z");
    mapList["SA04"].node.id = "SA04";

    mapList["NA01"] = map.path("M269,260.668c-2-2.333-1.667-11.666-0.333-14.333s0.167-6.834-3.167-10.501s-8-4-13.535,0.833c-4.609,4.024-14.465,0.5-17.798,0.833s-9.166,3.168-5.833,14.501s7,8,7.333,17s2.667,8,1.667,10c0,0,3.217-0.545,7.819-3.465c1.042-0.661,2.154-1.443,3.316-2.369c6.698-5.333,7.554-9.068,10.698-11.225C261.416,260.4,269,260.668,269,260.668z");
    mapList["NA01"].node.id = "NA01";

    mapList["NA02"] = map.path("M245.152,275.536c1.042-0.661,2.154-1.443,3.316-2.369c6.698-5.333,7.554-9.068,10.698-11.225c2.249-1.542,9.833-1.274,9.833-1.274l0,0c2,2.333,7.333,7.333,7.333,10.333s3,10.334,5,11.667s1.542-9.626,7.542-12.293s15.792,1.875,19.125,1.875s8.333-2.582,11.333-2.582s10.333-0.334,22.333-4.667s11.667,2,13.667,3.667c0,0,0.917,4.438-6.583,8.01s-15,13.072-15.5,15.822s-3.25,8.75,7.75,17s14.736,28.25,14.368,32.25s0.024,8.5,0.024,8.5s-8.698-1.668-11.059-0.917c-1.896,0.604-2.333,0.917-2.333,0.917s-13.167-9.75-12.5-10.583s2-0.5,2-2.833s1.5-6.333,2.333-6.5s-3-13-6.5-14.333s-8.5-0.333-8.5-6.5s-2.833-3.833-4.167-3.333s-5,3.333-5.167,5s0.393,6.5,2.696,6.5s4.137-2.333,5.137-0.667s3.833,6.167-1.333,11.833s-5.167,4.333-7.167,3.667s-7-3.667-10.333,0.667c0,0-15-13.167-20-16c0,0-14-8.833-15.833-14.333c0,0-3.834-7.5-6.167-9.5s-11.768-10.574-11.333-14.333S245.152,275.536,245.152,275.536L245.152,275.536z");
    mapList["NA02"].node.id = "NA02";

    mapList["NA03"] = map.path("M382.625,274c-5.333,0-7.625-3.666-11.625-5.332s-13.667,1.667-15.667,0l0,0c0,0,0.917,4.438-6.583,8.01s-15,13.072-15.5,15.822s-3.25,8.75,7.75,17s14.736,28.25,14.368,32.25s0.024,8.5,0.024,8.5s1.608,7.417,4.274,9.25s6.5,4.833,7.5,9s0.333,7.667,0.333,7.667s2.167,5.333,7.667,6.5s7.458,6.833,7.458,6.833s6.042,1.834,7.875-1.833s3-9.833,0-13.5s1-4.833,0.167-8.833s-4.333-6-2.5-9.167s5-4.167,5-4.167s4.5,1.834,7.667,1.667s3.667-1.667,4.833-3.333s1.333-7,2.167-7.5s0.833-0.5,0.833-0.5l-2-3.333c0,0-6.667-2.333-8.833,0c0,0-1-1.833-0.5-3.333s1.333-1.833,3-2.5S402,332.5,402,332.5s0.834-0.667,0.167-5.167s-0.667-9.334-2.5-10.667s-4-8.667-3.5-11.167s-3.5-5.333-4.667-7.5s-3-6.833-3.167-8s-0.167-1.167-0.167-1.167s-2.833-0.667-3.833-2.667S382.625,274,382.625,274z");
    mapList["NA03"].node.id = "NA03";

    mapList["NA04"] = map.path("M379,411.5c0,0-0.417-3.583-1.417-4.917s-2.75-6.5-2.75-8.333s3.25-7.75,4.667-8.333s3.125-0.417,3.125-0.417s-1.958-5.667-7.458-6.833s-7.667-6.5-7.667-6.5l0,0c0,0-1,0.958-1.563,1.021s-1.25-3.25-3.313-4.438s-2.063-2.125-2.688-3.625s-1.188-4.438-1.188-6.313s-2.107-3.125-3.357-4s-4.08,0-5.893,2.813s-4.438,1.5-5.5,0.75s-2.75-3.625-2.75-3.625c-1.708-2.542,0.75-8.5,0.75-8.5s-13.167-9.75-12.5-10.583l0,0c0,0-2.2,0.238-3.125,0.042c-2.271-0.481-4.875,1.292-5.708,3.125s1.5,5.833,0.333,9.167s0.833,12.833,0.833,15.5s-6.833,7.833-8.167,7.833s-1.833,4-2.167,4.833s-1.333,1-1.333,1s0.333,1.333,1,3.333s-3.5,8.333-5,10.167S303.167,402,303,404s-8,7.396-8,7.396s1.833,1.104,2.333,2.938s-7.5,4.167-7.5,4.167s0,0,0,2.501s-0.667,8.166,5,5.499s8.667-8.333,19.667-9.333c6.446-0.586,13.333,1,19.333-5s7.667-3.667,8.667,0s7,8.833,7,8.833l0,0c3.75,0.5,10-4.75,12.75-5.75S377.75,410.75,379,411.5L379,411.5z");
    mapList["NA04"].node.id = "NA04";

    mapList["NA05"] = map.path("M252,376.333c2-0.667,8,1.833,8.833,3.667s4,6.833,4,6.833l0,0l8.667,7.667c0,0,1.167-2.604,2.5-1.938s4.408,5.699,6.625,6.5c6.75,2.438,3.542,5.104,3.542,5.104c2.333-0.667,4,4.324,4,4.324l0,0c0.451-0.871,2.083-1.74,2.583-1.74s3.188-0.063,1.188,1.438s1.063,3.208,1.063,3.208s7.833-5.396,8-7.396s1.667-7.5,3.167-9.333s5.667-8.167,5-10.167s-1-3.333-1-3.333l0,0c0,0,0,0-2.292-0.667s1-8.625,0-11.25s-5-0.5-10.125-8.625s-3.5-3.125-9.25-10.375s4.125-11.875,6.75-13s3.25-4.084,3.25-4.084s-15-13.166-20-16l0,0c0,0,3.519,10.668-1.231,18.501s-12.167,14.166-13.333,17.083S251.25,370.333,252,376.333L252,376.333z");
    mapList["NA05"].node.id = "NA05";

    mapList["NA06"] = map.path("M209.333,346.335c0,0,5.333-0.335,6.5,0s2.333,3.332,2.833,4.665s4.167,3.167,5.833,4s10.833,9.833,13.667,12.333S250,377,252,376.333l0,0c-0.75-6,10.833-20.667,12-23.583s8.583-9.25,13.333-17.083s1.167-18.5,1.167-18.5s-14-8.833-15.833-14.333c0,0-3.834-7.5-6.167-9.5s-11.768-10.574-11.333-14.333s-0.014-3.466-0.014-3.466l0,0c-4.602,2.92-7.819,3.466-7.819,3.466l0,0c-1,2-3.334,22.667-7.667,27.667s-17,22.334-18.667,26.667S210,344.335,209.333,346.335L209.333,346.335z");
    mapList["NA06"].node.id = "NA06";

    mapList["NA07"] = map.path("M209.333,346.335C208.666,348.335,182,366,178,372s-1,8.5-6.5,11s-5,20-6,22s2.167,8.5-1.833,15c0,0,3,2.833,5.167,5s4.5,8.167,4.833,10.833s6.5,10.333,7.833,13.167c1.333,2.834,8,1.833,10.833,1.833s3.5,7.667,3.5,7.667s3-3.333,4.333-3.333s5.333-3.667,6-5.167s1.667-7.833,2.667-7.5s5.333-0.667,8.333-4.333s4.667-5.667,13,0.667s8-5.333,9-8.333s13.333-19,18.333-26s7.333-17.667,7.333-17.667s-3.167-5-4-6.833S254,375.667,252,376.333s-11-6.5-13.833-9s-12-11.5-13.667-12.333s-5.333-2.667-5.833-4s-1.666-4.33-2.833-4.665S209.333,346.335,209.333,346.335z");
    mapList["NA07"].node.id = "NA07";

    mapList["NA08"] = map.path("M296.75,515c0,0,1.25-13,1.25-15.5s-5.25-6.5-5.25-9.75s1.25-9.25,5.5-11.5s8-14.75,8.5-16.5s0-12,8.25-14.25s9.5-9.089,14.25-10.794s5.5-4.956,5.5-4.956s-1.75-3.25-0.25-5.25s2.489-4.478,6-5.556c2.164-0.665,3.5,2.556,3.5,2.556s1.75-3,5.5-2.5c0,0-6-5.167-7-8.833s-2.667-6-8.667,0s-12.888,4.414-19.333,5c-11,1-14,6.667-19.667,9.333s-5-2.998-5-5.499s0-2.501,0-2.501l0,0c0,0-1.667-3.667-1.333-5.667s1.667-4.343,1.667-4.343s-1.667-4.991-4-4.324s-5.667,0.667-6-1.667s-2.667-3.333-4-4s-2.667-4-2.667-4l-8.667-7.667c0,0-2.333,10.667-7.333,17.667s-17.333,23-18.333,26s-0.667,14.667-9,8.333s-10-4.334-13-0.667s-7.333,4.667-8.333,4.333s-2,6-2.667,7.5s-4.667,5.167-6,5.167s-4.333,3.333-4.333,3.333s1.333,14.667,3.333,16s2.667,0.333,4-0.667s4.333-3.333,4.333,0.667s4.667,9.666,4.667,11.333s1.334,13.668,2.667,14.334s7.333,4.334,7.333,7c0,0,5.333-2,5.333-4.667s0-4.667,0-4.667s2.333-6.334,5.333-6s14-2.666,15-0.333s2.333,3.334,6.667,1.667s10.667-1.668,14.333-0.334s11.333,0.334,11.667,3.334s5.333,15,5.333,15s3,1.334,5.333,2S296.75,515,296.75,515z");
    mapList["NA08"].node.id = "NA08";

    mapList["NA09"] = map.path("M222.167,507.167c0,0-2.417,1.583-3.917,5.083s1.25,6.75,1,8s-3.5,3.75-3.5,6.75s-1.5,12-1.5,12s-3-1.5-5-3.5s-4.5,1.25-2.75,6.75s9.75,7.75,9.75,7.75c4,2.5,0.5,16-1,17s-7.25,7-10.25,9.75s-3.5,7.5-0.5,11.5s7.25,1,7.25,1s0,0,1.75,0.25s4,8.75,4,13.5s6,8.25,9,9.25s5,9,5,9s0,0-1.25,0.5s-2.5,6,2.25,11.75s9.75-2.75,9.75-2.75s0,0,0.75,0.75s1.5,11.5,1.5,11.5c-2,1-16.5-1.5-19.5-4s-4.5-13-6-17s-3.5,0-5.5,2.5s-7-1-7.5-4.5s-5-2.5-6-7.5s-2.5-11-5.5-12S187,598,185,597s-6.5-4.5-6-7s-1-3-4-12s1.377-17.519,10.334-15.334C199,565.999,200,558,194,557s-14-11-17-15.5s-15.999-16.499-17.563-21.499s3.563-8.5,6.063-10s-1.5-25.5-1-33s-1-7.5-2.5-13.5s-2.453-7.321-2.5-9.762c-0.533-27.762,0.167-27.239,4.167-33.739l0,0c0,0,3,2.834,5.167,5s4.5,8.166,4.833,10.834c0.333,2.666,6.5,10.332,7.833,13.166s8,1.833,10.833,1.833s3.5,7.667,3.5,7.667l0,0c0,0,1.333,14.667,3.333,16s2.667,0.333,4-0.667s4.333-3.333,4.333,0.667s4.667,9.666,4.667,11.333s1.334,13.668,2.667,14.334S222.167,504.501,222.167,507.167L222.167,507.167z");
    mapList["NA09"].node.id = "NA09";

    angular.forEach(mapList, function (index) {
        index.attr(defaultCountry);
    });

    map.setViewBox(0, innerHeight / 2, window.innerWidth / 1.5, window.innerHeight / 1.5, true);
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