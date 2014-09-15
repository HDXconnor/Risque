/**
 * Created by Anthony on 15/09/2014.
 */

//Make an SVG Container
app.controller("MapController", ["$rootScope", '$http', function($rootScope, $http) {
    var mapData = JSON.parse("mapPaths.json");
    var svgMap = d3.select("body").append("svgMap")
                                  .attr("width", 900)
                                  .attr("height", 900);


}]);
