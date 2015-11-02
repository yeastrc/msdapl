#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create the following databases from mysqldumps:
# 1. NCBI
# 2. sgd
# 3. wormbase
# 4. flybase
# 5. SangerPombe
# 6. cgd
# 7. hgnc
# 8. go_human
# 9. philiusData
# 10. YRC_NRSEQ
# 11. mygo
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


mysqldumpdir=$base_dir/mysqldump

databases=(NCBI sgd wormbase flybase SangerPombe cgd hgnc go_human philiusData YRC_NRSEQ mygo)
# echo ${databases[@]}

for database in "${databases[@]}"
do
	echo "Creating database $database"
	
	if [ -f $mysqldumpdir/$database.sql.gz ] ; then
		gunzip $mysqldumpdir/$database.sql.gz
	fi
	
	if [ -f $mysqldumpdir/$database.sql ] ; then
		
		mysql $mysql_str -e "CREATE DATABASE IF NOT EXISTS $database"
		mysql $mysql_str $database < $mysqldumpdir/$database.sql
		
		STATUS=$?
		if [ $STATUS -gt 0 ] ; then
			echo "There was an error in creating the database $database"
			exit 1;
		fi
		
	else
		echo "File not found: $mysqldumpdir/$database.sql"
		exit 1
	fi	
		
	echo ""
done

exit 0