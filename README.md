# alfresco-cli
A Command Line Interface for ACS, AGS and APA.

## Description

This project provides command line programs to provide a scripting capabilities for [Alfresco Java SDK](https://github.com/alfresco/alfresco-java-sdk).

* `alfresco` command includes services coming from [Alfresco REST API](https://github.com/Alfresco/alfresco-java-sdk/tree/develop/alfresco-java-rest-api). Source code is available in [alfresco-rest-cli](alfresco-rest-cli) folder.
* `alfresco-stream` command includes services coming from [Alfresco Event API](https://github.com/Alfresco/alfresco-java-sdk/tree/develop/alfresco-java-event-api). Source code is available in [alfresco-event-cli](alfresco-event-cli) folder.

## Requirements

Java 11
```
$ java -version

openjdk version "11.0.1" 2018-10-16
OpenJDK Runtime Environment 18.9 (build 11.0.1+13)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.1+13, mixed mode)
```

## Usage

Once installed both programs can be executed in order to get available options.

```
$ alfresco
Usage: alfresco [-hV] [COMMAND]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  acs     Alfresco Content Services commands
  ags     Alfresco Governance Services commands
  apa     Alfresco Process Automation commands
  config  Configuration commands
```

```
$ alfresco-stream
Usage: alfresco [-hV] [COMMAND]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  config  Configuration commands
  watch  Alfresco Content Services commands
```
