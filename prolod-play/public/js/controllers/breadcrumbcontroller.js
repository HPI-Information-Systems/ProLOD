'use strict';

define(['angular', './controllers'], function (angular) {
    angular.module('Prolod2.controllers')
        .controller("BreadCrumbController", ['$scope', '$rootScope', 'Events', function ($scope, $rootScope, Events) {
            $scope.model = {
                breadcrumbs: []
            };

            $rootScope.$on(Events.VIEWCHANGED, function (evt, nav) {
                if (nav.view == 'index') {
                    $scope.model.breadcrumbs = [];
                    return
                }
                var crumbs = [];
                var url = '/' + nav.dataset;
                crumbs.push({url: url, name: nav.dataset});
                url += '/' + nav.group;
                if (nav.group !== 'all') {
                    crumbs.push({url: url, name: nav.group});
                }

                nav.view.forEach(function (v) {
                    url += '/' + v;
                    crumbs.push({url: url, name: v});
                });
                $scope.model.breadcrumbs = crumbs;
            });
        }]);

});


