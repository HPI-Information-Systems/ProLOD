"use strict";

define(['angular', './directives'], function (angular) {

    angular.module('Prolod2.controllers')
        .directive('prolodGraphThumbnail', [function () {

            function linkFunction($scope, element, attrs) {
                console.log("link!");

                var graph = buildGraph($scope);

                // do d3 stuff
                var width = 200,
                    height = 150;

                var fill = d3.scale.category20();

                var color = d3.scale.category20();

                var svg = d3.select(element[0])
                    .append("svg")
                    .attr("width", width)
                    .attr("height", height)
                    .attr("pointer-events", "all")
                    .append('svg:g');

                svg.append('svg:rect')
                    .attr('width', width)
                    .attr('height', height)
                    .attr('fill', 'white');

                var force = d3.layout.force()
                    .charge(-120)
                    .linkDistance(40)
                    .size([width, height]);

                var link = svg.selectAll(".link")
                    .data(graph.links)
                    .enter().append("line")
                    .attr("class", "link")
                    .style("marker-end", "url(#suit)")
                    .style("stroke-width", function (d) {
                               return Math.sqrt(d.value);
                           });

                var node = svg.selectAll(".node")
                    .data(graph.nodes)
                    .enter().append("circle")
                    .attr("class", "node")
                    .attr("r", 5)
                    .style("fill", function (d) {
                               return color(d.group);
                           })
                    .call(force.drag);

                force.nodes(graph.nodes)
                    .links(graph.links)
                    .start()
                    .on("tick", function () {
                            link
                                .attr("x1", function (d) {
                                          return d.source.x;
                                      })
                                .attr("y1", function (d) {
                                          return d.source.y;
                                      })
                                .attr("x2", function (d) {
                                          return d.target.x;
                                      })
                                .attr("y2", function (d) {
                                          return d.target.y;
                                      });

                            node
                                .attr("cx", function (d) {
                                          return d.x;
                                      })
                                .attr("cy", function (d) {
                                          return d.y;
                                      });
                        });
            }

            function buildGraph($scope) {
                var graph = {
                    nodes: [],
                    links: []
                };

                var nodeMap = {};
                $scope.graph.nodes.forEach(function (node) {
                    var n = {
                        group: node.group
                    };
                    nodeMap[node.id] = n;
                    graph.nodes.push(n)
                });

                $scope.graph.links.forEach(function (link) {
                    graph.links.push({
                        source: nodeMap[link.source],
                        target: nodeMap[link.target]
                    })
                });
                return graph;
            }

            return {
                link: linkFunction,
                controller: ['$scope', '$http', function ($http) {
                }],
                scope: {
                    graph: '='
                },
                restrict: 'EA',
                template: '<div>{{graph.name}} ({{graph.count}}x)</div>'
            };
        }
    ])
});