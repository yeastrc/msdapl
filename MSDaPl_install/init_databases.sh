#!/bin/sh

# ---------------------------------------------------------------------------------
# A shell script to create and initialize the following databases:
# 1. YRC_NRSEQ (a non-redundant protein sequence database)
# 2. mygo (gene ontology database)
# ---------------------------------------------------------------------------------

# source the properties
. config.properties

echo "MySQL host: $db_host"
echo "MySQL user: $db_user"

# if [ "$use_db_passwd" -gt 0 ] ; then
if [ "$db_passwd" != "" ] ; then
	
	echo Using MySQL password: YES
else

	echo Using MySQL password: NO
fi

base_dir=`pwd`

# Initialize species specific databases
echo "init_biodb.sh $java $base_dir $db_host $db_user "
sh init_biodb.sh $java $base_dir $db_host $db_user $db_passwd
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error initializing databases"
	exit 1;
fi
echo ""

# Initialize YRC_NRSEQ
echo "sh init_yrc_nrseq.sh $java $base_dir $db_host $db_user"
# sh init_yrc_nrseq.sh $java $base_dir $db_host $db_user $db_passwd 
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error initializing YRC_NRSEQ database"
	exit 1;
fi
echo ""


# Initialize GO database
# echo "sh init_mygo.sh $java $base_dir $db_host $db_user"
# sh init_mygo.sh $java $base_dir $db_host $db_user $db_passwd 

# Initialize the philiusData database
# sh init_philiusData.sh $java $javac $base_dir $db_host $db_user $db_passwd

# Create the MSDaPl databases
# sh init_msdapl_databases.sh $java $javac $base_dir $db_host $db_user $db_passwd 


exit 0