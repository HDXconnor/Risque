

angular.module('gameApp')

    .controller("GameController", ['$rootScope', '$http', function ($rootScope, $http) {

        this.endTurn = function(){
            var endTurnData = JSON.stringify({Command: "EndTurn", Data: {CurrentPlayer: $rootScope.Game.GameState.CurrentPlayer}});
            postData(endTurnData);
        };

        this.quitGame = function () {
            var quitGameData = JSON.stringify({Command: "Quit", Data: {}});
            postData(quitGameData);
            $rootScope.obj = null;
            $rootScope.gameStarted = false;
        };
        this.magic = function(){
            console.log($rootScope.gameMessages[0].Message);
            console.log($rootScope.chatObj);
        };

        function postData(data) {
                    $http({
                        method: 'POST',
                        url: 'GameServlet',
                        headers: {'Content-Type': 'application/json'},
                        data: data
                    }).success(function (output) {
                        
                    });
                }
    }]);