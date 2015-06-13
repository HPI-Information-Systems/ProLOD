'use strict';

define(['angular', './controllers'], function (angular) {
    angular.module('Prolod2.controllers')
        .controller("TreeViewController", ['$scope', '$route', '$location', 'httpApi', 'routeBuilder', 'colorHash', function (
                                           $scope, $route, $location, httpApi, routeBuilder, colorHash) {
            $scope.model = {
                treeOptions: {
                    nodeChildren: 'children',
                    dirSelectable: true,
                    injectClasses: {
                        ul: 'a1',
                        li: 'a2',
                        liSelected: 'tree-selected',
                        iExpanded: 'glyphicon glyphicon-triangle-bottom',
                        iCollapsed: 'glyphicon glyphicon-triangle-right',
                        iLeaf: 'glyphicon glyphicon-triangle-bottom',
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

            $scope.loading = true;

            httpApi.getDatasets().then(function (evt) {
                $scope.loading = false;

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
                                group: group.name,
                                color: colorHash(group.name)
                            }
                        })
                    }
                });
                $scope.model.treeData = data;
            });

            $scope.onSelection = function (selected) {
                var params = angular.extend({},$route.current.params);
                if ($route.current.activetab === 'index') {
                    var url = routeBuilder.getGraphUrl({dataset: selected.dataset, group: selected.group});
                    $location.url(url);
                    return;
                }

                if ($route.current.activetab === 'graphs' &&
                    ($route.current.params.dataset !== selected.dataset || ! selected.group)) {
                    var url = routeBuilder.getGraphUrl({dataset: selected.dataset, group: selected.group});
                    $location.url(url);
                    return;
                }

                if($route.current.params.dataset === selected.dataset) {
                    if(selected.group) {
                        params.group = selected.group;
                    } else {
                        params.group = undefined;
                    }
                } else {
                    params.dataset = selected.dataset;
                }
                $route.updateParams(params);
            }
        }]);

});


