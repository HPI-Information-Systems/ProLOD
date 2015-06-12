this will be the new prolod


Usage
=====

commandline
---

- install sbt
- execute ```sbt "project prolod_play" run```
- use -Dconfig.file=<path>/application.conf


idea
----

- create new launch config
- add a new sbt task with 'run' as parameter
- remove 'Make' from before launch settings
- run or debug with this config

Packaging
---------

- execute ```sbt "project prolod_play" dist```