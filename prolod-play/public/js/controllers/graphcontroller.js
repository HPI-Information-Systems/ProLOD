'use strict';
    define(function () {


        var GraphCtrl = function ($scope) {



//        var width = 1200,
//            height = 600,
//            fill = d3.scale.category20();
//
//        var color = d3.scale.category20();
//
//        var force = d3.layout.force()
//            .charge(-120)
//            .linkDistance(60)
//            .size([width, height]);
//
//        var svg = d3.select("#graph").append("svg")
//            .attr("width", width)
//            .attr("height", height)
//            .attr("pointer-events", "all")
//            .append('svg:g')
//            .call(d3.behavior.zoom().on("zoom", redraw));
//
//
//        function redraw() {
//            console.log("here", d3.event.translate, d3.event.scale);
//            svg.attr("transform",
//                "translate(" + d3.event.translate + ")"
//                + " scale(" + d3.event.scale + ")");
//        }
//
//
//        svg.append('svg:rect')
//            .attr('width', width)
//            .attr('height', height)
//            .attr('fill', 'white');
//
//        svg.append("defs").selectAll("marker")
//            .data(["suit", "licensing", "resolved"])
//            .enter().append("marker")
//            .attr("id", function(d) { return d; })
//            .attr("viewBox", "0 -5 10 10")
//            .attr("refX", 19)
//            .attr("refY", 0)
//            .attr("markerWidth", 6)
//            .attr("markerHeight", 6)
//            .attr("orient", "auto")
//            .append("path")
//            .attr("d", "M0,-5L10,0L0,5 L10,0 L0, -5")
//            .style("stroke", "#bbb")
//            //.style("opacity", "0.6");
//
//        var jsonURL = "http://localhost:9000/personslink";
//
//        d3.json(jsonURL, function(error, graph) {
//            force
//                .nodes(graph.nodes)
//                .links(graph.links)
//                .start();
//
//            var link = svg.selectAll(".link")
//                .data(graph.links)
//                .enter().append("line")
//                .attr("class", "link")
//                .style("marker-end",  "url(#suit)")
//                .on('click', clicklink)
//                .on('mouseover', link_in)
//                .on('mouseout', link_out)
//                .style("stroke-width", function(d) { return Math.sqrt(d.value); });
//
//           /*$('line').tipsy({
//                gravity: 'w',
//                html: true,
//                title: function() {
//                    var d = this.__data__;
//                    return "Node: "+ d.id+" | Source: "+ d.source.firstName +" "+ d.source.lastName +" | Target: "+ d.target.firstName +" "+ d.target.lastName +" | Value: "+ d.value;
//                }
//            });*/
//
//            var node = svg.selectAll(".node")
//                .data(graph.nodes)
//                .enter().append("circle")
//                .attr("class", "node")
//                .attr("r", 5)
//                .style("fill", function(d) { return color(d.group); })
//                .call(force.drag)
//                .on('click', connectedNodes)
//                .on('mouseover', node_in)
//                .on('mouseout', node_out);
//
//            node.append("title")
//                .text(function(d) { return "Node: "+ d.id+" | Name: "+ d.firstName+" "+ d.lastName+" | Age: "+ d.age+" | Group: "+ d.group; });
//
//           /* node.append("text")
//                .attr("x", 12)
//                .attr("dy", ".35em")
//                .text(function(d) { return d.id; });*/
//
//            link.append("title")
//                .text(function(d) { return "Node: "+ d.id+" | Source: "+ d.source.firstName +" "+ d.source.lastName +" | Target: "+ d.target.firstName +" "+ d.target.lastName +" | Value: "+ d.value; })
//
//
//                /*$('circle').tipsy({
//                gravity: 'w',
//                html: true,
//                title: function() {
//                    var d = this.__data__;
//                    return "Node: "+ d.id+" | Name: "+ d.firstName+" "+ d.lastName+" | Age: "+ d.age+" | Group: "+ d.group;
//                }
//            });
//*/
//
//
//
//            force.on("tick", function() {
//                link.attr("x1", function(d) { return d.source.x; })
//                    .attr("y1", function(d) { return d.source.y; })
//                    .attr("x2", function(d) { return d.target.x; })
//                    .attr("y2", function(d) { return d.target.y; });
//
//                node.attr("cx", function(d) { return d.x; })
//                    .attr("cy", function(d) { return d.y; });
//            });
//
//
//            //Toggle stores whether the highlighting is on
//            var toggle = 0;
//            var i;
//
//            //Create an array logging what is connected to what
//            var linkedByIndex = {};
//            for (i = 0; i < graph.nodes.length; i++) {
//                linkedByIndex[i + "," + i] = 1;
//            };
//            graph.links.forEach(function (d) {
//                linkedByIndex[d.source.index + "," + d.target.index] = 1;
//            });
//
//            //This function looks up whether a pair are neighbours
//            function neighboring(a, b) {
//                return linkedByIndex[a.index + "," + b.index];
//            }
//
//
//            function link_in(){
//                var link = d3.select(this);
//                var mark = d3.select(".marker");
//                    mark.style("stroke", "black");
//                link.style('stroke-width', 2);
//                link.style("stroke", "black");
//            }
//            function link_out(){
//                var link = d3.select(this);
//                link.style('stroke-width', 1);
//                link.style("stroke", "#bbb");
//            }
//
//            function node_in(){
//                var node = d3.select(this);
//                node.style('stroke-width', 2);
//
//                var labels = node.append("text")
//                    .text(function(d) { return d.name; });
//            }
//            function node_out(){
//                var node = d3.select(this);
//                node.style('stroke-width', 1);
//            }
//
//            function clicklink(){
//                var link = d3.select(this);
//                link.append("text")
//                    .text(function(d) { return "safasfa"; });
//            }
//
//
//            function connectedNodes() {
//
//                if (toggle == 0) {
//                    //Reduce the opacity of all but the neighbouring nodes
//                    var d = d3.select(this).node().__data__;
//                    node.style("opacity", function (o) {
//                        return neighboring(d, o) | neighboring(o, d) ? 1 : 0.1;
//                    });
//
//                    link.style("opacity", function (o) {
//                        return d.index==o.source.index | d.index==o.target.index ? 1 : 0.1;
//                    });
//
//                    //Reduce the op
//
//                    toggle = 1;
//                } else {
//                    //Put them back to opacity=1
//                    node.style("opacity", 1);
//                    link.style("opacity", 1);
//                    toggle = 0;
//                }
//
//            }
//        });

            var width = 1200,
                height = 600,
                fill = d3.scale.category20();

            var color = d3.scale.category20();

            var force1 = d3.layout.force()
                .charge(-120)
                .linkDistance(40)
                .size([width, height]);

            var force = d3.layout.force()
                .charge(-120)
                .linkDistance(40)
                .size([width, height]);

            var svg1 = d3.select("#graph_1").append("svg")
                .attr("width", width)
                .attr("height", height)
                .attr("pointer-events", "all")
                .append('svg1:g')
                .call(d3.behavior.zoom().on("zoom", redraw1));

            var svg2 = d3.select("#graph_2").append("svg")
                .attr("width", width)
                .attr("height", height)
                .attr("pointer-events", "all")
                .append('svg:g')
                .call(d3.behavior.zoom().on("zoom", redraw2));


            function redraw1() {
                console.log("here", d3.event.translate, d3.event.scale);
                svg1.attr("transform",
                    "translate(" + d3.event.translate + ")"
                    + " scale(" + d3.event.scale + ")");
            }
            function redraw2() {
                console.log("here", d3.event.translate, d3.event.scale);
                svg2.attr("transform",
                    "translate(" + d3.event.translate + ")"
                    + " scale(" + d3.event.scale + ")");
            }


            svg1.append('svg:rect')
                .attr('width', width)
                .attr('height', height)
                .attr('fill', 'white');
            svg2.append('svg:rect')
                .attr('width', width)
                .attr('height', height)
                .attr('fill', 'white');

            var jsonURL = "http://localhost:9000/personslink";

            d3.json(jsonURL, function (error, graph) {

                var nodes = graph.nodes.slice(),
                    links = [],
                    bilinks = [];

                graph.links.forEach(function (link) {
                    var s = nodes[link.source],
                        t = nodes[link.target],
                        i = {}; // intermediate node
                    nodes.push(i);
                    links.push({source: s, target: i}, {source: i, target: t});
                    bilinks.push([s, i, t]);
                });


                var link1 = svg1.selectAll(".link")
                    .data(graph.links)
                    .enter().append("line")
                    .attr("class", "link")
                    .style("marker-end",  "url(#suit)")
                    .on('click', clicklink)
                    .on('mouseover', link_in)
                    .on('mouseout', link_out)
                    .style("stroke-width", function(d) { return Math.sqrt(d.value); });

                var node1 = svg1.selectAll(".node")
                    .data(graph.nodes)
                    .enter().append("circle")
                    .attr("class", "node")
                    .attr("r", 5)
                    .style("fill", function(d) { return color(d.group); })
                    .call(force1.drag)
                    .on('click',
                    connectedNodes1)
                    .on('mouseover', node_in)
                    .on('mouseout', node_out);



                force1
                    .nodes(graph.nodes)
                    .links(graph.links)
                    .start();

                force
                    .nodes(nodes)
                    .links(links)
                    .start();

                var link = svg2.selectAll(".link")
                    .data(bilinks)
                    .enter().append("path")
                    .attr("class", "link")
                    .on('click', clicklink)
                    .on('mouseover', link_in)
                    .on('mouseout', link_out)
                    .style("stroke-width", function (d) {
                        return Math.sqrt(d.value);
                    });

                var node = svg2.selectAll(".node")
                    .data(graph.nodes)
                    .enter().append("circle")
                    .attr("class", "node")
                    .attr("r", 5)
                    .style("fill", function (d) {
                        return color(d.group);
                    })
                    .call(force.drag)
                    .on('click', connectedNodes)
                    .on('mouseover', node_in)
                    .on('mouseout', node_out);

                node.append("title")
                    .text(function (d) {
                        return "Node: " + d.id + " | Name: " + d.firstName + " " + d.lastName + " | Age: " + d.age + " | Group: " + d.group;
                    });

                force.on("tick", function () {
                    link.attr("d", function (d) {
                        return "M" + d[0].x + "," + d[0].y
                            + "S" + d[1].x + "," + d[1].y
                            + " " + d[2].x + "," + d[2].y;
                    });
                    node.attr("transform", function (d) {
                        return "translate(" + d.x + "," + d.y + ")";
                    });
                });


                force1.on("tick", function() {
                    link1.attr("x1", function(d) { return d.source.x; })
                        .attr("y1", function(d) { return d.source.y; })
                        .attr("x2", function(d) { return d.target.x; })
                        .attr("y2", function(d) { return d.target.y; });

                    node1.attr("cx", function(d) { return d.x; })
                        .attr("cy", function(d) { return d.y; });
                });



                function link_in() {
                    var link = d3.select(this);
                    link.style('stroke-width', 2);
                    link.style("stroke", "black");
                }

                function link_out() {
                    var link = d3.select(this);
                    link.style('stroke-width', 1);
                    link.style("stroke", "#bbb");
                }

                function node_in() {
                    var node = d3.select(this);
                    node.style('stroke-width', 2);

                    var labels = node.append("text")
                        .text(function (d) {
                            return d.name;
                        });
                }

                function node_out() {
                    var node = d3.select(this);
                    node.style('stroke-width', 1);
                }

                function clicklink() {
                    var link = d3.select(this);
                    link.append("text")
                        .text(function (d) {
                            return "safasfa";
                        });
                }

                //Toggle stores whether the highlighting is on
                var toggle = 0;
                var i;

                //Create an array logging what is connected to what
                var linkedByIndex = {};
                for (i = 0; i < graph.nodes.length; i++) {
                    linkedByIndex[i + "," + i] = 1;
                }
                ;
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
                            return d.index == o.source.index | d.index == o.target.index ? 1 : 0.1;
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


                //Toggle stores whether the highlighting is on
                var toggle1 = 0;
                var i1;

                //Create an array logging what is connected to what
                var linkedByIndex1 = {};
                for (i1 = 0; i1 < graph.nodes.length; i1++) {
                    linkedByIndex1[i1 + "," + i1] = 1;
                }
                ;
                graph.links.forEach(function (d) {
                    linkedByIndex1[d.source.index + "," + d.target.index] = 1;
                });

                //This function looks up whether a pair are neighbours
                function neighboring1(a, b) {
                    return linkedByIndex1[a.index + "," + b.index];
                }

                function connectedNodes1() {


                    if (toggle1 == 0) {
                        //Reduce the opacity of all but the neighbouring nodes
                        var d = d3.select(this).node().__data__;
                        node1.style("opacity", function (o) {
                            return neighboring1(d, o) | neighboring1(o, d) ? 1 : 0.1;
                        });

                        link1.style("opacity", function (o) {
                            return d.index == o.source.index | d.index == o.target.index ? 1 : 0.1;
                        });

                        //Reduce the op

                        toggle1 = 1;
                    } else {
                        //Put them back to opacity=1
                        node1.style("opacity", 1);
                        link1.style("opacity", 1);
                        toggle1 = 0;
                    }
                }



            });
        }

        GraphCtrl.$inject = ['$scope'];

        return GraphCtrl;
    });
