'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("GraphCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', function ($scope, $routeParams, routeBuilder, httpApi) {
            $scope.updateBreadcrumb([{name: 'graphs', url: routeBuilder.getGraphUrl()}]);

            $scope.data = {
                pattern: {},
                chart: {}
            };

            $scope.colorFunction = function(d) { return 'white'; };

            httpApi.getGraphStatistics($routeParams.dataset, $routeParams.group).then(function (data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;
                var keys = Object.keys(stats.nodeDegreeDistribution);
                var values = keys.map(function (key) {
                    return stats.nodeDegreeDistribution[key];
                });
                $scope.data.chart.labels = keys;
                $scope.data.chart.data = [values];

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
});
