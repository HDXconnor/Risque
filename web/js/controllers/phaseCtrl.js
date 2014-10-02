'use strict';

angular.module('gameApp')

    .controller("PhaseController", ["$rootScope", '$http', function ($rootScope, $http) {
        this.atkBoxes = function () {
            if ($rootScope.phase === "Attack") {
                return $rootScope.isHidden;
            } else {
                return true;
            }
        };

        this.deployBoxes = function () {
            if ($rootScope.phase === "Deploy") {
                return $rootScope.isHidden;
            } else {
                return true;
            }
        };

        this.reinfBoxes = function () {
            if ($rootScope.phase === "Move") {
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
        }
    }]);