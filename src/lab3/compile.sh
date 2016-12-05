#!/bin/bash

javac -cp jacop-4.4.0.jar VariableSelect.java
javac -cp '.;jacop-4.4.0.jar' Golomb.java
java -cp '.;jacop-4.4.0.jar' Golomb