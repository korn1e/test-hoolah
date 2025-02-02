
# Hoolah - Coding Challenge


----
## Build Requirement:

Before compile, make sure following is installed in your build machine:

- Java JDK version 8 or later
- Maven 3

----
## Compile and Build

Open command line tool, go to working directory, and execute following command:

```bash
mvn clean package
```

The package bundle file named `test-hoolah.jar` will be created in `target` directory.

----
#### Configuration File: 
By default, when running the application, file `application.properties` is not required.
Thus, it will use internal file.  

However, you can override parameter by manually create configuration file with name `application.properties`,
and put it in the working directory or same location as the .jar file.   

Following parameter is needed:
```bash
data.file.path=/path/to/data.csv
output.file.path=/path/to/output.csv
```
----
#### Data File: 
Make sure you provide data file which you defined in `application.properties` file.

----
## Run the Application

Open command line tool, go to working directory, and execute following command:

```bash
java -jar target/test-hoolah.jar
```
Above will generate error message and guide how to provide required arguments.

Example correct command:
```bash
java -jar target/test-hoolah.jar "20/08/2018 12:00:00" "20/08/2018 13:00:00" "Kwik-E-Mart"
```
The output in console and file (according to `application.properties`) will be generated.





