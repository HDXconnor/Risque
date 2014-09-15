/**
 * Created by Anthony on 15/09/2014.
 */

//Make an SVG Container
app.controller("MapController", ["$rootScope", '$http', function($rootScope, $http) {
    var mapData = JSON.parse("../json/mapPaths.json");
    var svgMap = d3.select("body").append("svgMap")
                                  .attr("width", 900)
                                  .attr("height", 900);
    angular.forEach(mapData.Map, function(index) {
        svgMap.append("path")
              .attr("id", index.id)
              .attr("class", "country-shape")
              .attr("d", lineFunction(index.d))
              .attr("stroke", index.stroke)
              .attr("fill", index.fill)
    };
}]);