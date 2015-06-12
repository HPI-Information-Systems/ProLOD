'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers').controller("GraphPatternCtrl", ['$scope', '$routeParams', '$timeout', 'routeBuilder', 'httpApi', 'colorHash',
        function ($scope, $routeParams, $timeout, routeBuilder, httpApi, colorHash) {
            var pattern = $routeParams.pattern;
            $scope.updateBreadcrumb([
                {name: 'Graphs', url: routeBuilder.getGraphUrl()},
                {name: 'Pattern ' + pattern, url: routeBuilder.getGraphPatternUrl(pattern)}
            ]);

            $scope.data = {
                pattern: {}
            };


            $scope.colorFunction = function (d) {
                return colorHash(d.group);
            };

            $scope.loading = true;

            $scope.limit = 10;
            function increaseLimit() {
                $scope.limit += 3;

                if ($scope.limit < $scope.data.pattern.length) {
                    $timeout(increaseLimit, 50);
                }
            }

            httpApi.getGraphPatternStatistics($routeParams.dataset, [$routeParams.group], pattern).then(function (data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;

                $scope.loading = false;
                increaseLimit();

            });
        }
    ]);

});


