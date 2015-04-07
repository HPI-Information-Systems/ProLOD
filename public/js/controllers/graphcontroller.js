'use strict';

define(function () {

    var GraphCtrl = function ($scope) {
        $scope.updateView(['view4']);

        var width = 960,
            height = 500,
            fill = d3.scale.category20();

        var force = d3.layout.force()
            .charge(-200)
            .linkDistance(30)
            .size([width, height]);


        var svg = d3.select("#graph").append("svg")
            .attr("width", width)
            .attr("height", height)
            .attr("pointer-events", "all")
            .append('svg:g')
            .call(d3.behavior.zoom().on("zoom", redraw))
            .append('svg:g');

        svg.append('svg:rect')
            .attr('width', width)
            .attr('height', height)
            .attr('fill', 'white');

        function redraw() {
            console.log("here", d3.event.translate, d3.event.scale);
            svg.attr("transform",
                "translate(" + d3.event.translate + ")"
                + " scale(" + d3.event.scale + ")");
        }

        var jsonURL = "http://localhost:9000/personslink";

        d3.json(jsonURL, function (error, graph) {
            force
                .nodes(graph.nodes)
                .links(graph.links)
                .start();

            var link = svg.selectAll(".link")
                .data(graph.links)
                .enter().append("line")
                .attr("class", "link")
                .style("stroke-width", function (d) {
                    return Math.sqrt(d.value);
                });

            var node = svg.selectAll(".node")
                .data(graph.nodes)
                .enter().append("circle")
                .attr("class", "node")
                .attr("r", 6)
                .style("fill", function (d) {
                    return fill(d.group);
                })
                .call(force.drag);


            node.append("title")
                .text(function (d) {
                    return d.id;
                });


            force.on("tick", function () {
                link.attr("x1", function (d) {
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

                node.attr("cx", function (d) {
                    return d.x;
                })
                    .attr("cy", function (d) {
                        return d.y;
                    });
            });
        });
/*
        var width = 960,
            height = 500;

        var color = d3.scale.category20();

        var force = d3.layout.force()
            .linkDistance(30)
            .linkStrength(1)
            .size([width, height]);

        var svg = d3.select("#graph").append("svg")
            .attr("width", width)
            .attr("height", height);

        var jsonURL = "http://localhost:9000/personslink";

        d3.json(jsonURL, function(error, graph) {
            var nodes = graph.nodes.slice(),
                links = [],
                bilinks = [];

            graph.links.forEach(function(link) {
                var s = nodes[link.source],
                    t = nodes[link.target],
                    i = {}; // intermediate node
                nodes.push(i);
                links.push({source: s, target: i}, {source: i, target: t});
                bilinks.push([s, i, t]);
            });

            force
                .nodes(nodes)
                .links(links)
                .start();

            var link = svg.selectAll(".link")
                .data(bilinks)
                .enter().append("path")
                .attr("class", "link");

            var node = svg.selectAll(".node")
                .data(graph.nodes)
                .enter().append("circle")
                .attr("class", "node")
                .attr("r", 5)
                .style("fill", function(d) { return color(d.group); })
                .call(force.drag);

            node.append("title")
                .text(function(d) { return d.name; });

            force.on("tick", function() {
                link.attr("d", function(d) {
                    return "M" + d[0].x + "," + d[0].y
                        + "S" + d[1].x + "," + d[1].y
                        + " " + d[2].x + "," + d[2].y;
                });
                node.attr("transform", function(d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
            });
        });*/

        /*var links = [
            {source: "A", target: "B", type: "licensing", label: "Label for A"},
            {source: "B", target: "A", type: "licensing", label: "Label for A"},
            {source: "B", target: "A", type: "licensing", label: "Label for b "},
            {source: "B", target: "C", type: "licensing", label: "Label for B"},
            {source: "C", target: "A", type: "licensing", label: "Label for C"}
        ];

        var nodes = {};


// Compute the distinct nodes from the links.
        links.forEach(function(link) {
            link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
            link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
        });

        var w = 960,
            h = 500;

        var force = d3.layout.force()
            .nodes(d3.values(nodes))
            .links(links)
            .size([w, h])
            .linkDistance(160)
            .charge(-10)
            .on("tick", tick)
            .start();

        var svg = d3.select("#graph").append("svg:svg")
            .attr("width", w)
            .attr("height", h);

// Per-type markers, as they don't inherit styles.
        svg.append("svg:defs").selectAll("marker")
            .data(["suit", "licensing", "resolved"])
            .enter().append("svg:marker")
            .attr("id", String)
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", 15)
            .attr("refY", -1.5)
            .attr("markerWidth", 6)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
            .append("svg:path")
            .attr("d", "M0,-5L10,0L0,5");

        var path = svg.append("svg:g").selectAll("path")
            .data(force.links())
            .enter().append("svg:path")
            .attr("class", function(d) { return "link " + d.type; })
            .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

        var circle = svg.append("svg:g").selectAll("circle")
            .data(force.nodes())
            .enter().append("svg:circle")
            .attr("r", 6)
            .call(force.drag);

        var text = svg.append("svg:g").selectAll("g")
            .data(force.nodes())
            .enter().append("svg:g");

// A copy of the text with a thick white stroke for legibility.
        text.append("svg:text")
            .attr("x", 8)
            .attr("y", ".51em")
            .attr("class", "shadow")
            .text(function(d) { return d.name; });

        text.append("svg:text")
            .attr("x", 8)
            .attr("y", ".51em")
            .text(function(d) { return d.name; });

// Use elliptical arc path segments to doubly-encode directionality.
        function tick() {
            path.attr("d", function(d) {
                var dx = d.target.x - d.source.x,
                    dy = d.target.y - d.source.y,
                    dr = Math.sqrt(dx * dx + dy * dy);
                return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
            });

            circle.attr("transform", function(d) {
                return "translate(" + d.x + "," + d.y + ")";
            });

            text.attr("transform", function(d) {
                return "translate(" + d.x + "," + d.y + ")";
            });
        }*/
    };
    GraphCtrl.$inject = ['$scope'];

    return GraphCtrl;

});