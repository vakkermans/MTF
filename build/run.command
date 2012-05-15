#!/bin/sh
cd `dirname $0`
java -d64 -Djava.library.path=../data/opengl/macosx -jar mtf.jar

