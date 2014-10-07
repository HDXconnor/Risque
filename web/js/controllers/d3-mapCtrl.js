

angular.module('gameApp')
    .controller("MapController", ["$rootScope", '$http', function ($rootScope, $http) {
        $rootScope.mapList = {};
        //Make an SVG Container
        $rootScope.svgMap = d3.select("#svgMap").append("svg")
            .attr("width", "50%")
            .attr("height", "100%");


        $http.get('json/mapPaths.json').success(function (mapData) {

            //Add Countries to SVG Container
            angular.forEach(mapData.Map, function (index) {
                $rootScope.mapList[index.id] = $rootScope.svgMap.append("path")
                    .attr("id", index.id)
                    .attr("d", index.d)
                    .attr("stroke", index.stroke)
                    .attr("fill", index.fill)
                    .attr("class", "countryShape")
                    .attr("transform", "scale(0.7)");
            });

            angular.forEach($rootScope.mapList, function (index) {

                index[0][0].addEventListener("mouseover", function () {
                    $rootScope.thisCountryID = index.id;
                    console.log($rootScope.thisCountryID);

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
                            if ($rootScope.CurrentPlayer !== index.Owner) {
                                $rootScope.defendCountryName = index.CountryName;
                                $rootScope.defendOwner = index.Owner;
                                $rootScope.defendTroops = index.Troops;
                            } else {
                                $rootScope.attackCountryName = index.CountryName;
                                $rootScope.attackOwner = index.Owner;
                                $rootScope.attackTroops = index.Troops;
                            }
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

                }, true);

                index[0][0].addEventListener("mouseout", function () {
                }, true);

                index[0][0].addEventListener("click", function () {
                    $rootScope.thisCountryID = index.node.id;

                    if ($rootScope.CurrentPlayer === $rootScope.thisUserNumber) {
//                                index.animate(defaultCountry, animationSpeed);
                        if ($rootScope.phase === "Setup") {
                            var send = JSON.stringify({Command: "Setup", Data: {CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.currentPlayer}});
                            postData(send);
                        }
                        if ($rootScope.phase === "Deploy") {
                            var send = JSON.stringify({Command: "Deploy", Data: {CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.currentPlayer}});
                            postData(send);
                        }
                        if ($rootScope.phase === "Attack") {
                            angular.forEach($rootScope.board, function (index) {
                                if ($rootScope.thisCountryID === index.CountryID) {
                                    if ($rootScope.currentPlayer === index.Owner) {
                                        $rootScope.attackCountryID = $rootScope.thisCountryID;
                                        console.log("attack");
                                    }
                                    if ($rootScope.currentPlayer !== index.Owner) {
                                        $rootScope.defendCountryID = $rootScope.thisCountryID;
                                        console.log("defend");
                                    }
                                }
                            });
                        }
                        if ($rootScope.phase === "Move") {
                            var send = JSON.stringify({Command: "Move", Data: {SourceCountry: $rootScope.prevCountryID, CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.currentPlayer}});
                            postData(send);
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
            }).error();
        }

    }]);