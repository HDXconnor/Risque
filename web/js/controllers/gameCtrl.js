

angular.module('gameApp')

    .controller("GameController", ['$rootScope', '$http', function ($rootScope, $http) {

//        this.endPhase = function () {
//            // Deploy -> Attack -> Move
//            if ($rootScope.Game.GameState.Phase === "Setup" || $rootScope.Game.GameState.Phase === "Deploy")
//                $rootScope.Game.GameState.Phase = "Attack";
//            else if ($rootScope.Game.GameState.Phase === "Attack")
//                $rootScope.Game.GameState.Phase = "Move";
//            else if ($rootScope.Game.GameState.Phase === "Move") {
////                moveTroops();
//                $rootScope.Game.GameState.Phase = "Deploy";
//                // increment currentplayer, mod number of players
//                $rootScope.Game.GameState.CurrentPlayer= ($rootScope.Game.GameState.CurrentPlayer + 1) % $rootScope.Game.Players.length;
//                $rootScope.Game.GameState.CurrentPlayer = $rootScope.Game.Players[$rootScope.Game.GameState.CurrentPlayer].PlayerOrder;
//            }
//        };

        this.endTurn = function(){
            var endTurnData = JSON.stringify({Command: "EndTurn", Data: {CurrentPlayer: $rootScope.Game.GameState.CurrentPlayer}});
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