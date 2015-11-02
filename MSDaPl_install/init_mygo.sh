#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to initialize the mygo database
# ---------------------------------------------------------------------------------

java=$1;
base_dir=$2;
mysql_host=$3; # host name
mysql_user=$4; # MySQL username
mysql_passwd=$5; # MySQL password


if [ $# -lt 5 ] ; then
	mysql_passwd=""
fi

# Make sure we get all the required parameters
if [ $# -lt 4 ] ; then
	
	echo "Usage: $0 java_exe_location base_dir mysql_host mysql_username [mysql_password]";
	exit 1; 
fi

go_dir=${base_dir}/biodb/go/
schema_dir=${base_dir}/schema
java_dir=${base_dir}/java
log_dir=${base_dir}/logs

mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"


# 
# Create mygo database
# 
# echo "Creating mygo"
# echo "mysql $mysql_str -e 'DROP DATABASE IF EXISTS mygo'"
# mysql $mysql_str -e 'DROP DATABASE IF EXISTS mygo'
# echo "$mysql_str -e 'CREATE DATABASE mygo'"
# mysql $mysql_str -e 'CREATE DATABASE mygo'


STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error create mygo database"
	exit 1;
fi
echo ""

# create all the tables 
# cd $go_dir/go_assocdb-tables
# echo "cat *.sql | mysql $mysql_str mygo"
# cat *.sql | mysql $mysql_str mygo
# echo "mysqlimport $mysql_str -L mygo *.txt"
# mysqlimport $mysql_str -L mygo *.txt
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error populating mygo database (assocdb tables)"
	exit 1;
fi
echo ""

# create the tables that are populated only in seqdb
# cd $go_dir/go_seqdb-tables
# echo "mysqlimport $mysql_str -L mygo seq.txt gene_product_seq.txt seq_dbxref.txt"
# mysqlimport $mysql_str -L mygo seq.txt gene_product_seq.txt seq_dbxref.txt
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error populating mygo database (seqdb tables)"
	exit 1;
fi
echo ""


# create the additional tables we need in mygo
echo "Creating additional tables"
echo "mysql $mysql_str < $schema_dir/mygo_mytables.sql"
mysql $mysql_str < $schema_dir/mygo_mytables.sql
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating additional tables in the mygo database"
	exit 1;
fi
echo ""


#
# CREATE THE PROPERTIES FILE
#
sh create_yrc_biodb_apps_db_properties.sh $base_dir $mysql_host $mysql_user $mysql_passwd
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating file $props_file"
        exit 1;
fi


# build the YRCMAP table
logfile="$log_dir/mygo_builder.log"
classpath="./:java/yrc_biodb_builders.jar:java/commons-lang-2.6.jar:java/mysql-connector-java-5.1.6-bin.jar:java/log4j-1.2.15.jar"

echo "$java -classpath $classpath org.yeastrc.go.GOYRCMapBuilder  > $logfile"
$java -classpath $classpath org.yeastrc.go.GOYRCMapBuilder  > $logfile

STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error in populating additional tables in the mygo database"
	exit 1;
fi

echo ""

exit 0

