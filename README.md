# ProLOD++

[ProLOD++](https://hpi.de/naumann/projects/data-profiling-and-analytics/prolod.html) is a  project of the Hasso-Plattner-Institut ([HPI](http://www.hpi.de)). ProLOD contains algorithms to perform data profiling on graph data.

ProLOD is supplied under the [Creative Commons BY-SA](https://creativecommons.org/licenses/by-sa/3.0/) license. You can use and extend the tool to develop your own profiling algorithms. 

### Building ProLOD 
ProLOD is a GWT Project and currently uses the rather old gwt-2.0.1 sdk. The current Google Plugin for Eclipse doesn't support this anymore so you will need the [older version 3.7.0 (for eclipse 4.3/4.4)](https://commondatastorage.googleapis.com/eclipse_toolreleases/products/gpe/release/3.7.0/4.3/com.google.gdt.eclipse.suite.4.3.update.site_3.7.0.zip)

You need to download and configure the [gwt-2.0.1 sdk](http://www.gwtproject.org/versions.html) and import the supplied Eclipse project.

### Configuration
It is neccessary to configure a database that is used by prolod to keep and process the data. More information can be found in the [wiki](https://github.com/HPI-Information-Systems/ProLOD/wiki/Configuration).

#### Coding style
The project follows the [google-styleguide](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html). Please make sure that all contributions adhere to the correct format. Formatting settings for common IDEs can be found at: http://code.google.com/p/google-styleguide/. All files should contain the copyright header.

