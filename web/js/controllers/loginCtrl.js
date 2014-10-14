

angular.module('gameApp')

        .controller('LoginController', ['$rootScope', '$http', function ($rootScope, $http) {
                this.setUser = function () {
                    var loginName = document.getElementById("login-textbox").value;
                    var joinData = JSON.stringify({Command: "Login", Data: {Username: loginName}});
                    postData(joinData);
//                    writeCookie("Username", loginName);
                    console.log("Logged in as: " + loginName);
                    //$cookieStore.put("Username", $rootScope.username);
                };

                this.facebook = function () {
                    $rootScope.goingToAuth = true;
                    console.log($rootScope.goingToAuth);
                    var id = 'facebook' + makeID();
                    var url = 'OAuthServlet?service=facebook&id=' + id;
                    console.log(url);
                    $http({method: 'POST', url: url, headers: {'Content-Type': 'text/plain'}}).success(
                            function (responseData) {
                                window.open(responseData, "_self");
                            });
                };

                this.google = function () {
                    $rootScope.goingToAuth = true;
                    var id = 'google' + makeID();
                    var url = 'OAuthServlet?service=google&id=' + id;
                    console.log(url);
                    $http({method: 'POST', url: url, headers: {'Content-Type': 'text/plain'}}).success(
                            function (responseData) {
                                window.open(responseData, "_self");
                            });
                };

                this.loginVis = function () {
//                    return(document.cookie.indexOf("Username") >= 0);
                    if ($rootScope.userName == null) {
                        return true;
                    } else {
                        return false;
                    }
                };

                this.delCookie = function () {
                    var cookie = readCookie();
                    console.log("deleting cookie: " + cookie);
                    for (var index in cookie) {
                        var name = cookie[index];
                    }
                    name = name.replace('Username=', '');
                    var quitData = JSON.stringify({Command: "Quit", Data: {CurrentPlayer: $rootScope.thisPlayer}});
                    document.cookie = "Username=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                    postData(quitData);
                    $rootScope.$apply();
                };

                function readCookie() {
                    var x = document.cookie;
                    return x.split("; ");
                }

                function postData(data) {
                    $http({
                        method: 'POST',
                        url: 'GameServlet',
                        headers: {'Content-Type': 'application/json'},
                        data: data
                    }).success(function (output) {
                        $rootScope.nameObj = output;
                        console.log($rootScope.obj.Game);
                        
                    });
                }

                function writeCookie(key, value) {
                    document.cookie = key + "=" + value + ";";
                }

                function makeID()
                {
                    var text = "";
                    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

                    for (var i = 0; i < 5; i++)
                        text += possible.charAt(Math.floor(Math.random() * possible.length));

                    return text;
                }

            }]);