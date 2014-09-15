//
//
//app.controller("MapController", ["$rootScope", '$http', function($rootScope, $http) {
//    var mapData = JSON.parse("../json/mapPaths.json");
//    //Make an SVG Container
//    var svgMap = d3.select("body").append("svgMap")
//                                  .attr("width", 900)
//                                  .attr("height", 900);
//    //Add Countries to SVG Container
//    angular.forEach(mapData.Map, function(index) {
//        svgMap.append("path")
//              .attr("id", index.id)
//              .attr("class", "country-shape")
//              .attr("d", lineFunction(index.d))
//              .attr("stroke", index.stroke)
//              .attr("fill", index.fill)
//    };
//}]);