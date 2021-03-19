# alfresco-rest-cli
A Command Line Interface for Alfresco Repository and APA using REST API.

## Description

`alfresco` command includes services coming from [Alfresco REST API](https://github.com/Alfresco/alfresco-java-sdk/tree/develop/alfresco-java-rest-api)

## Requisites

To properly build and run the project in a local environment it is required to have installed some tools.

Java 11
```
$ java -version

openjdk version "11.0.1" 2018-10-16
OpenJDK Runtime Environment 18.9 (build 11.0.1+13)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.1+13, mixed mode)
```

Maven version 3.3

```
$ mvn -version

Apache Maven 3.6.1 (d66c9c0b3152b2e69ee9bac180bb8fcc8e6af555; 2019-04-04T21:00:29+02:00)
```

## Build and run

```
$ mvn clean package

$ java -jar target/java -jar target/alfresco-rest-cli-1.0.0.jar
```

An additional `alfresco` program is generated in `target` folder in order to provide easier access. So alternatively you can run the folowing lines:

```
$ chmod +x target/alfresco

$ target/alfresco
```

## Configuration

```
$ target/alfresco config

Commands:
  acs  Set configuration for ACS.
  apa  Set configuration for APA.
```

Configuration for ACS and APA can be stored locally in your home folder in order to use different servers.

For instance, to configure an ACS server following command may be used.

```
$ target/alfresco config acs http://localhost:8080 admin admin
```

## ACS commands

Following ACS Commands are available.

```
$ target/alfresco acs
Usage: alfresco acs [-hV] [COMMAND]
Commands:
  node    Node commands
  person  Person commands
  group   Group commands
  search  Search commands
  site    Site commands
```

Detailed information on every command can be obtained by typing the command name.

```
$ target/alfresco acs node
Usage: alfresco acs node [-hV] [COMMAND]
Commands:
  list
  update
  create
  get
  delete
```

## AGS commands

Available commands.

```
$ target/alfresco ags
Usage: alfresco ags [-hV] [COMMAND]
Commands:
  securityMark  Security Marks commands
```

## APA commands

Available commands.

```
$ target/alfresco apa
Usage: alfresco apa [-hV] [-f=<format>] [COMMAND]
Commands:
  startProcess  Start process
```

## Generic parameters

```
Configuration commands
  -f, --format=<format>   Output format. E.g.: 'default', 'json' or 'id'.
                            Default: default
  -h, --help              Show this help message and exit.
  -V, --version           Print version information and exit.
```

In addition to `help` and `version` options, ouput formatting can be selected by using the `-f` flag in every command.

By default formatting is tabular text.

```
$ target/alfresco acs node list
----------------------------------------------------------------------------------------------------------------------
ID                                       NAME                           MODIFIED AT               USER
----------------------------------------------------------------------------------------------------------------------
0913c13d-1a34-41d8-90c9-5ebd8617b2de     Data Dictionary                2021-03-18T08:13:20.552Z  System
f5e7fbae-f5a5-416b-97a5-3bef3bd483d3     Guest Home                     2021-03-18T08:12:56.127Z  System
631d7461-ee01-4dcf-979f-02b4c4afd0e8     Imap Attachments               2021-03-18T08:12:56.243Z  System
6d81b1bc-dfa9-4875-8053-ce7bf5a7d889     IMAP Home                      2021-03-18T08:12:56.265Z  System
93d41181-9660-45b3-b6e1-82018289ad7c     Shared                         2021-03-18T14:13:25.359Z  Administrator
f1990e51-e36e-46b9-b667-18947ccb7487     Sites                          2021-03-18T08:13:16.712Z  System
267e5175-cd79-4ed6-8877-3f23011051c5     User Homes                     2021-03-18T08:24:46.156Z  Administrator
----------------------------------------------------------------------------------------------------------------------
```

In order to get raw JSON Response, following parameter can be used.

```
$ target/alfresco acs node list -f json
{
  "pagination" : {
    "count" : 7,
    "hasMoreItems" : false,
    "totalItems" : 7,
    "skipCount" : 0,
    "maxItems" : 100
  },
  "entries" : [ {
    "entry" : {
      "id" : "0913c13d-1a34-41d8-90c9-5ebd8617b2de",
      "name" : "Data Dictionary",
      "nodeType" : "cm:folder",
      "isFolder" : true,
      "isFile" : false,
      "isLocked" : false,
...
}
```

And finally, a list of IDs can be obtained using this option.

```
$ target/alfresco acs node list -f id
0913c13d-1a34-41d8-90c9-5ebd8617b2de, f5e7fbae-f5a5-416b-97a5-3bef3bd483d3, 631d7461-ee01-4dcf-979f-02b4c4afd0e8, 6d81b1bc-dfa9-4875-8053-ce7bf5a7d889, 93d41181-9660-45b3-b6e1-82018289ad7c, f1990e51-e36e-46b9-b667-18947ccb7487, 267e5175-cd79-4ed6-8877-3f23011051c
```
