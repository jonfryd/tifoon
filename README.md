[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Tifoon: Open Network Ports Monitoring

This is an attempt to create an application in Java which can effectively monitor open ports in
networks of host machines/devices by comparing consecutive scans against a known "good" baseline.

Open services/ports is a significant security threat, which is why monitoring what ports are open and
exposed makes it easier to manage this risk and stay alert if something change. This could potentially
indicate that a host has been compromised by a trojan/worm, although there could be many benign reasons
for such an event, of course.

Currently Tifoon relies on the world class `nmap` port scanner from which it reads the output, as well
as a diff algorithm using [JaVers](http://javers.org/) for determining changes to open ports reported
in a concise, easy to read manner.

Tifoon is fully functional in its present state, however, additional features and convenience is coming
soon.

# Building

JDK 8 and Maven 3 is required to build Tifoon from command line.

Clone the repository and execute Maven from the root directory:

    $ git clone https://github.com/jonfryd/tifoon
    $ cd tifoon/
    $ mvn clean install

This will build all required modules, installs them in the local Maven repository and create a ZIP
file in the `tifoon-app/target` subdirectory for distribution. If desired, copy the distro to
another directory elsewhere on your system and extract it there.

# Usage

Ensure you have some flavor of Java 8 Runtime Environment installed before proceeding. Oracle's JRE and
OpenJDK have been tested on Windows, Linux and Mac OS X.

Also, one of the following is a prerequisite to perform any port scanning:

1. Local install of nmap
2. A working local Docker installation (Tifoon will pull and use an nmap container image automatically)

From command line, Tifoon can be extracted from the ZIP archive and launched via three simple steps:

    $ unzip tifoon-app-0.5-dist.zip
    $ cd tifoon-app-0.5-dist/
    $ java -jar tifoon-app-0.5-SNAPSHOT.jar

With its "factory settings" the local host (IP address 127.0.0.1) is a complete TCP port scan is scheduled
for every hour. For the second and later scans, the result is automatically checked ("diffed") against
the initial scan for any changes compared to the baseline and the specific changes are reported.

This behaviour can, of course, be adjusted to include any number of networks and hosts as described in
the configuration section below.

Scans and diffs are saved to the `scans/` folder (gets created automatically when needed). YAML output
is the current default, but JSON is supported, as well.

A log file `tifoon.log` is maintained, as well, which contains all standard output produced by Tifoon
for auditing and debugging purposes.

Tifoon runs forever until stopped (CTRL + C) or killed. It might be a good idea to launch Tifoon within
a Linux/UNIX `screen` so it runs in the background in a way that is detached from your terminal.

It is also possible to run the application directly with Maven's Exec plugin. From the root of the cloned
GIT project:

    $ cd tifoon-app/
    $ mvn exec:java

## Configuration

Three configuration files are used to define the behaviour of Tifoon. All files are in YAML format and
should be easy to modify with any text editor. These files are loaded once and for all startup. Changes
while the application is running are not detected.

### `config/application.yml`

Defines the behaviour of the application. The config file includes a comment at the end of each
property line which briefly explains the purpose of each option. The output format can be set to
either YAML or JSON, nmap can be executed by either local process or Docker, and a number of
options controls how Tifoon deals with the baseline, like whether it is created on the initial scan
or loaded from a previous scan file.

This is a Spring Boot application which means that Tifoon inherits a bunch of [customisation options](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).
One example is related to logging and you will see a few properties related to logging already exposed,
namely the name of the log file and log levels for various packages.

### `config/network.yml`

This is list of networks and hosts to be monitored. Each network consists of an arbitrary number of hosts,
and the set of ports to be scanned for every host in this network. Example:

      ports:
        - 20-25
        - 153
        - 900-999

Results in all of TCP ports 20 to 25, 153 and 900 to 999 being scanned.

If a hostname (and not a IPv4 address) is provided for any host, the IPv4 address is resolved on startup
by DNS lookup on startup (resolution is final and not redone on consecutive scan).

Of course, all hosts could technically speaking be on the same physical network but grouped into
logical networks.

Ranges of hosts in CIDR or IP interval notation can not be specified, yet. Also, TCP is the only
protocol supported as of this moment.

### `config/docker.yml`

This config file is only used when the docker command executor enabled. It specifies how commands
for scanner plugins (currently only `nmap` is supported) are mapped to Docker containers. A default
mapping specifies a fallback container image to be used if no mapping is found in the `customImages`
list.

# Design

Tifoon is based on an open source technologies, domain-driven design, a flexible core and designed with
extendability in mind by programming against abstractions. Plugins for I/O, scanning and command
execution are loaded and registered on startup from JAR files in the `plugins` subdirectory.

## 3rd party libraries used

Tifoon stands on the shoulders of giants. The key libraries used are:

* [Spring Boot](https://projects.spring.io/spring-boot/)
* [Spring Plugin](https://github.com/spring-projects/spring-plugin)
* [JaVers](http://javers.org/)
* [Guava](https://github.com/google/guava)
* [Lombok](https://projectlombok.org/)
* [nmap4j](https://sourceforge.net/projects/nmap4j/)

Check the `pom.xml` files for an exhaustive list.

# Acknowledgements

Thanks to the open source community for sharing their work with the world. More power to you guys!

Also, big props to JetBrains for making the wonderful IntelliJ IDEA Community available to
developers for free, making Java coding productive and a lot of fun.

# TODO

Tifoon is still in its infancy, but I have several ideas for how this baby can grow in the future:

* Support for specifying ranges of hosts
* Define pre-defined sets of "top ports" for fast scanning of the most critical services
* UDP and SCTP protocols scanning
* Add "convenience launchers" for common operating systems
* IPv6 support
* Banner grabbing and OS detection
* Optionally save scans and diffs to a database instead of as local files (JPA mapping is done already)
* Report when the input network configuration has changed (via a hash)
* Add the option of defining sets of ports which can be easily referred to in scan targets
* Alternative scanner plugins, e.g. Robert David Graham's [masscan](https://github.com/robertdavidgraham/masscan) looks like an excellent addition
* REST web application
* A proper frontend (AngularJS)
* PDF/HTML reporting (DynamicReports/JasperReports)

# How to contribute

All contributions are greatly appreciated; i.e. bug reports, feature suggestions, grammar corrections,
encouragements, whatever.

If you are a developer you are more than welcome to tag along for the ride by creating pull-requests,
but please keep these common sense coding guidelines in mind:

* Clean, maintainable and readable code, please
* Embrace the beauty of simplicity in design
* Think in terms of generic solutions
* Try to apply well-known design principles and patterns where applicable
* Write testable code and unit tests for critical functionality

We want to ensure well-crafted software, which is robust, relies on reasonable defaults and behaves
in ways that are "unsurprising" to the general audience.

# Author

This application created by Jon Frydensbjerg - email: jonf@elixlogic.com
