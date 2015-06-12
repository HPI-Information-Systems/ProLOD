'use strict';

define(['angular', './controllers', 'dimple'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers').controller("GraphCtrl", ['$scope', '$routeParams', '$timeout', 'routeBuilder', 'httpApi',
        function ($scope, $routeParams, $timeout, routeBuilder, httpApi) {
            $scope.updateBreadcrumb([{name: 'Graphs', url: routeBuilder.getGraphUrl()}]);

            $scope.data = {
                pattern: {},
                chart: {}
            };

            $scope.colorFunction = function (d) {
                return 'white';
            };


            $scope.loading = true;

            $scope.limit = 10;
            function increaseLimit() {
                $scope.limit+= 1;

                if($scope.limit < $scope.data.pattern.length){
                    $timeout(increaseLimit, 50);
                }
            }

            httpApi.getGraphStatistics($routeParams.dataset, $routeParams.group).then(function (data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;

                $scope.loading = false;
                increaseLimit();

                drawChart(stats.nodeDegreeDistribution);

                $scope.nodes = stats.nodes;
                $scope.edges = stats.edges;
                $scope.connectedComponents = stats.connectedComponents;
                $scope.connectedComponentsMinEdges = stats.connectedComponentsMinEdges;
                $scope.connectedComponentsMaxEdges = stats.connectedComponentsMaxEdges;
                $scope.connectedComponentsAvgEdges = stats.connectedComponentsAvgEdges;
                $scope.stronglyConnectedComponents = stats.stronglyConnectedComponents;
                $scope.stronglyConnectedComponentsMinEdges = stats.stronglyConnectedComponentsMinEdges;
                $scope.stronglyConnectedComponentsMaxEdges = stats.stronglyConnectedComponentsMaxEdges;
                $scope.stronglyConnectedComponentsAvgEdges = stats.stronglyConnectedComponentsAvgEdges;
                $scope.averageDiameter = stats.averageDiameter;
                $scope.giantComponentEdges = stats.giantComponentEdges;
                $scope.giantComponentNodes = stats.giantComponentNodes;
                $scope.giantComponentDiameter = stats.giantComponentDiameter;
            });
        }
    ]);

    function drawChart(distribution) {
        var xaxis = "node degree", yaxis = "number of nodes";

        var data = Object.keys(distribution).map(function (key) {
            var obj = {};
            obj[xaxis] = key;
            obj[yaxis] = distribution[key];
            return obj;
        });

        var width = 500, height = 300;
        var svg = dimple.newSvg("#distribution-chart", "100%", "100%");
        //svg.attr("preserveAspectRatio", "xMinYMin meet");
        //svg.attr("viewBox", "0 0 " + width + " " + height);
        var myChart = new dimple.chart(svg, data);
        var border = 70;
        myChart.setMargins(border, 5, 5, border);
        var x = myChart.addCategoryAxis("x", xaxis);
        x.addOrderRule(xaxis);
        var y = myChart.addMeasureAxis("y", yaxis);
        y.tickFormat = "d";
        myChart.addSeries(null, dimple.plot.bar);
        myChart.draw();

        window.onresize = function () {
            myChart.draw(0, true);
        };
    }

});
