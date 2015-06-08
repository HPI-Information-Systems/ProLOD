'use strict';

define(['angular', './controllers'], function (angular) {
    angular.module('Prolod2.controllers')
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
                        dataset: ds.id,
                        children: ds.groups.map(function (group) {
                            return {
                                name: group.name,
                                size: group.size,
                                dataset: ds.id,
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
        }]);

});


