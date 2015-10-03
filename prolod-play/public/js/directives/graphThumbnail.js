"use strict";

define(['angular', 'd3', './directives'], function (angular, d3) {

    angular.module('Prolod2.directives').directive('prolodGraphThumbnail', [function () {
            function linkFunction($scope, element, attrs) {

                var graph = buildGraph($scope.graph);

                // do d3 stuff
                var width = 200,
                    height = 150;
                if(window.location.href.indexOf("giantComponent") > -1) {
                    width = 300;
                    height = 250;
                }

                var fill = d3.scale.category20();

                var color = $scope.colorFunction;

                var showArrows = $scope.showArrows === 'true';

                var svg = d3.select(element[0])
                    .append("svg")
                    .attr("width", width)
                    .attr("height", height)
                    .attr("pointer-events", "all")
                    .attr("preserveAspectRatio", "xMinYMin meet");

                var g = svg.append('svg:g');

                g.append('svg:rect')
                    .attr('width', width)
                    .attr('height', height)
                    .attr('fill', 'white');


                var link = g.selectAll(".link")
                    .data(graph.links)
                    .enter().append("line")
                    .attr("class", function(d) {if (d.surrounding) return "surrounding_link"; return "link";} )
                    .style("marker-end", function(d) {if (d.surrounding) return "url(#surrounding_target)"; return "url(#target)";})
                    .style("stroke-width", function (d) {
                               return Math.sqrt(d.value);
                           });

                var node = g.selectAll(".node")
                    .data(graph.nodes)
                    .enter().append("circle")
                    .attr("class", function(d) {if (d.surrounding) return "surrounding_node"; return "node";})
                    .attr("uri", function (d) { return d.uri; })
                    .attr("label", function (d) { return d.label; })
                    .attr("r", 5)
                    .style("fill", color);

                var tickTime = 1000;
                var initialTicks = 50;

                var force = d3.layout.force()
                    .charge(-120)
                    .linkDistance(40)
                    .size([width, height]);

                node.call(force.drag);

                var forceNodes = force.nodes(graph.nodes)
                    .links(graph.links)
                    .start();

                // do some initial ticks to decrease jumping
                for (var i = 0; i < initialTicks; ++i) force.tick();

                // set svg update handler after initial ticks improves performance a lot!
                forceNodes.on("tick", function () {
                    link.attr("x1", function (d) {return d.source.x;})
                        .attr("y1", function (d) {return d.source.y;})
                        .attr("x2", function (d) {return d.target.x;})
                        .attr("y2", function (d) {return d.target.y;});

                    var inf = 1000000;
                    var minX = inf, maxX = -inf, minY=inf, maxY=-inf;
                    node.attr("cx", function (d) {
                        maxX = Math.max(maxX, d.x);
                        minX = Math.min(minX, d.x);
                        return d.x;
                    }).attr("cy", function (d) {
                        maxY = Math.max(maxY, d.y);
                        minY = Math.min(minY, d.y);
                        return d.y;
                    });

                    var padding = 10;
                    minX -= padding; maxX += padding;
                    minY -= padding; maxY += padding;
                    minX = Math.min(minX, 0); minY = Math.min(minY, 0);
                    maxX = Math.max(maxX, width); maxY = Math.max(maxY, height);
                    var w = maxX - minX; var h = maxY - minY;
                    svg.attr("viewBox", "" + minX + " " + minY + " " + w + " " + h);
                });

                force.tick(); // one last tick that updates the nodes

                setTimeout(function() {
                    force.stop();
                }, tickTime);

                //if it's the second view
                if (showArrows) {
                    svg.append("defs").selectAll("marker")
                        .data(["target"])
                        .enter().append("svg:marker")
                        .attr("id", function (d) { return d; })
                        .attr("viewBox", "0 -5 10 10")
                        .attr("refX", 15)
                        .attr("refY", -1.5)
                        .attr("markerWidth", 6)
                        .attr("markerHeight", 6)
                        .attr("orient", "auto")
                        .style("fill", "#bbb")
                        .append("svg:path")
                        .attr("d", "M0,-5L10,0L0,5");
                        //.attr("id", "arrowHead");

                    svg.append("defs").selectAll("marker")
                        .data(["surrounding_target"])
                        .enter().append("svg:marker")
                        .attr("id", function (d) { return d; })
                        .attr("viewBox", "0 -5 10 10")
                        .attr("refX", 15)
                        .attr("refY", -1.5)
                        .attr("markerWidth", 6)
                        .attr("markerHeight", 6)
                        .attr("orient", "auto")
                        .style("fill", "#E8E8E8")
                        .append("svg:path")
                        .attr("d", "M0,-5L10,0L0,5");
                    //.attr("id", "arrowHead");

                    svg.append("defs").selectAll("marker")
                        .data(["target_red"])
                        .enter().append("svg:marker")
                        .attr("id", function (d) { return d; })
                        .attr("viewBox", "0 -5 10 10")
                        .attr("refX", 15)
                        .attr("refY", -1.5)
                        .attr("markerWidth", 6)
                        .attr("markerHeight", 6)
                        .attr("orient", "auto")
                        .style("fill", "red")
                        .append("svg:path")
                        .attr("d", "M0,-5L10,0L0,5");

                    svg.append("defs").selectAll("marker")
                        .data(["target_lightred"])
                        .enter().append("svg:marker")
                        .attr("id", function (d) { return d; })
                        .attr("viewBox", "0 -5 10 10")
                        .attr("refX", 15)
                        .attr("refY", -1.5)
                        .attr("markerWidth", 6)
                        .attr("markerHeight", 6)
                        .attr("orient", "auto")
                        .style("fill", "#FFAD99")
                        .append("svg:path")
                        .attr("d", "M0,-5L10,0L0,5");


                    node.append("svg:title")
                        .text(
                            function(d){
                                var nodeTitle = d.uri;
                                if (d.label) {
                                    nodeTitle = d.label + " " + nodeTitle;
                                }
                                return nodeTitle
                            }
                        );

                    link.append("svg:title")
                        .text(
                        function(d){
                            var linkTitle = d.uri;
                            if (d.label) {
                                linkTitle = d.label;
                            }
                            return linkTitle
                        }
                    );


                    link.on("mouseover", mouseover);
                    link.on("mouseout", mouseout);

                    var link_s = g.selectAll(".surrounding_link")
                    link_s.on("mouseover", mouseover_surrounding);
                    link_s.on("mouseout", mouseout_surrounding);

                }

                var flag = 0;

                node.on("mousedown",function(){
                    flag = 0;
                }, false);

                node.on("mousemove", function(){
                    flag = 1;
                }, false);

                node.on("mouseup", function(node){
                    if (flag === 0){
                        var link = d3.select(this);
                        console.log("Node");
                        $scope.clickHandler(node);
                    } else if(flag === 1) {
                        console.log("drag");
                    }
                }, false);

                function mouseover() {
                    var link = d3.select(this);
                    link.style("stroke", "red");
                    link.style("marker-end", "url(#target_red)");
                }

                function mouseover_surrounding() {
                    var link = d3.select(this);
                    link.style("stroke", "#FFAD99");
                    link.style("marker-end", "url(#target_lightred)");
                }

                function mouseout() {
                    var link = d3.select(this);
                    link.style("stroke", "#bbb");
                    link.style("marker-end", "url(#target)");
                }

                function mouseout_surrounding() {
                    var link = d3.select(this);
                    link.style("stroke", "#E8E8E8");
                    link.style("marker-end", "url(#surrounding_target)");
                }

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
                        id: node.id,
                        uri: node.uri,
                        label: node.label,
                        surrounding: node.surrounding
                    };
                    nodeMap[node.id] = n;
                    graph.nodes.push(n)
                });

                scopeGraph.links.forEach(function (link) {
                    graph.links.push({
                        source: nodeMap[link.source],
                        target: nodeMap[link.target],
                        uri: link.uri,
                        label: link.label,
                        surrounding: link.surrounding
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
                    clickHandler: '=',
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
