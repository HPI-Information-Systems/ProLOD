'use strict';

define(['angular', './controllers'], function (angular) {
    angular.module('Prolod2.controllers')
        .controller("TreeViewController", ['$scope', '$route', '$location', 'httpApi', 'routeBuilder', 'colorHash', function (
                                           $scope, $route, $location, httpApi, routeBuilder, colorHash) {
            $scope.model = {
                treeOptions: {
                    nodeChildren: 'children',
                    dirSelectable: true,
                    multiSelection: true,
                    injectClasses: {
                        ul: 'a1',
                        li: 'a2',
                        liSelected: 'tree-selected',
                        iExpanded: 'glyphicon glyphicon-triangle-bottom',
                        iCollapsed: 'glyphicon glyphicon-triangle-right',
                        iLeaf: 'glyphicon glyphicon-asterisk',
                        label: 'a6',
                        labelSelected: 'a8'
                    }
                },
                treeData: [],
                selectedNodes: [],
                expandedNodes: []
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
                    var params = $route.current.params;
                    var dsNode = {
                        name: ds.name,
                        size: ds.size,
                        dataset: ds.id,
                        children: ds.groups.map(function (group) {
                            var groupNode = {
                                name: group.name,
                                size: group.size,
                                dataset: ds.id,
                                group: group.name,
                                color: colorHash(group.name)
                            };
                            if(ds.name === params.dataset && (params.group === group.name
                                || params.group && params.group.indexOf(group.name) >= 0)) {
                                $scope.model.selectedNodes.push(groupNode);
                            }
                            return groupNode;
                        })
                    };
                    if(dsNode.name === params.dataset) {
                        $scope.model.expandedNodes.push(dsNode);
                    }
                    if(dsNode.name === params.dataset) {
                        $scope.model.selectedNodes.push(dsNode);
                    }
                    return dsNode;
                });
                $scope.model.treeData = data;
            });

            $scope.onSelection = function (selected) {
                var params = angular.extend({}, $route.current.params);

                // on first selection redirect to graph view
                // on dataset reset view to initial graph view and disable selections
                if ($route.current.activetab === 'index' || $route.current.activetab === 'graphs' &&
                    ($route.current.params.dataset !== selected.dataset)) {
                    $scope.model.selectedNodes.length = 0;
                    $scope.model.selectedNodes.push(selected);
                    // expand node and close all other nodes
                    if(!selected.group && $scope.model.expandedNodes.indexOf(selected) === -1){
                        $scope.model.expandedNodes.length = 0;
                        $scope.model.expandedNodes.push(selected);
                    }
                    var url = routeBuilder.getGraphUrl({dataset: selected.dataset, group: [selected.group]});
                    $location.url(url);
                    return;
                }

                if($route.current.params.dataset === selected.dataset) {
                    if(selected.group) {
                        console.log(params.group);
                        if(!params.group) {
                            params.group = [];
                        }
                        if(typeof(params.group) == 'string') {
                            params.group = [params.group];
                        }
                        var index = params.group.indexOf(selected.group);
                        if(index >= 0) {
                            params.group.splice(index, 1);
                        } else {
                            // should multiselection be possible?
                            // params.group.length = 0;
                            params.group.push(selected.group);
                        }
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


