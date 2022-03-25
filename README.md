# MANAi - An IntelliJ Plugin for Software Energy Consumption Profiling
MANAi provides ease of use for energy consumption profiling on Intel-based PCs and Macs. 
MANAi leverages *Intel's Running Average Power Limit (RAPL)* to provide feedback to developers 
from within Jetbrains Intellij IDEA development tool platform. 

Once MANAi is installed and configured correctly, one can execute unit tests in Java projects 
using the respective MANAi command runner in IDEA. Recorded samples are mapped to the respective 
program unit in the development environment and 
energy data is being presented. The following screenshot shows methods that were 
executed using MANAi. The color coding gives feedback to the developer on the energy characteristics 
of respective methods.

![](doc/fasta_code.png)

## Motivation
Understanding the energy implications of software design
choices can play a crucial role towards developing
sustainable software. MANAi helps developers, researcher 
and students alike to make energy consumption explicit 
by providing a wide range of visualisation and tools
for energy-aware development straight within an integrated 
development. This way MANAi can support in building awareness
for energy-efficient software design amongst the developer community.

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
MANAi uses _Apache Maven_ for communication between an application under test 
and the _Intellij_ plugin. In order to properly execute a MANAi test run, ensure
_Apache Maven_ is installed on your system and properly configured. MANAi processes
the `M2_HOME` environment variable, so ensure it is set and pointing 
to a valid maven installation directory. The plugin was tested with _Apache Maven 3.8.4_. 

### Installation
MANAi can either be installed and used from one of the published pre-packaged binaries,
or you can build it yourself directly from source.

#### Installation from Pre-Built Binaries
We provide a set of pre-built binaries which are ready to 
use. Just select the correct binary depending on the system
you are working with, download it and install the plugin
using Intellij's internal plugin mechanism. 

http:// ... downloadlinks

#### Installation from Source
- install maven plugin - just run mvn install
- install MANA plugin - unzip Mana Plugin in IDEA folder → maven bash script runner - exec plugin?
- install RAPL Command - exec plugin?
- Provide an installation script and plugin

```grooy
gradle downloadFile and processResources tasks
```

```xml
<insertxmlofmanainstrumentplugin>
```

### Usage

#### Project Setup and Preliminaries
- install maven plugin - just run mvn install
- install MANA plugin - unzip Mana Plugin in IDEA folder → maven bash script runner - exec plugin?
- install RAPL Command - exec plugin?
- [ ]  Provide an Installation script and plugin


```xml
how to set up a project usign manai
```
#### Storing Measurements
Measurements are stored in a small _H2 database_ which is automatically created at the
initial startup of MANAi in the user's home directory in a special `.mana` folder.

Feel free to connect to the H2 database using your favorite SQL-Client application. An example
on how to connect to the database to e.g. select and export measurements for further analysis
using IntelliJ's internal database view is provided in our Wiki system.

## Conclusion
MANAi provides an out-of-the-box solution 
to software energy profiling from within IntelliJ IDE.
We hope that MANAi helps to support in better comprehending
the energy implications of software design choices. If you are
interested in contributing to MANAi or just want to share
your experience using the plugin, feel free to contact me.

Currently, MANAi is primarily tailored to be 
used in connection with Java applications. 
However its modular design allow to further extend 
it to other languages and development environments.

## License
Copyright (c) 2020 the original author or authors. DO NOT ALTER OR REMOVE COPYRIGHT NOTICES.

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

## Contributing
We are currently bringing together a contribution guide
which will cover information on how you can contribute to MANAi.