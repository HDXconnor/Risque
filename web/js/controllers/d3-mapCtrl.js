

angular.module('gameApp')
    .controller("MapController", ["$rootScope", '$http', function ($rootScope, $http) {
        $rootScope.mapList = {};
        $rootScope.joinList = {};
        $rootScope.scale = String(window.innerHeight/1130);
        var canvas = String((window.innerHeight/1130)*900);
        $rootScope.moveFrom = null;
        $rootScope.moveTo = null;

        //Make an SVG Container
        $rootScope.svgMap = d3.select("#svgMap").append("svg")
            .attr("id", "map-canvas")
            .attr("width", canvas + "px")
            .attr("height", canvas + "px");


        $http.get('json/mapPaths.json').success(function (mapData) {

            //Add Countries to SVG Container
            angular.forEach(mapData.Map, function (index) {
                if (index.id.indexOf("Join") !== -1) {
                    $rootScope.joinList[index.id] = $rootScope.svgMap.append("path")
                        .attr("id", index.id)
                        .attr("d", index.d)
                        .attr("stroke", "black")
                        .attr("fill", "none")
                        .attr("stroke-width", 1)
                        .attr("stroke-linejoin", "round")
                        .attr("transform", "scale(" + $rootScope.scale + ")");
                } else {
                    $rootScope.mapList[index.id] = $rootScope.svgMap.append("path")
                        .attr("id", index.id)
                        .attr("d", index.d)
                        .attr("stroke", "#999")
                        .attr("stroke-width", 1)
                        .attr("stroke-linejoin", "round")
                        .attr("cursor", "pointer")
                        .attr("fill", index.fill)
                        .attr("class", "countryShape")
                        .attr("transform", "scale(" + $rootScope.scale + ")");
                }
            });


            function color($rootScope) {
                angular.forEach($rootScope.board, function (country) {
                    if (country.Owner === -1) {
                        $rootScope.mapList[country.CountryID].attr("fill", "black");
                    }
                    else if (country.Owner === 0) {
                        $rootScope.mapList[country.CountryID].attr("fill", "#FF3B30");
                    }
                    else if (country.Owner === 1) {
                        $rootScope.mapList[country.CountryID].attr("fill", "#00ff1b");
                    }
                    else if (country.Owner === 2) {
                        $rootScope.mapList[country.CountryID].attr("fill", "#66FFE7");
                    }
                    else if (country.Owner === 3) {
                        $rootScope.mapList[country.CountryID].attr("fill", "#4A4A4A");
                    }
                    else if (country.Owner === 4) {
                        $rootScope.mapList[country.CountryID].attr("fill", "#6f3ed6");
                    }
                    else if (country.Owner === 5) {
                        $rootScope.mapList[country.CountryID].attr("fill", "#007AFF");
                    }
                });
            }

            function troopCounters($rootScope) {
                angular.forEach($rootScope.board, function (country) {
                    var countryID = country.CountryID;
                    var element = document.getElementById(countryID);
                    var bbox = element.getBBox();
                    var x = bbox.x;
                    var y = bbox.y;
                    var h = bbox.height;
                    var w = bbox.width;
                    var centerX = x + (w/2);
                    var centerY = y + (h/2);
                    console.log(bbox);
                    $rootScope.svgMap.append("text")
                        .attr("id",countryID + "Number")
                        .attr("fill", "black")
                        .attr("x", centerX)
                        .attr("y", centerY)
                        .attr("font-size", "20pt")
                        .text(country.Troops)
                        .attr("transform", "scale(" + $rootScope.scale + ")");


                });
            }

                angular.forEach($rootScope.mapList, function (index) {

                index[0][0].addEventListener("mouseover", function () {
                    $rootScope.thisCountryID = index[0][0].id;

                    angular.forEach($rootScope.board, function (index) {
                        if ($rootScope.thisCountryID === index.CountryID) {
                            $rootScope.countryName = index.CountryName;
                            $rootScope.owner = index.Owner;
                            $rootScope.troops = index.Troops;
                        }
                        angular.forEach($rootScope.players, function (player) {
                            if ($rootScope.owner === player.PlayerOrder) {
                                $rootScope.playerName = player.DisplayName;
                            }
                        });
                    });

                    angular.forEach($rootScope.board, function (index) {
                        if ($rootScope.thisCountryID === index.CountryID) {
//                            if ($rootScope.CurrentPlayer !== index.Owner) {
//                                $rootScope.defendCountryName = index.CountryName;
//                                $rootScope.defendOwner = index.Owner;
//                                $rootScope.defendTroops = index.Troops;
//                            } else {
//                                $rootScope.attackCountryName = index.CountryName;
//                                $rootScope.attackOwner = index.Owner;
//                                $rootScope.attackTroops = index.Troops;
//                            }
                        }

                        angular.forEach($rootScope.players, function (player) {
                            if ($rootScope.attackOwner === player.PlayerOrder) {
                                $rootScope.attackOwner = player.DisplayName;
                            }

                            if ($rootScope.defendOwner === player.PlayerOrder) {
                                $rootScope.defendOwner = player.DisplayName;
                            }

                        });
                    });
                    d3.select(index[0][0]).attr("fill", "white");
//                                          .duration(2000);
                }, true);

                index[0][0].addEventListener("mouseout", function () {
                    color($rootScope);
                }, true);

                index[0][0].addEventListener("click", function () {
                    $rootScope.thisCountryID = index[0][0].id;

                    if ($rootScope.CurrentPlayer === $rootScope.thisUserNumber) {
                        if ($rootScope.phase === "Setup") {
                            var send = JSON.stringify({Command: "Setup", Data: {CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.CurrentPlayer}});
                            postData(send);
                        }
                        if ($rootScope.phase === "Deploy") {
                            var send = JSON.stringify({Command: "Deploy", Data: {CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.CurrentPlayer}});
                            postData(send);
                            if($rootScope.troopsToDeploy!=1){
                                $rootScope.troopsToDeploy--;
                            }if($rootScope.troopsToDeploy==0){
                                $rootScope.phase="Attack";
                            }
                        }
                        if ($rootScope.phase === "Attack") {
                            angular.forEach($rootScope.board, function (index) {
                                if ($rootScope.thisCountryID === index.CountryID) {
                                    if ($rootScope.CurrentPlayer !== index.Owner) {
                                $rootScope.defendCountryName = index.CountryName;
                                $rootScope.defendOwner = index.Owner;
                                $rootScope.defendTroops = index.Troops;
                                $rootScope.defendCountryID = $rootScope.thisCountryID;
                            } else {
                                $rootScope.attackCountryName = index.CountryName;
                                $rootScope.attackOwner = index.Owner;
                                $rootScope.attackTroops = index.Troops;
                                $rootScope.attackCountryID = $rootScope.thisCountryID;
                            } 
//                                    if ($rootScope.CurrentPlayer === index.Owner) {
//                                        $rootScope.attackCountryID = $rootScope.thisCountryID;
//                                        console.log("attack");
//                                    }
//                                    if ($rootScope.CurrentPlayer !== index.Owner) {
//                                        $rootScope.defendCountryID = $rootScope.thisCountryID;
//                                        console.log("defend");
//                                    }
                                }
                            });
                        }
                        if ($rootScope.phase === "Move") {
                            angular.forEach($rootScope.board, function (index) {
                                if ($rootScope.thisCountryID === index.CountryID){
                                    if ($rootScope.CurrentPlayer === index.Owner) {
                            if($rootScope.moveFrom===null){
                                $rootScope.moveFrom=$rootScope.thisCountryID;
                                $rootScope.moveFromCountryName= index.CountryName;
                                $rootScope.moveFromTroops=index.Troops;
                                $rootScope.moveFromOwner = index.Owner;
                            }else if($rootScope.moveTo===null){
                                $rootScope.moveTo=$rootScope.thisCountryID;
                                $rootScope.moveToCountryName=index.CountryName;
                                $rootScope.moveToTroops=index.Troops;
                                $rootScope.moveToOwner = index.Owner;
                            }else{
                                $rootScope.moveFrom=$rootScope.thisCountryID;
                                $rootScope.moveFromCountryName= index.CountryName;
                                $rootScope.moveFromTroops=index.Troops;
                                $rootScope.moveFromOwner = index.Owner;
                                $rootScope.moveTo=null;
                                $rootScope.moveToCountryName=null;
                                $rootScope.moveToTroops=null;
                                $rootScope.moveToOwner = null;
                            }
                        }
                        }
                            });
                        }
                        $rootScope.prevCountryID = index.node.id;

                    }
                    else {
                        console.log("Current player: "+$rootScope.CurrentPlayer);
                        console.log("this user: " + $rootScope.thisUserNumber);
                    }
                }, true);
            });
        });

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
    }]);