'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("MainCtrl", ['$scope', '$rootScope', '$location', '$route', 'routeBuilder', 'Events',
         function ($scope, $rootScope, $location, $route, routeBuilder, Events) {

             if(!$route.current) {
                // fix route.current is undefined on load
                 $route.current = {params: {}}
             }

             $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
                 $scope.currentRoute = current;
             });

             $scope.routeBuilder = routeBuilder;

             $scope.init = function () {
                 //console.log('init!');
             };

             $scope.updateBreadcrumb = function (viewChain) {
                 //console.log('view: ' + JSON.stringify(viewChain));
                 $rootScope.$emit(Events.VIEWCHANGED, viewChain);
             };

         }]);
});


