#!/bin/sh

set -e

this=`readlink -f $0`
dir=`dirname $this`
pom_list=`find $dir -name pom.xml`
echo "Using directory : [$this]"
echo "Formatting files :"

for pom in $pom_list; do
    echo "    [$pom]"
    xmllint --format --noblanks --output $dir/pom.xml.tmp $pom
    mv $dir/pom.xml.tmp $pom
done
echo "Done."
