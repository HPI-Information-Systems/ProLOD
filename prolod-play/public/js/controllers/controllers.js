/*global define */

'use strict';

define(['angular'], function (angular) {

    var Events = {
        viewChanged: 'viewChanged'
    };

    angular.module('Prolod2.controllers', [])
        .controller("MainCtrl", ['$scope', '$rootScope', '$routeParams', '$location', '$route',
            function ($scope, $rootScope, $routeParams, $location, $route) {

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
                    $rootScope.$emit(Events.viewChanged, $scope.nav);
                };

                $scope.goTo = function (params) {
                    angular.extend($scope.nav, params);
                    var url = $scope.makeUrl();
                    $location.path(url);
                };

            }])

        .controller("TreeViewController", ['$scope', 'httpApi', function ($scope, httpApi) {
            $scope.model = {
                treeOptions: {
                    nodeChildren: 'children',
                    dirSelectable: true,
                    injectClasses: {
                        ul: 'a1',
                        li: 'a2',
                        liSelected: 'a7',
                        iExpanded: 'a3',
                        iCollapsed: 'a4',
                        iLeaf: 'a5',
                        label: 'a6',
                        labelSelected: 'a8'
                    }
                },
                treeData: []
            };

            /* [
             {
             "name": "Dbpedia", dataset: "dbpedia", "children": [
             {"name": "Tiere", dataset: "dbpedia", group: "tiere"},
             {"name": "Autos", dataset: "dbpedia", group: "autos"}
             ]
             },
             {
             "name": "Drugbank", dataset: "drugbank", "children": [
             {"name": "Drugs", dataset: "drugbank", group: "drugs"},
             {"name": "Diseases", dataset: "drugbank", group: "diseases"}
             ]
             }
             ]
             */

            httpApi.getDatasets().then(function (evt) {
                var data = evt.data.datasets.map(function (ds) {
                    return {
                        name: ds.name,
                        size: ds.size,
                        dataset: ds.name,
                        children: ds.groups.map(function (group) {
                            return {
                                name: group.name,
                                size: group.size,
                                dataset: ds.name,
                                group: group.name
                            }
                        })
                    }
                });
                $scope.model.treeData = data;
            });

            $scope.onSelection = function (selected) {
                var view = $scope.nav.view ? [$scope.nav.view[0]] : [];
                $scope.goTo({dataset: selected.dataset, group: selected.group || "all", view: view});
            }
        }])
        .controller("BreadCrumbController", ['$scope', '$rootScope', function ($scope, $rootScope) {
            $scope.model = {
                breadcrumbs: []
            };

            $rootScope.$on(Events.viewChanged, function (evt, nav) {
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