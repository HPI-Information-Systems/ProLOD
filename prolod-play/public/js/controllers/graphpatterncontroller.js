'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("GraphPatternCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', 'colorHash', function (
                                         $scope, $routeParams, routeBuilder, httpApi, colorHash) {
            var pattern = $routeParams.pattern;
            $scope.updateBreadcrumb([
                {name:'Graphs', url: routeBuilder.getGraphUrl()},
                {name:'Pattern ' + pattern, url: routeBuilder.getGraphPatternUrl(pattern)}
            ]);

            $scope.data = {
                pattern: {}
            };


            $scope.colorFunction = function(d) {
                return colorHash(d.group);
            };

            $scope.loading = true;

            httpApi.getGraphPatternStatistics($routeParams.dataset, [$routeParams.group], pattern).then(function(data) {
                $scope.loading = false;
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;
            });
        }]);

});


