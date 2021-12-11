# Mana Idea Plugin
The Mana tool provides ease of use for energy consumption profiling on Intel PCs. Therefore, 
Mana RAPL leverages Intels Running Average Power Limit (RAPL) to provide Feedback to developers 
on the energy consumption of their applications. As such Mana RAPL is designed as Intellij IDEA 
plugin and thus allows to be integrated into a developers workflow when developing applications. 
Currently Mana RAPL is primarily tailored to be used in connection with Java applications, 
however its modular Design allow to further extend it to other languages and development environments.
Once Mana is installed and configured correctly, one can execute unit tests in a Java application 
using the respective Mana command runner in IDEA. Once a unit is executed using Mana RAPL, the RAPL 
command is executed in the background to record current CPU and IO energy data. After the test execution, 
the recorded samples are mapped to the respective program unit in the development environment and 
energy data is being presented. As an example the following screenshot shows methods that were 
executed using Mana RAPL. The color coding gives feedback to the developer on the energy characteristics 
of respective methods. Furthermore, details 
about the recorded data as well as an overview on the distribution of the energy consumption
is presented in a special view.

## Usage

A example of a recorded sample consists of 5 attributes. The sample below shows the Json format used to report recorded samples.

### Installing the Plugin

- install maven plugin - just run mvn install
- install MANA plugin - unzip Mana Plugin in IDEA folder → maven bash script runner - exec plugin?
- install RAPL Command - exec plugin?
- [ ]  Provide an Install script and plugin

### Project Setup and Preliminaries

- install maven plugin - just run mvn install
- install MANA plugin - unzip Mana Plugin in IDEA folder → maven bash script runner - exec plugin?
- install RAPL Command - exec plugin?
- [ ]  Provide an Install script and plugin

## Architecture

- [ ]  Describe architecture and redraw image

Once a run is started the recorded data samples are stored in a `.mana` folder relative to 
the current project being opened in IDEA. When


## Features

### Summary Window

The summary window prints details about recorded energy traces on a per class basis. Once the view is opened, it refreshes itself with respect to the currently selected/opened class in the editor. The window provides detailed information about the recorded samples per method and data.

- [ ]  Currently, only the most recent measurement is visualized in the plot. In the future, build totals and update on selection.

### Line Annotator

The line annotator when refreshed queries the service for the most recent energy data 
for respective method being rendered. After that it computes the energy consumption and 
prints it alongside a color coding of the method.

As depicted in the figure above, once energy data is available for a particular method, 
the method signature is followed by a visual representation of the energy characteristics 
obtained via RAPL during method execution. The first value represents the average total 
energy consumption over all available samples. Additionally, the plot gives the user 
feedback on how the energy consumption has developed since the first obtained sample. 
To achieve this, all available samples for that particular method are sorted using their 
record date. The height and the color of the bars gives visual feedback on the energy 
consumption per each sample and thus allows for an easy inspection how the energy consumption 
evolved over time. The second value represents the average energy consumption for the most 
recent sample obtained. Finally, the second bar-chart allows to easily compare the energy 
consumption amongst methods in the same class. In essence, the color as well as the length 
of the bar give feedback on the relative energy contribution of respective method compared 
to the other available methods in the same class.

### Method Heatmap

Consider we have N samples for Method X, whereas a sample is a collection of measurements 
per for a data. Then, use these N samples and bin them in one of the ten bins. each bin 
is then counter of how many elements are there → the number o

### Run Mana Energy Profiler

- [ ]  [https://plugins.jetbrains.com/docs/intellij/run-configuration-management.html#creating-configurations-from-context](https://plugins.jetbrains.com/docs/intellij/run-configuration-management.html#creating-configurations-from-context)  Run from gutter
- [ ]  [https://upsource.jetbrains.com/idea-ce/file/idea-ce-f3337ede11680b745f513943772150ee781a51c0/java/execution/impl/src/com/intellij/execution/configurations/JavaCommandLineState.java?_ga=2.147355447.1055181884.1633813665-678432071.1633813665](https://upsource.jetbrains.com/idea-ce/file/idea-ce-f3337ede11680b745f513943772150ee781a51c0/java/execution/impl/src/com/intellij/execution/configurations/JavaCommandLineState.java?_ga=2.147355447.1055181884.1633813665-678432071.1633813665)

### Editor Summary Report

Once data is recorded, there should be a possibility to view a report with charts and tables that summarizes

- a) the evolution of a method over time
- b) the evolution of all methods over time
- [ ]  Implement evolution over time report for a single method
- [ ]  Implement evolution over time report for all methods

## Conclusion

## References
