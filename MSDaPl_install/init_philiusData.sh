#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create and initialize the philiusData database
# ---------------------------------------------------------------------------------

java=$1;
javac=$2
base_dir=$3;
mysql_host=$4; # host name
mysql_user=$5; # MySQL username
mysql_passwd=$6; # MySQL password


if [ $# -lt 6 ] ; then
	mysql_passwd=""
fi

# Make sure we get all the required parameters
if [ $# -lt 5 ] ; then
	
	echo "Usage: $0 java_exe_location javac_exe_location base_dir mysql_host mysql_username [mysql_password]";
	exit 1; 
fi

schema_dir=${base_dir}/schema

mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi

database=philiusData

echo "Creating database $database"
# echo "mysql $mysql_str < $schema_dir/$database.sql"
mysql $mysql_str < $schema_dir/$database.sql

STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error create the database $database"
	exit 1;
fi
echo ""

# Initialize philiusData for all the sequences we currently have in YRC_NRSEQ
# TODO