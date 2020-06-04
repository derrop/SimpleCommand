# Simple Commands

A library to create simple commands for anything. They can be executed in the console, in a web panel or what ever you want.

## Getting started

Maven:

```xml
<repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.derrop</groupId>
    <artifactId>simplecommand</artifactId>
    <version>1.0-RELEASE</version>
</dependency>
```

Gradle:

```groovy
maven {
    name 'jitpack'
    url 'https://jitpack.io'
}

compile group: 'com.github.derrop', name: 'simplecommand', version: '1.0-RELEASE'
```
<br>

With that being done, you can now create your [DefaultCommandMap](src/main/java/com/github/derrop/simplecommand/map/DefaultCommandMap.java) to register your commands.

Your first command could like [this](src/test/java/com/github/derrop/simplecommand/ExampleCommand.java), register it in your [CommandMap](src/main/java/com/github/derrop/simplecommand/map/CommandMap.java) by using the `registerSubCommands` method.

When you have created your commands, you can dispatch them by using the `dispatchCommand` or `dispatchConsoleCommand` methods in your [CommandMap](src/main/java/com/github/derrop/simplecommand/map/CommandMap.java).