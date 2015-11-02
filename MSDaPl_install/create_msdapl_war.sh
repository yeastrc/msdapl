#!/bin/sh

# ---------------------------------------------------------------------------------
# A shell script to create a deployable war archive for msdapl
# ---------------------------------------------------------------------------------

# source the properties
. config.properties

host=$db_host
db_user=$app_db_user
db_passwd=$app_db_passwd

# uncompress the msdapl source
echo "Uncompressing msdapl"
cd ./msdapl
tar -xvf msdapl.tar.gz
cd msdapl

# create a properties file
# Write msdapl_db.properties file
mv msdapl_db.properties  msdapl_db.properties.example

echo "Creating properties file msdapl_db.properties"

echo "# Global information; will be used for all databases" > msdapl_db.properties
echo "# unless it is overridden by individual database properties" >> msdapl_db.properties
echo "all.host=$host" >> msdapl_db.properties
echo "all.user=$db_user" >> msdapl_db.properties
echo "all.password=$db_passwd" >> msdapl_db.properties
echo "" >> msdapl_db.properties
echo "db.msData.dbname=msData" >> msdapl_db.properties
echo "db.jobQueue.dbname=jobQueue" >> msdapl_db.properties
echo "db.mainDb.dbname=mainDb" >> msdapl_db.properties
echo "db.mainDb.jndiname=yrc" >> msdapl_db.properties
echo "db.YRC_NRSEQ.dbname=YRC_NRSEQ" >> msdapl_db.properties
echo "db.YRC_NRSEQ.jndiname=nrseq" >> msdapl_db.properties
echo "db.sgd.dbname=sgd" >> msdapl_db.properties
echo "db.sgd.jndiname=sgd" >> msdapl_db.properties
echo "db.go.dbname=mygo" >> msdapl_db.properties
echo "db.go.jndiname=go" >> msdapl_db.properties
echo "db.wormbase.dbname=wormbase" >> msdapl_db.properties
echo "db.wormbase.jndiname=wormbase" >> msdapl_db.properties
echo "db.flybase.dbname=flybase" >> msdapl_db.properties
echo "db.flybase.jndiname=flybase" >> msdapl_db.properties
echo "db.hgnc.dbname=hgnc" >> msdapl_db.properties
echo "db.hgnc.jndiname=hgnc" >> msdapl_db.properties
echo "db.philiusData.dbname=philiusData" >> msdapl_db.properties
echo "db.go_human.dbname=go_human" >> msdapl_db.properties


# create a context.xml file
echo "Writing context.xml file"
$ant -f build_context.xml
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "Error writing context.xml"
	exit 1;
fi

# create msdapl.war
echo "Building msdapl.war"
$ant -f build_war.xml
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "Error creating msdapl.war"
	exit 1;
fi
mv msdapl.war ../

exit 0