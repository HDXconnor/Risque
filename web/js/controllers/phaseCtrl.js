

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

        this.endPhaseVis = function () {
            if ($rootScope.countryCount != 0) {
                return true;
            } else {
                return false;
            }
        };

        this.endPhase = function(){
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
        this.attack = function(){
            var send = JSON.stringify({Command: "Attack", Data: {AttackingCountry: $rootScope.attackCountryID, DefendingCountry: $rootScope.defendCountryID, CurrentPlayer: $rootScope.obj.Game.GameState.CurrentPlayer}});
            postData(send);
            
        };
        this.move = function(){
            $rootScope.amountTroopsSent = document.getElementById("troop-amount").value;;
            var send = JSON.stringify({Command: "Move", Data: {SourceCountry: $rootScope.moveFrom, CountryClicked: $rootScope.moveTo, CurrentPlayer: $rootScope.currentPlayer,Troops: $rootScope.amountTroopsSent}});
            postData(send);
            $rootScope.moveFrom=null;
            $rootScope.moveTo=null;
        };
        this.postData = function(data){
            $http({
                        method: 'POST',
                        url: 'GameServlet',
                        headers: {'Content-Type': 'application/json'},
                        data: data
                    }).success(function (output) {
                        $rootScope.obj = output;
                    });
        }
//        function postData(data) {
//                    $http({
//                        method: 'POST',
//                        url: 'GameServlet',
//                        headers: {'Content-Type': 'application/json'},
//                        data: data
//                    }).success(function (output) {
//                        $rootScope.obj = output;
//                    });
//                    $rootScope.$apply();
//                };
        
    }]);