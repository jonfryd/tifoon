# Tifoon: Port Scanner Diff Application

<The vision>

Tested on Windows, Linux and Mac OS X.

# Building

JDK 8 and Maven 3 is required to build from command line.

Execute Maven from the root directory:

    $ cd tifoon/
    $ mvn clean install

This will build all required modules, installs them in the local repository and create a ZIP file 
in the `tifoon-app/target` subdirectory for distribution.

# Usage

For Linux or Mac OS X from command line, Tifoon can be extracted from the ZIP archive and launched 
via three simple steps:

    $ unzip tifoon-app-1.0-dist.zip
    $ cd tifoon-app-1.0-dist/    
    $ java -jar tifoon-app-1.0-SNAPSHOT.jar

With its "factory settings" the local host (IP 127.0.0.1) is scanned every hour. For the second
and later scans, the result is checked ("diffed") against the initial scan for changes. This behaviour 
can be adjusted to include any number of networks and hosts as described in the configuration section
below.

 asdfjifjiasj saf jsaijf.
 
 
Scans and diffs are saved to the `scans/` folder (gets created automatically when needed).

Note that currently it is necessary to have one of the following is prerequisite to do any scanning:

* Local install of nmap
* A working local Docker installation (Tifoon will pull and use an nmap container image automatically)

## Configuration

Three configuration files are used to change the behaviour of Tifoon. All files are in YAML format and
should be easy to modify with any text editor. These files are loaded once and for all startup. Changes
while the application is running are not detected.

### `config/application.yml`

Defines the 

### `config/network.yml`

Hosts grouped into logical networks.

### `config/docker.yml`

Commands mapped to Docker containers.

# Design

Tifoon is based on an open source technologies, domain-driven design, a flexible core and designed with 
extendability in mind by programming against abstractions. 

## 3rd party libraries used

Some key components are:

* Spring Boot
* Spring Plugin
* JaVers
* Guava
* Lombok
* nmap4j

Check the `pom.xml` files for an exhaustive list.

# Acknowledgements

Thanks to the open source community for sharing their work with the world. You guys rock!

Also, big props to JetBrains for making the wonderful IntelliJ IDEA Community available to 
the community for free, making Java coding productive and a lot of fun.

# TODO

Tifoon is still in its infancy, but I have several ideas for how this baby can grow in the future. 
Ease of use, generating value for users, maintainability and solid design is important. For example... 

* Add "convenience launchers" for common operating systems
* UDP and SCTP protocols scanning
* IPv6 support
* Banner grabbing and OS detection
* Optionally save scans and diffs to a database instead of as local files (JPA mapping is done already)
* Report when the input network configuration has changed (via a hash)
* Add the option of defining sets of ports which can be easily referred to in scan targets
* Alternative scanner plugins, e.g. Robert David Graham's masscan looks like an excellent addition
* REST web application
* A proper frontend
* PDF/HTML reporting

# How to contribute

* Please read the coding guidelines first!
* Think in terms of generic principles
* Clean, maintainable and readable code, please
* Create a pull-request

# Author

This tool was created by Jon Frydensbjerg - email: jonf@elixlogic.com