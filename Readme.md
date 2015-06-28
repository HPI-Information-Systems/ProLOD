# ProLOD

[ProLOD++](https://hpi.de/naumann/projects/data-profiling-and-analytics/prolod.html) is a  project of the Hasso-Plattner-Institut ([HPI](http://www.hpi.de)). ProLOD++ contains algorithms to perform data profiling on Linked Data.


Usage
=====

commandline
---

- install sbt
- create a database.conf in prolod-play/conf
- execute ```sbt "project prolod_play" run```


idea
----

- create new launch config
- add a new sbt task with ```"project prolod_play" run``` as parameter
- remove 'Make' from before launch settings
- run or debug with this config

Packaging
---------

- execute ```sbt "project prolod_play" dist```
