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

            $scope.loading = true;


            function updateSelection() {
                if(!$scope.model.treeData) {
                    return;
                }

                console.log("update treeview");
                var params = $route.current.params;

                $scope.model.selectedNodes.length = 0;

                // remove all expansions from other datasets
                var filtered = $scope.model.expandedNodes.filter(function(node) {
                    if (!params.dataset) {
                        return false;
                    }
                    return node.dataset === params.dataset;
                });
                $scope.model.expandedNodes.length = 0;
                filtered.forEach(function(node) {
                    $scope.model.expandedNodes.push(node);
                });

                $scope.model.treeData.forEach(function(dsNode) {
                    if (dsNode.dataset === params.dataset) {
                        $scope.model.expandedNodes.push(dsNode);
                        $scope.model.selectedNodes.push(dsNode);
                        if(!params.group) {
                            return;
                        }
                        dsNode.children.forEach(function(groupNode) {
                            if((params.group === groupNode.group || params.group.indexOf(groupNode.group) >= 0)) {
                                $scope.model.selectedNodes.push(groupNode);
                                // TODO handle child groups!
                            }
                        });
                    }
                });
                if (!params.dataset) {
                    $scope.model.expandedNodes.push($scope.model.treeData[0]);
                    $scope.model.selectedNodes.push($scope.model.treeData[0]);
                    params.dataset = $scope.model.treeData[0].dataset;
                    $route.updateParams(params);
                    return;
                }
            }

            $scope.$on('$routeChangeSuccess', function (event, current, previous) {
                updateSelection();
            });

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
                        return groupNode;
                    });

                    return dsNode;
                });
                $scope.model.treeData = data;
                updateSelection();
            });

            $scope.onSelection = function (selected) {
                var params = angular.extend({}, $route.current.params);
                // on first selection redirect to graph view
                // on dataset change reset view to initial graph view and disable selections
                // when the selected dataset is clicked all groups should be unselected
                if ($route.current.activetab === 'index' || $route.current.activetab === 'graphs' &&
                    ($route.current.params.dataset !== selected.dataset || !selected.group)) {
                    var url = routeBuilder.getGraphUrl({dataset: selected.dataset, group: [selected.group]});
                    $location.url(url);
                    return;
                } else {
                    //console.log(params.group);
                    if(!params.group) {
                        params.group = [];
                    }
                    if(typeof(params.group) == 'string') {
                        params.group = [params.group];
                    }
                    if($route.current.params.dataset !== selected.dataset || !selected.group) {
                        params.dataset = selected.dataset;
                        params.group.length = 0;
                    }
                    var index = params.group.indexOf(selected.group);
                    if(index >= 0) {
                        params.group.splice(index, 1);
                    } else if (selected.group) {
                        // should multiselection be possible?
                        // params.group.length = 0;
                        params.group.push(selected.group);
                    }
                }
                $route.updateParams(params);
            }
        }]);

});


