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
                    /*.on('tick', function() {
                          var g = svg.select('g');
                          svg.attr("viewBox", "0 0 300 300") })*/
                    .append('svg:g');

                svg.append('svg:rect')
                    .attr('width', width)
                    .attr('height', height)
                    .attr('fill', 'white');


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
                    .attr("uri", function (d) { return d.uri; })
                    .attr("r", 5)
                    .style("fill", color)

                var tickTime = 2000;
                var initialTicks = 20;

                var force = d3.layout.force()
                    .charge(-120)
                    .linkDistance(40)
                    .size([width, height]);

                setTimeout(function() {
                    force.stop();
                },tickTime);

                node.call(force.drag);



                //if it's the second view
                if (showArrows) {
                    svg.append("defs").selectAll("marker")
                        .data(["suit", "licensing", "resolved"])
                        .enter().append("marker")
                        .attr("id", function (d) { return d; })
                        .attr("viewBox", "0 -5 10 10")
                        .attr("refX", 16)
                        .attr("refY", 0)
                        .attr("markerWidth", 10)
                        .attr("markerHeight", 10)
                        .attr("orient", "auto")
                        .append("path")
                        .attr("d", "M0,-5L10,0L0,5 L10,0 L0, -5")
                        .style("stroke", "#BBBBBB")
                        .style("stroke-width",1.1)
                        .style("opacity", "1");

                    node.append("svg:title")
                        .text(
                        function(d){
                            return (d.uri)
                        }
                    );

                    link.append("svg:title")
                        .text(
                        function(d){
                            return (d.uri)
                        }
                    );

                    link.on("mouseover", mouseover);
                    link.on("mouseout", mouseout);

                }

                function mouseover() {
                    link.attr('stroke-width', 2);
                    svg.append("defs").selectAll("marker")
                        .enter().append("marker")
                        .attr("refX", 1);
                }

                function mouseout() {
                    link.attr('stroke-width', 1);
                }


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

                for (var i = 0; i < initialTicks; ++i) force.tick();

            }

            function buildGraph(scopeGraph) {
                var graph = {
                    nodes: [],
                    links: []
                };

                var nodeMap = {};
                scopeGraph.nodes.forEach(function (node) {
                    var n = {
                        group: node.group,
                        uri: node.uri
                    };
                    nodeMap[node.id] = n;
                    graph.nodes.push(n)
                });

                scopeGraph.links.forEach(function (link) {
                    graph.links.push({
                        source: nodeMap[link.source],
                        target: nodeMap[link.target],
                        uri: link.uri
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
                template: [
                    '<div>{{graph.name}} ',
                        '<span ng-if="graph.occurences>=0">({{graph.occurences}}x)</span>',
                    '</div>'
                ].join('\n')
            };
        }
        ])
});