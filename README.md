# MANAi - An IntelliJ Plugin for Software Energy Consumption Profiling
The Mana tool provides ease of use for energy consumption profiling on Intel-based PCs and Macs. 
MANAi leverages *Intel's Running Average Power Limit (RAPL)* to provide feedback to developers 
from within Jetbrains Intellij IDEA development tool platform. 

Currently, MANAi is primarily tailored to be used in connection with Java applications, 
however its modular design allow to further extend it to other languages and development environments.
Once MANAi is installed and configured correctly, one can execute unit tests in Java projects 
using the respective MANAi command runner in IDEA. Recorded samples are mapped to the respective 
program unit in the development environment and 
energy data is being presented. The following screenshot shows methods that were 
executed using MANAi. The color coding gives feedback to the developer on the energy characteristics 
of respective methods.

![](doc/method_attributed.png)

## Motivation

## Getting Started

### Prerequisites
MANAi relies on *Intel's Running Average Power Limit (RAPL)*. Before using MANAi
please ensure that your system supports RAPL. For a list of supported 
CPU models refer to the following table:

| model name  | supported by RAPL  |
| --- | --- |
|   |   |
|   |   |
|   |   |

Currently, MANAi comes in two different flavors, one that supports Intel-based
Macs and the other for Windows-based systems. 

#### Apple Users
The MAC-based version relies on
measurements obtained using an adapted version of the C++ Mozilla RAPL implementation 
available under http://

> ⚠️ Be aware, that MANAi only supports Intel-based Mac systems.

#### Windows Users
The Windows version uses Intel's Power Gadget which needs to be 
installed beforehand. You can obtain Intel Power Gadget via the following
https://www.intel.com/content/www/us/en/developer/articles/tool/power-gadget.html. 
After download, run the installer and test run the power gadget app
to verify that obtaining energy measurements is available on your system.

#### Maven 
MANAi uses apache maven for communication between an application under test 
and the Intellij plugin. In order to properly execute MANAi test run, ensure
apache maven is installed on your system and properly configured. MANAi processes
the `M2_HOME` environment variable, so ensure it is properly set pointing 
to a valid maven installation directory. The plugin was tested with apache maven 3.8.4. 

### Installing MANAi
#### Installation from Pre-Built Binaries
#### Installation from Source
- install maven plugin - just run mvn install
- install MANA plugin - unzip Mana Plugin in IDEA folder → maven bash script runner - exec plugin?
- install RAPL Command - exec plugin?
- Provide an installation script and plugin

### Usage
An example of a recorded sample consists of 5 attributes. 
The sample below shows the Json format used to report recorded samples.

#### Project Setup and Preliminaries
- install maven plugin - just run mvn install
- install MANA plugin - unzip Mana Plugin in IDEA folder → maven bash script runner - exec plugin?
- install RAPL Command - exec plugin?
- [ ]  Provide an Installation script and plugin

#### Storing Measurements
Measurements are stored in a small H2 database which is automatically created upon the
initial startup of MANAi in the user's home directory in a special `mana` folder.

Feel free to connect to the H2 database using your favored SQL-Client application. An example
on how to connect to the database to e.g. select and export measurements for further analysis
using IntelliJ's internal database view.

## Conclusion
## FAQ
## License
## Contributing