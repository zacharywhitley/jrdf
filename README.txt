JRDF Library
------------

Introduction
============
JRDF is a library for parsing, storing and manipulating RDF.

Directory Layout
================
/        ant build, licence files, TODO, CONTRIBUTORS
/conf    Spring wiring files, checkstyle, IntelliJ config jar, logging
/doc     Copy of the JRDF web site
/lib     All libraries required to build and run JRDF
/src     Java, Groovy, and SableCC grammar
/test    Java, Groovy, RDF for tests

Building
========
The source distribution contains a version of Ant and Windows and Unix shell
scripts to run all the tests and produce the build artefacts (JARs)

Windows Users:
> build.bat

Unix Users:
$ ./build.sh

This will produce a /build directory.

See RELEASE-NOTES for changes between each version.

-Andrew Newman