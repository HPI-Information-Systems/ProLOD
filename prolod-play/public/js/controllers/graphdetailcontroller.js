'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("GraphDetailCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', function ($scope, $routeParams, routeBuilder, httpApi) {
            var pattern = $routeParams.pattern;
            var detail = $routeParams.detail;
            $scope.updateBreadcrumb([
                {name:'graphs', url: routeBuilder.getGraphUrl()},
                {name:'pattern ' + pattern, url: routeBuilder.getGraphPatternUrl(pattern)},
                {name:'detail ' + detail, url: routeBuilder.getGraphDetailUrl(pattern, detail)}
            ]);

            $scope.data = {
                pattern: {}
            };

            httpApi.getGraphDetail($routeParams.dataset, [$routeParams.group], pattern, detail).then(function(data) {
                var stats = data.data.statistics;
            });
        }]);

});


