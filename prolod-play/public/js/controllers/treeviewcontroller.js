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
                        iLeaf: 'glyphicon glyphicon-triangle-bottom',
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
                        children: null
                    };
                    dsNode.children = ds.groups.map(function (group) {
                        var groupNode = {
                            name: group.name,
                            size: group.size,
                            dataset: ds.id,
                            dsNode: dsNode,
                            group: group.name,
                            color: colorHash(group.name)
                        };
                        if(ds.name === params.dataset && (params.group === group.name
                                                          || params.group && params.group.indexOf(group.name) >= 0)) {
                            $scope.model.selectedNodes.push(groupNode);
                        }
                        return groupNode;
                    });

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
                // on dataset change reset view to initial graph view and disable selections
                // when the selected dataset is clicked all groups should be unselected
                if ($route.current.activetab === 'index' || $route.current.activetab === 'graphs' &&
                    ($route.current.params.dataset !== selected.dataset || !selected.group)) {
                    $scope.model.selectedNodes.length = 0;
                    $scope.model.selectedNodes.push(selected);
                    // expand node and close all other nodes
                    if(!selected.group && $scope.model.expandedNodes.indexOf(selected) === -1){
                        $scope.model.expandedNodes.length = 0;
                        $scope.model.expandedNodes.push(selected);
                    }
                    // select dataset on group selection
                    if(selected.group && $scope.model.selectedNodes.indexOf(selected.dsNode) === -1) {
                        $scope.model.selectedNodes.push(selected.dsNode);
                    }
                    var url = routeBuilder.getGraphUrl({dataset: selected.dataset, group: [selected.group]});
                    $location.url(url);
                    return;
                } else if($route.current.params.dataset === selected.dataset && selected.group && selected.group) {
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
                    console.error("undefined treeview action");
                }
                $route.updateParams(params);
            }
        }]);

});


