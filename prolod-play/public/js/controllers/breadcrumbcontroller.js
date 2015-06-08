'use strict';

define(['angular', './controllers'], function (angular) {
    angular.module('Prolod2.controllers')
        .controller("BreadCrumbController", ['$scope', '$rootScope', '$route', 'Events', function ($scope, $rootScope, $route, Events) {
            $scope.model = {
                breadcrumbs: []
            };

            $rootScope.$on(Events.VIEWCHANGED, function (evt, breadcrumbs) {
                $scope.model.breadcrumbs = breadcrumbs;
            });
        }]);

});


