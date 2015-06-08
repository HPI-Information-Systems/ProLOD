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

            $scope.statistics = {};

            httpApi.getGraphPatternStatistics($routeParams.dataset, [$routeParams.group], pattern).then(function(data) {
                $scope.statistics = data.data.statistics;
            });
        }]);

});


