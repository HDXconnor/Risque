

angular.module('gameApp')

    .controller("GameController", ['$rootScope', '$http', function ($rootScope, $http) {

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
                    }).success(function (output) {
                        $rootScope.obj = output;
                        console.log($rootScope.obj.Game);
                    });
                    $rootScope.$apply();
                }
    }]);