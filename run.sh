#!/bin/bash

javac -cp "lib/*" */*.java *.java
java -cp ".:lib/*" MainPageServer
rm -f *.class */*.class
