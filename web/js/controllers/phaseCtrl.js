

angular.module('gameApp')

        .controller("PhaseController", ["$rootScope", '$http', function ($rootScope, $http) {

                this.increment = function(){
                    $rootScope.amountTroopsToMove++;
                }
                this.decrement= function(){
                    if($rootScope.amountTroopsToMove !=0) {
                        $rootScope.amountTroopsToMove--;
                    }
                }
                this.atkBoxes = function () {
                    if ($rootScope.phase === "Attack") {
                        return $rootScope.isHidden;
                    } else {
                        return true;
                    }
                };
                this.setupBoxes = function () {
                    if ($rootScope.phase === "Setup") {
                        return $rootScope.isHidden;
                    } else {
                        return true;
                    }
                }
                this.deployBoxes = function () {
                    if ($rootScope.phase === "Deploy") {
                        return $rootScope.isHidden;
                    } else {
                        return true;
                    }
                };

                this.endPhaseVis = function () {
                    if ($rootScope.phase === "Attack" || $rootScope.phase === "Move") {
                        return $rootScope.isHidden;
                    } else {
                        return true;
                    }
                };

                this.endPhase = function () {
                    var endPhaseData = JSON.stringify({Command: "EndPhase", Data: {CurrentPlayer: $rootScope.CurrentPlayer}});
                    postData(endPhaseData);
                }

                this.reinfBoxes = function () {
                    if ($rootScope.phase === "Move") {
                        return $rootScope.isHidden;
                    } else {
                        return true;
                    }
                };
                this.attack = function () {
                    if ($rootScope.attackCountryID !== null && $rootScope.defendCountryID !== null) {
                        var send = JSON.stringify({Command: "Attack", Data: {AttackingCountry: $rootScope.attackCountryID, DefendingCountry: $rootScope.defendCountryID, CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
                        postData(send);
                        $rootScope.attackCountryName = null;
                        $rootScope.attackTroops = null;
                        $rootScope.defendCountryName = null;
                        $rootScope.defendTroops = null;
                        $rootScope.defendCountryID = null;
                        $rootScope.attackCountryID = null;
                    }
                };
                this.move = function () {
                    if ($rootScope.amountTroopsToMove !== null && $rootScope.moveFrom !== null && $rootScope.moveTo !== null) {
                        var send = JSON.stringify({Command: "Move", Data: {SourceCountry: $rootScope.moveFrom, CountryClicked: $rootScope.moveTo, CurrentPlayer: $rootScope.currentPlayer, Troops: $rootScope.amountTroopsToMove}});
                        postData(send);
                        $rootScope.moveFrom = null;
                        $rootScope.moveTo = null;
                        $rootScope.moveFromCountryName = null;
                        $rootScope.moveFromTroops = null;
                        $rootScope.moveToCountryName = null;
                        $rootScope.moveToTroops = null;
                        $rootScope.amountTroopsToMove=0;

                    }
                };
                this.sendChat = function() {
                    var chatString = document.getElementById("game-chatbox").value.trim();
                    if (chatString) {
                        var chatData = JSON.stringify({Data: {Username: $rootScope.userName, Message: chatString}});
                        $http({method: 'POST', url: 'ChatServlet', headers: {'Content-Type': 'application/json'}, data: chatData}).success(function () {
                            document.getElementById("game-chatbox").value = "";
                        });
                    }
                };

                function postData(data) {
                    $http({
                        method: 'POST',
                        url: 'GameServlet',
                        headers: {'Content-Type': 'application/json'},
                        data: data
                    }).success(function (output) {
                        $rootScope.obj = output;
                    });
                }
                ;

            }]);