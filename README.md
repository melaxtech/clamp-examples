# clamp-examples

This repository contains api examples of the CLAMP software (https://clamp.uth.edu/)
To debug and run, you will need to contact the CLAMP team for a valid license (http://www.melaxtech.com/)


# Build and Run
You will need access to the MelaxTech Nexus repository. Please contact us for that.

First, ~/.m2/settings.xml must be updated (if it exists) or created.
If you do not have a file, then create one with this text:
```bash
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository/>
    <interactiveMode/>
    <usePluginRegistry/>
    <offline/>
    <pluginGroups>
        <pluginGroup>org.jenkins-ci.tools</pluginGroup>
    </pluginGroups>
    <servers>
        <server>
            <id>melax-nexus-releases</id>
            <username>USERNAME</username>
            <password>PASSWORD</password>
        </server>
        <server>
            <id>melax-nexus-snapshots</id>
            <username>USERNAME</username>
            <password>PASSWORD</password>
        </server>
    </servers>
      <mirrors>
    </mirrors>

     <proxies/>
    <profiles/>
    <activeProfiles/>
</settings>
```
If you already have a settings.xml file then just add the 2 server to your servers section:
```bash
        <server>
            <id>melax-nexus-releases</id>
            <username>USERNAME</username>
            <password>PASSWORD</password>
        </server>
        <server>
            <id>melax-nexus-snapshots</id>
            <username>USERNAME</username>
            <password>PASSWORD</password>
        </server>
```

Replace USERNAME and PASSWORD accordingly.

```bash
#to build the backend package
`mvn clean package` 

#edit properties file with paths to UMLS and RxNorm folders if needed by pipeline (the included PublicClinicalPipeline.pipeline.jar doesn't need them)
edit src/main/resources/application.properties

#to run backend package
./runJar.sh
#which is just the single command
java -jar target/clamp-nlp-service.jar --spring.config.location=src/main/resources/application.properties
#You may want to run it with & so that it runs in the background

#run DemoApi by
mvn exec:java -Dexec.mainClass="edu.uth.clamp.support.api.DemoApi" -Dexec.args="json"
```


# DemoApi Options
* The first parameter should be one of html json or xmi. For html and xmi the output will be saved in a file named demoTest.html or demoTest.xmi respectively.
For json, the entities, relations, and their attributes will be printed to the screen.

* The second parameter is optional. If it begins with 'http' then it is the URL to the backend pipeline. By default this is http://localhost:8080. If that port is already occupied then the output of starting the backend will have a line like `INFO: Tomcat initialized with port(s): 8080 (http)` with what port number is being used. 

* The third parameter is either file or the text to be processed (and the rest of the parameters will be concatenated as part of the text). If file is given, then the next parameter should be the name of the file to be processed.
If no text or file is given, default text will be processed.


# Contact us for commercial use: support@melaxtech.com 
