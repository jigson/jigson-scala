#!/bin/sh

for test_case in ./suite/*.jig; do
	tmp=`mktemp`
	scala -classpath ../target/jigson-1.0-SNAPSHOT-jar-with-dependencies.jar org.jigson.convert.JIGtoJSON $test_case > $tmp
	./json-comp/index.js $test_case $tmp ./json-comp/package.json
	if [ $? -ne 0 ] ; then 
	echo "-------------- generated file ----------------"
	cat $tmp
	echo "-----------------------------------------------"
	fi
done
