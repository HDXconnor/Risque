'use strict';

angular.module('gameApp')

    .controller("MapController", ["$rootScope", '$http', function ($rootScope, $http) {

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
                        var send = JSON.stringify({Command: "Deploy", Data: {CountryClicked: $rootScope.thisCountryID, CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
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