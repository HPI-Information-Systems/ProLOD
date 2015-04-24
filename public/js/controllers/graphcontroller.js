'use strict';

define(function () {

    var GraphCtrl = function ($scope) {

        var width = 1200,
            height = 600,
            fill = d3.scale.category20();

        var color = d3.scale.category20();

        var force = d3.layout.force()
            .charge(-120)
            .linkDistance(40)
            .size([width, height]);

        var svg = d3.select("#graph").append("svg")
            .attr("width", width)
            .attr("height", height)
            .attr("pointer-events", "all")
            .append('svg:g')
            .call(d3.behavior.zoom().on("zoom", redraw));

        function redraw() {
            console.log("here", d3.event.translate, d3.event.scale);
            svg.attr("transform",
                "translate(" + d3.event.translate + ")"
                + " scale(" + d3.event.scale + ")");
        }

        svg.append('svg:rect')
            .attr('width', width)
            .attr('height', height)
            .attr('fill', 'white');


        var jsonURL = "http://localhost:9000/personslink";

        d3.json(jsonURL, function(error, graph) {
            force
                .nodes(graph.nodes)
                .links(graph.links)
                .start();

            var link = svg.selectAll(".link")
                .data(graph.links)
                .enter().append("line")
                .attr("class", "link")
                .on('click', connectedNodes)/*
                .on('mouseover', highlightin)
                .on('mouseout', highlightout)*/
                .style("stroke-width", function(d) { return Math.sqrt(d.value); });

            var node = svg.selectAll(".node")
                .data(graph.nodes)
                .enter().append("circle")
                .attr("class", "node")
                .attr("r", 5)
                .style("fill", function(d) { return color(d.group); })
                .call(force.drag)
                .on('click', connectedNodes);


            node.append("title")
                .text(function(d) { return d.name; });

            force.on("tick", function() {
                link.attr("x1", function(d) { return d.source.x; })
                    .attr("y1", function(d) { return d.source.y; })
                    .attr("x2", function(d) { return d.target.x; })
                    .attr("y2", function(d) { return d.target.y; });

                node.attr("cx", function(d) { return d.x; })
                    .attr("cy", function(d) { return d.y; });
            });

            //Toggle stores whether the highlighting is on
            var toggle = 0;
            var i;

            //Create an array logging what is connected to what
            var linkedByIndex = {};
            for (i = 0; i < graph.nodes.length; i++) {
                linkedByIndex[i + "," + i] = 1;
            };
            graph.links.forEach(function (d) {
                linkedByIndex[d.source.index + "," + d.target.index] = 1;
            });

            //This function looks up whether a pair are neighbours
            function neighboring(a, b) {
                return linkedByIndex[a.index + "," + b.index];
            }

            function connectedNodes() {

                if (toggle == 0) {
                    //Reduce the opacity of all but the neighbouring nodes
                    var d = d3.select(this).node().__data__;
                    node.style("opacity", function (o) {
                        return neighboring(d, o) | neighboring(o, d) ? 1 : 0.1;
                    });

                    link.style("opacity", function (o) {
                        return d.index==o.source.index | d.index==o.target.index ? 1 : 0.1;
                    });

                    //Reduce the op

                    toggle = 1;
                } else {
                    //Put them back to opacity=1
                    node.style("opacity", 1);
                    link.style("opacity", 1);
                    toggle = 0;
                }

            }
        });
    }

    GraphCtrl.$inject = ['$scope'];

    return GraphCtrl;
});