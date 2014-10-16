

angular.module('gameApp')

    .controller("GameController", ['$rootScope', '$http', function ($rootScope, $http) {

        this.endTurn = function(){
            var endTurnData = JSON.stringify({Command: "EndTurn", Data: {CurrentPlayer: $rootScope.Game.GameState.CurrentPlayer}});
            postData(endTurnData);
        };


        this.magic = function(){
            console.log($rootScope.chatMessages);
        };
        this.sendChat = function() {
            var chatString = document.getElementById("game-chatbox").value.trim();
            if (chatString) {
                var chatData = JSON.stringify({Data: {Username: $rootScope.userName, Message: chatString}});
                $rootScope.chatMessages.push({Username: $rootScope.userName,Message: chatString});
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
                        
                    });
                }
    }]);