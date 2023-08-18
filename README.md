# kahala-game

This is simple approach of Kahala game.

## Table of Contents
1. [Requirements](#requirements)
1. [Build](#build)
1. [Running](#running)

## Requirements

- java 17+
- docker
- docker-compose
- maven 3.9.3+

## Build

To build the application, first we need to generate the application jar.
```shell
$ mvn clean install
```
TThis command will generate a JAR file inside the target directory. 
Next, create the Docker image. Navigate to the root directory of the project and run the following command:
```shell
$ docker build -t kahala:latest .
```

## Running
To run the application, first start the stack, which includes a PostgreSQL image and the kahala-bol image:
```shell
$ docker-compose -f kahala-stack.yml up
```

Once the stack is up, access the application in your browser at http://localhost:8080.
We can also access the SwaggerUI at http://localhost:8080/swagger-ui.html.