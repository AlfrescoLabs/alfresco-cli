# alfresco-event-cli
A Command Line Interface for Alfresco Repository using Event API.

## Description

`alfresco-stream` command includes services coming from [Alfresco Event API](https://github.com/Alfresco/alfresco-java-sdk/tree/develop/alfresco-java-event-api)

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

$ java -jar target/java -jar target/alfresco-event-cli-1.0.0.jar
```

An additional `alfresco-stream` program is generated in `target` folder in order to provide easier access. So alternatively you can run the folowing lines:

```
$ chmod +x target/alfresco-stream

$ target/alfresco-stream
```

## Configuration

```
$ target/alfresco-stream config

Commands:
  acs       Set configuration for ACS.
  activemq  Set configuration for ActiveMQ.
```

Configuration for ACS and ActiveMQ can be stored locally in your home folder in order to use different servers.

For instance, to configure an ACS server following command may be used.

```
$ target/alfresco-stream config acs http://localhost:8080 admin admin
```

## Watch folder command

Following command is available

```
$ target/alfresco-stream watch folder <folder>
Usage: alfresco watch folder [-hV] [-t=<eventType>] <folder>
      <folder>    The id or relative path of the node to be watched.
                    Default: /
  -t, --event-type=<eventType>
                  Type of event. E.g: NODE_CREATED, NODE_UPDATED, NODE_DELETED
```

Once the command is started, the program keeps listening to the folder specified.

Events are logged in the output.

```
$ target/alfresco-stream watch folder "/Shared/watched"
---------------------------------------------------------------------------------------------------------------------------------
EVENT TYPE           ID                                       NAME                MODIFIED AT                      USER
---------------------------------------------------------------------------------------------------------------------------------
NODE_CREATED         79b79553-52b2-4806-99a2-997a29e71e87     watch-folder.sh     2021-03-19T10:49:30.398Z[UTC]    Administrator
NODE_UPDATED         79b79553-52b2-4806-99a2-997a29e71e87     watch-folder.sh     2021-03-19T10:49:45.693Z[UTC]    Administrator
NODE_DELETED         79b79553-52b2-4806-99a2-997a29e71e87     watch-folder.sh     2021-03-19T10:49:45.693Z[UTC]    Administrator
```

Currently no additional options are available.
