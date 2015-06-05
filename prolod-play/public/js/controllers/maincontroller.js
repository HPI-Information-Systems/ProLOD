'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("MainCtrl", ['$scope', '$rootScope', '$routeParams', '$location', '$route', 'Events',
         function ($scope, $rootScope, $routeParams, $location, $route, Events) {

             $scope.nav = {
                 view: []
             };

             $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
                 angular.extend($scope.nav, current.params);
                 $scope.currentRoute = current;
             });

             $scope.makeUrl = function (params) {
                 params = params || {};
                 var dataset = params.dataset || $scope.nav.dataset;
                 var group = params.group || $scope.nav.group;
                 var view = params.view || $scope.nav.view;
                 return '/' + dataset + '/' + group + '/' + view.join('/');
             };

             $scope.init = function () {
                 console.log('init!');
             };

             $scope.updateView = function (viewChain) {
                 $scope.nav.view = viewChain;
                 console.log('view: ' + JSON.stringify($scope.nav));
                 $rootScope.$emit(Events.VIEWCHANGED, $scope.nav);
             };

             $scope.goTo = function (params) {
                 angular.extend($scope.nav, params);
                 var url = $scope.makeUrl();
                 $location.path(url);
             };

         }]);

});


