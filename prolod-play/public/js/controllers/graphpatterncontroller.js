'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("GraphPatternCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', function ($scope, $routeParams, routeBuilder, httpApi) {
            var pattern = $routeParams.pattern;
            $scope.updateBreadcrumb([
                {name:'graphs', url: routeBuilder.getGraphUrl()},
                {name:'pattern ' + pattern, url: routeBuilder.getGraphPatternUrl(pattern)}
            ]);

            $scope.data = {
                pattern: {}
            };

            var color = d3.scale.category20();

            $scope.colorFunction = function (d) {
                return color(d.group);
            };

            httpApi.getGraphPatternStatistics($routeParams.dataset, [$routeParams.group], pattern).then(function(data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;
            });
        }]);

});


