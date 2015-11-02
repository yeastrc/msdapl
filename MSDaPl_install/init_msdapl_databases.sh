#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create and initialize the following databases:
# 1. msData (this database stores scans, search results etc.)
# 2. mainDb (this database stores project, user information etc.)
# 3. jobQueue (this database stores information related to upload jobs)
# ---------------------------------------------------------------------------------

# source the properties
. config.properties


base_dir="."

mysql_host=$db_host; # host name
mysql_user=$db_user; # MySQL username
mysql_passwd=$db_passwd; # MySQL password


mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"


schema_dir=$base_dir/schema
mysqldump_dir=$base_dir/mysqldump

databases=(msData mainDb jobQueue)
# echo ${databases[@]}

for database in "${databases[@]}"
do
	echo "Creating database $database"
	# echo "mysql $mysql_str < $schema_dir/$database.sql"
	mysql $mysql_str < $schema_dir/$database.sql

	STATUS=$?
	if [ $STATUS -gt 0 ] ; then
		echo "There was an error in creating the database $database"
		exit 1;
	fi
	
	if [ $database = 'msData' ] ; then
		echo "creating msData summary tables"
		mysql $mysql_str  $database < $schema_dir/msData_summary_tables.sql
		
		STATUS=$?
		if [ $STATUS -gt 0 ] ; then
			echo "There was an error in creating msData summary tables"
			exit 1;
		fi
	fi
	
	if [ $database = 'mainDb' ] ; then

		if [ -f $mysqldump_dir/NCBI_Taxonomy.sql.gz ] ; then
                	gunzip $mysqldump_dir/NCBI_Taxonomy.sql.gz
        	fi

	
		echo "creating NCBI_Taxonomy table in mainDb"
		mysql $mysql_str  $database < $mysqldump_dir/NCBI_Taxonomy.sql
		
		STATUS=$?
		if [ $STATUS -gt 0 ] ; then
			echo "There was an error in creating NCBI_Taxonomy table in mainDb"
			exit 1;
		fi
		
	fi
	
	echo ""
	
done

# Initialize the mainDb database
# Create "administrators" group and one group for the lab
# Create one admin user and one normal user
java_dir="$base_dir/java"
echo "Initializing mainDb database"
cd $java_dir
$javac MainDbInitializer.java
cd ../
echo "$java -classpath .:.$java_dir:$java_dir/lib/mysql-connector-java-5.1.6-bin.jar MainDbInitializer"
$java -classpath .:$java_dir:$java_dir/lib/mysql-connector-java-5.1.6-bin.jar MainDbInitializer

exit 0
