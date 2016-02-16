# PURPOSE
This repository is a simple example of how a dropwizard application can be deployed to cloudfoundry. I didn't find a
comprehensive description on the internet, and it took me almost 2 days to figure everything out myself, so I figured I
would write one myself.

## Usage of this example
The application in this example has only a single resource defined, accessible on the root path of the application.
It returns some static string which is configurable.
It displays only the actions that need to be done to deploy a dropwizard application to CloudFoundry.

### Starting the app locally
To start the application locally you will need to first install it using
```bash
$> mvn clean install
```
then define a `PORT` environment variable and start the app just as you would start a regular DW application:
```bash
$> export PORT=8000
$> java -jar target/dwOnCfExample-1.0-SNAPSHOT.jar server conf/conf.yml

```
or simpler
```bash
$> env PORT=8000 java -jar target/dwOnCfExample-1.0-SNAPSHOT.jar server conf/conf.yml
```

### Deploying on CF
To deploy this app to a configured CloudFoundry environment execute the command below:
```bash
$> cf push dwOnCfExample -b "https://github.com/cloudfoundry/java-buildpack.git" -p target/dwOnCfExample-1.0-SNAPSHOT.jar
```
This will deploy the application named `dwOnCfExample`, packaged in the `target/dwOnCfExample-1.0-SNAPSHOT.jar` jar,
with the java buildpack from cloudfoundry.

## Detailed description
What needs to be done, to deploy and start the application on cloudfoundry:

### Add bundle in Application main
This marvelous add-on to dropwizard, called (dropwizard-template-config)[https://github.com/tkrille/dropwizard-template-config],
prepared by tkrille, allows dropwizard to easily receive configuration parameters from environment variables. This is
needed, as the port on which the application starts is dynamically allocated when deploying the application, and is saved
in the `$PORT` environment variable.

The only minus is that this change makes it necessary to set the `PORT` environment variable everytime you start the
application, also locally.

```java
@Override
public void initialize(final Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.addBundle(new TemplateConfigBundle());
}
```

### Create proper conf
CloudFoundry reserves a single port for each application. Because of that the server needs to set to the `simple` type,
to use the same port for both the application and admin endpoints. Additionally the default port needs to be overriden
using the environment variable which was enabled using the dropwizard-template-config. The sample below does just that.
```yml
server:
  type: simple
  applicationContextPath: /
  adminContextPath: /admin
  connector:
    type: http
    port: ${env.PORT}
```

### Change main method
The main in the `Application` class starts the application. When starting dropwizard you have to pass the starting
parameters (command and the location of the configuration file). I tried lots of ways to pass a proper startup command
to the java buildpack but I failed to get it to work. The buildpack automatically finds the main method in the application
and I couldn't pass a custom start command to it, with the proper command and config file. Thats why this change is
required: when starting the application on localhost, you will be able to override the default parameters in the start
command yourself, but on cloudfoundry we can safely work with these defaults, when none are provided (coudfoundry will
not provide any).

This obviously hardcodes that the application will look for a file named `conf.yml` during startup, without it won't start
correctly.
```java
if (args == null || args.length == 0) {
    args = new String[] { "server", ClassLoader.getSystemResource("./conf.yml").getFile() };
}
new ExampleApplication().run(args);
```

### Ensure your conf file is packaged into the jar
By specifying the configuration file as a resource in the build definition in the `pom.xml`, maven will always put the file
when packaging the application:
```xml
<build>
    <resources>
        <resource>
            <directory>
                conf
            </directory>
            <includes>
                <include>conf.yml</include>
            </includes>
        </resource>
    </resources>
</build>
```