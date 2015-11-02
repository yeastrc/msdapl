#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create databases for the following standard databases
# 1. sgd
# 2. flybase
# 3. wormbase
# 4. hgnc
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

biodb_dir="$base_dir/biodb"	
schema_dir="$base_dir/schema"
java_dir="$base_dir/java"
log_dir="$base_dir/logs"

#
# CREATE THE PROPERTIES FILE
#
sh create_yrc_biodb_apps_db_properties.sh $base_dir $mysql_host $mysql_user $mysql_passwd
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating file $props_file"
        exit 1;
fi


mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"


# databases=(sgd wormbase flybase hgnc uniprot)
databases=(uniprot)
# ---------------------------------------------------------------------------------
# Create empty databases
# ---------------------------------------------------------------------------------
for database in "${databases[@]}"
do
	echo "Creating database $database"
	# echo "mysql $mysql_str < $schema_dir/$database.sql"
	mysql $mysql_str < $schema_dir/$database.sql

	STATUS=$?
	if [ $STATUS -gt 0 ] ; then
		echo "There was an error create the database $database"
		exit 1;
	fi
	echo ""
done

# ---------------------------------------------------------------------------------
# Populate the databases:  
#     1. Build the tables containing name mappings, descriptions etc.
# ---------------------------------------------------------------------------------
classpath="./:java/yrc_biodb_builders.jar:java/commons-lang-2.6.jar:java/mysql-connector-java-5.1.6-bin.jar:java/log4j-1.2.15.jar"

for database in "${databases[@]}"
do
	echo "Populating database $database (name mappings, descriptions etc.)"
	
	logfile="$log_dir/$database"_biodb_builder.log
	dbdir="$biodb_dir/$database"
	
	if [ $database = 'sgd' ] ; then
		
		# build tblFeature and tblGeneName
		echo "$java -classpath $classpath org.yeastrc.sgd.SGDFeatureTablesBuilder $dbdir/SGD_features.tab  > $logfile"
		$java -classpath $classpath org.yeastrc.sgd.SGDFeatureTablesBuilder $dbdir/SGD_features.tab > $logfile
		
	fi
	
	if [ $database = 'wormbase' ] ; then
		
		# build tblProteins and tblProteinGenes
		echo "$java -classpath $classpath org.yeastrc.wormbase.WormBaseTablesBuilder $dbdir/c_elegans.WS225.wormpep_package/wormpep.table225  > $logfile"
		$java -classpath $classpath org.yeastrc.wormbase.WormBaseTablesBuilder $dbdir/c_elegans.WS225.wormpep_package/wormpep.table225  > $logfile
		
	fi
	
	if [ $database = 'flybase' ] ; then
		
		# build geneAnnotation and proteinAnnotation and proteinGeneMapping
		synonymfile="$dbdir/fb_synonym_fb_2011_06.tsv"
		genesummaryfile="$dbdir/gene_summaries.tsv"
		mapfile="$dbdir/fbgn_fbtr_fbpp_fb_2011_06.tsv"
		
		echo "$java -classpath $classpath org.yeastrc.flybase.FlyBaseAnnotationsTablesBuilder $synonymfile $genesummaryfile  $mapfile > $logfile"
		$java -classpath $classpath org.yeastrc.flybase.FlyBaseAnnotationsTablesBuilder $synonymfile $genesummaryfile  $mapfile > $logfile
		
	fi
	
	if [ $database = 'hgnc' ] ; then
		
		# build hgnc_names
		echo "$java -classpath $classpath org.yeastrc.hgnc.HGNCTableBuilder $dbdir/hgnc_downloads.cgi > $logfile"
		$java -classpath $classpath org.yeastrc.hgnc.HGNCTableBuilder $dbdir/hgnc_downloads.cgi > $logfile
	fi
	
	if [ $database = 'uniprot' ] ; then
		
		# build uniprot_name 
		echo "$java -classpath $classpath org.yeastrc.uniprot.UniprotNameTableBuilder $dbdir/uniprot_sprot.dat > $logfile"
	$java -classpath $classpath org.yeastrc.uniprot.UniprotNameTableBuilder $dbdir/uniprot_sprot.dat > $logfile
	fi
	
	STATUS=$?
	if [ $STATUS -gt 0 ] ; then
		echo "There was an error in creating the database $database"
		exit 1;
	fi
	echo ""
	
	
done

# ---------------------------------------------------------------------------------
# Populate the databases:  
#     1. Build the tables for GO annoations.
# ---------------------------------------------------------------------------------
for database in "${databases[@]}"
do
	echo "Populating database $database (GO annotations tables)"
	
	logfile="$log_dir/sgd_biodb_builder.log"
	dbdir="$biodb_dir/$database"
	
	if [ $database = 'sgd' ] ; then
		
		# build tblGOAnnotation
		echo "$java -classpath $classpath org.yeastrc.sgd.SGDGOAnnotationTableBuilder $dbdir/gene_association.sgd >> $logfile"
		$java -classpath $classpath org.yeastrc.sgd.SGDGOAnnotationTableBuilder $dbdir/gene_association.sgd >> $logfile
		
	fi
	if [ $database = 'wormbase' ] ; then
		
		# build tblGOAnnotation
		echo "$java -classpath $classpath org.yeastrc.wormbase.WormBaseGOAnnotationTableBuilder $dbdir/gene_association.WS225.wb.ce >> $logfile"
		$java -classpath $classpath org.yeastrc.wormbase.WormBaseGOAnnotationTableBuilder $dbdir/gene_association.WS225.wb.ce >> $logfile
		
	fi
	if [ $database = 'flybase' ] ; then
		
		# build tblGOAnnotation
		echo "$java -classpath $classpath org.yeastrc.flybase.FlyBaseGOAnnotationTableBuilder $dbdir/gene_association.fb >> $logfile"
		$java -classpath $classpath org.yeastrc.flybase.FlyBaseGOAnnotationTableBuilder $dbdir/gene_association.fb >> $logfile
		
	fi
	if [ $database = 'hgnc' ] ; then
		echo "Nothing to do for hgnc database"
	fi
	
	STATUS=$?
	if [ $STATUS -gt 0 ] ; then
		echo "There was an error in populating go annotations for database $database"
		exit 1;
	fi
	echo ""
	
done


exit 0