'use strict';

angular.module('gameApp')

    .controller("GameController", ['$rootScope', '$http', function ($rootScope, $http) {

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
            if ($rootScope.countryCount !== 0) {
                return true;
            } else {
                return false;
            }
        };
        this.endTurn = function(){
            var endTurnData = JSON.stringify({Command: "EndTurn", Data: {CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
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