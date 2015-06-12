'use strict';

define(['angular', './controllers', 'dimple'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("GraphCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', function ($scope, $routeParams, routeBuilder, httpApi) {
            $scope.updateBreadcrumb([{name: 'graphs', url: routeBuilder.getGraphUrl()}]);

            $scope.data = {
                pattern: {},
                chart: {}
            };

            $scope.colorFunction = function(d) { return 'white'; };

            $scope.loading = true;

            httpApi.getGraphStatistics($routeParams.dataset, $routeParams.group).then(function (data) {
                $scope.loading = false;
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;

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

        }]);

        function drawChart(distribution) {
            var xaxis = "node degree", yaxis = "number of nodes";

            var data = Object.keys(distribution).map(function (key) {
                var obj = {};
                obj[xaxis] = key;
                obj[yaxis] = distribution[key];
                return obj;
            });

            var width = 500, height = 300;
            var svg = dimple.newSvg("#distribution-chart", width, height);
            var myChart = new dimple.chart(svg, data);
            var border = 50;
            myChart.setBounds(border, 0.5 * border, width -1.5*border, height-1.5*border);
            var x = myChart.addCategoryAxis("x", xaxis);
            x.addOrderRule(xaxis);
            myChart.addMeasureAxis("y", yaxis);
            myChart.addSeries(null, dimple.plot.bar);
            myChart.draw();
        }

});
