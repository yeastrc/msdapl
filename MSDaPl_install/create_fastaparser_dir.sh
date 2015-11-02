#!/bin/sh

# ---------------------------------------------------------------------------------
# A shell script to create a directory for fasta parser program and copy
# all the required file
# ---------------------------------------------------------------------------------

# source the properties
# Don't use shell-hostile constructs in the shell-readable properties file!
. config.properties

# create a directory
fastaparserdir="fasta_parser"
echo "creating $fastaparserdir"
mkdir $fastaparserdir
echo "creating $fastaparserdir/lib"
mkdir $fastaparserdir/lib

# copy the required jars
echo "copying required jars"
cp java/fastaparser.jar $fastaparserdir
cp java/lib/mysql-connector-java-5.1.6-bin.jar $fastaparserdir/lib
cp java/lib/yrc_r1876.jar $fastaparserdir/lib
cp java/lib/xercesImpl.jar $fastaparserdir/lib
cp java/lib/commons-dbcp-1.2.2.jar $fastaparserdir/lib
cp java/lib/commons-pool-1.4.jar $fastaparserdir/lib
cp java/lib/commons-collections-3.2.1.jar $fastaparserdir/lib
cp java/lib/catalina.jar $fastaparserdir/lib
cp java/lib/tomcat-juli.jar $fastaparserdir/lib
cp java/lib/log4j-1.2.15.jar $fastaparserdir/lib
cp java/lib/jargs.jar $fastaparserdir/lib

# copy log4j.properties file
echo "copying log4j.properties file"
cp log4j.properties $fastaparserdir

#
# CREATE THE PROPERTIES FILE
#
sh create_fasta_parser_properties.sh $fastaparserdir
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating properties file"
        exit 1;
fi

