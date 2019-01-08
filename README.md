leila-csv
==========

*leila-csv* is a demo plugin for the tool [leila](https://github.com/raymanrt/leila).

*leila-csv* demonstrates simply how can be implemented a custom formatting plugin for *leila*.

*leila-csv* serialize each *lucene* Document extracted with *leila* into a standard csv.

It consists of a single class `CsvFormatter` whith just one constructor.

The class ha just one public method, which receives a Document and returns a String, which is always empty
meaning that leila won't print anything. This is expected, since the class prints directly to stdout.

Dependencies:
* commons-csv
* lucene-core 4.10.4

This software is open-source, released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0),
and made without any commercial intent. Please feel free to contribute, especially if you find *leila* usefull and
discover some bug or missing feature.
The non-exhaustive feature request list is at the bottom of this page.

Build from source
-----------------

*leila-csv* source can be downloaded from this repository and can be build with maven. The requirements are a JVM compatible
with Java 1.8 version.

```
git clone https://github.com/raymanrt/leila-csv.git
cd leila-csv
mvn clean package
ls target/leila-csv.jar
```

A portable fat jar is build which includes every dependency required to use leila-csv in bundle with leila.

Using leila-csv
----------------

To make leila-csv visible to leila you should run the leila Main with the java command, not forgetting to include the
generated jar in your classpath, for example:
```
java -cp 'target/leila.jar:../leila-csv/target/leila-csv.jar' com.github.raymanrt.leila.Main \
    target/demo-index \
    -p com.github.raymanrt.leila.csv.CsvFormatter
```

Please remember the syntax for the `-p` option:
* the first argument is the path of the desired class
* the following arguments are the builder parameters

Limits and desired functions
----------------------------

* *leila-csv* considers each document as having only String values (or list of String values);
with more configuration it should be easy make it cast one or more fields to some custom type;
also some kind of type inference could be considered