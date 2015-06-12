"use strict";

define(['angular', './directives'], function (angular) {

    angular.module('Prolod2.directives')
        .directive('prolodGraphThumbnail', [function () {

            function linkFunction($scope, element, attrs) {

                var graph = buildGraph($scope.graph);

                // do d3 stuff
                var width = 200,
                    height = 150;

                var fill = d3.scale.category20();

                var color = $scope.colorFunction;

                var showArrows = $scope.showArrows == 'true';

                var svg = d3.select(element[0])
                    .append("svg")
                    .attr("width", width)
                    .attr("height", height)
                    .attr("pointer-events", "all")
                    .attr("preserveAspectRatio", "xMinYMin meet")
                    .on('tick', function() {
                                                     var g = svg.select('g');
                                                      svg.attr("viewBox", "0 0 300 300") })
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
                    .style("fill", color)
                    .call(force.drag);

                force.nodes(graph.nodes)
                    .links(graph.links)
                    .start()
                    .on("tick", function () {
                            link.attr("x1", function (d) {return d.source.x;})
                                .attr("y1", function (d) {return d.source.y;})
                                .attr("x2", function (d) {return d.target.x;})
                                .attr("y2", function (d) {return d.target.y;});

                            node.attr("cx", function (d) {return d.x;})
                                .attr("cy", function (d) {return d.y;});
                        });

                for (var i = 0; i < 20; ++i) force.tick();

            }

            function buildGraph(scopeGraph) {
                var graph = {
                    nodes: [],
                    links: []
                };

                var nodeMap = {};
                scopeGraph.nodes.forEach(function (node) {
                    var n = {
                        group: node.group
                    };
                    nodeMap[node.id] = n;
                    graph.nodes.push(n)
                });

                scopeGraph.links.forEach(function (link) {
                    graph.links.push({
                        source: nodeMap[link.source],
                        target: nodeMap[link.target]
                    })
                });
                return graph;
            }

            return {
                link: linkFunction,
                controller: ['$scope', '$http', function ($http) { }],
                scope: {
                    graph: '=',
                    colorFunction: '=',
                    showArrows: '@'
                },
                restrict: 'EA',
                template: '<div>{{graph.name}} ({{graph.occurences}}x)</div>'
            };
        }
        ])
});