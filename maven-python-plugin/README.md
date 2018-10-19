# Maven python plugin

This maven plugin allows to run test written using pytest,
build a python egg distribution and install the package locally.

## Prerequisites

* Maven 3
* Java 1.8
* Python 2.7 or 3.X
* pip
* setup.py in your python source directory
* __optional__: [pytest](https://docs.pytest.org/en/latest/usage.html), if you want to run your python test with maven.
* __optional__: [mypy](http://mypy-lang.org/), if you want to run optional static typing analysis.

## Get plugin from sqooba's Artifactory

Add the plugin repository to your `pom.xml` or `settings.xml`. For example in your pom.xml:

    <pluginRepositories>
        <pluginRepository>
            <id>maven.sqooba.io</id>
            <name>sqooba</name>
            <url>https://maven.sqooba.io</url>
        </pluginRepository>
    </pluginRepositories>

Make sure that have the rights to read from this maven repository.

## Build and install manually

    mvn clean install
    cp ./maven-python-plugin/target/maven-python-plugin-1.1.0-SNAPSHOT.jar ~/.m2/repository/io/sqooba/maven-python-plugin/1.1.0-SNAPSHOT/

## Use in your pom.xml

    <plugin>
        <groupId>io.sqooba</groupId>
        <artifactId>maven-python-plugin</artifactId>
        <version>1.1.0</version>
        <executions>
            <execution>
                <goals>
                    <goal>test</goal>
                    <goal>package</goal>
                    <goal>install</goal>
                </goals>
            </execution>
        </executions>
    </plugin>

## Build your python code with maven

    mvn test

to run your python test. __Please note that only tests written using [pytest](https://docs.pytest.org/en/latest/usage.html) are supported for now.__

    mvn package

to build an egg distribution of your python package in `./target/py/`

    mvn install

to install the python package in `site-packages` using pip

    mvn compile

to run optional static typing analysis using [mypy](http://mypy-lang.org/). __Please note that only mypy is supported for now.__

## Versioning

If no `version` attribute or if the placeholder `version = ${VERSION}` is found in `setup.py`,
the project version present in the `pom.xml` will be used as python package version.

If a `<packageVersion>` configuration is specified in the `pom.xml` it will override
the maven project version above.

If a version number is specified in the setup.py it will override both options above.


## Configuration and defaults

`test` phase:
* Python interpreter (to run pytest): `<pythonExecutable>python</pythonExecutable>`
* Test directory: `<testDirectory>${project.basedir}/src/main/python/tests</testDirectory>`
* Extra pytest parameters (logging, code coverage, etc...): `<extraParams>"-v -s"</extraParams>`

`package` phase:

* Python interpreter (to build the dist): `<pythonExecutable>python</pythonExecutable>`
* Python source directory: `<sourceDirectory>${project.basedir}/src/main/python</sourceDirectory>`
* Python package version: `<packageVersion>${project.version}</packageVersion>`
* Python package build (possible values: `sdist`, `bdist_egg`, `bdist_wheel`): `<pythonBuild>sdist</pythonBuild>`

`compile` phase:

* Python interpreter: `<pythonExecutable>python</pythonExecutable>`
* Python source directory: `<sourceDirectory>${project.basedir}/src/main/python</sourceDirectory>`
* Extra mypy parameters: `<extraParams>--ignore-missing-imports</extraParams>`


`install` phase:

* Pip executable (to install the package): `<pipExecutable>pip</pipExecutable>`
