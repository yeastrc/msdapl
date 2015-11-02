#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create and initialize the YRC_NRSEQ database for the following
# organisms:
# 1. S. cerevisiae (Source: SGD)
# 2. C. elegans  (Source: WormBase)
# 3. D. melanogaster (Source: FlyBase)
# 4. Human (Sources: IPI, SwissProt, HGNC)
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
	
schema_dir="$base_dir/schema"
fasta_dir="$base_dir/fasta"
java_dir="$base_dir/java"
log_dir="$base_dir/logs"

# java=/usr/bin/java

#
# CREATE THE PROPERTIES FILE
#
sh create_yrc_nrseq_db_properties.sh $base_dir $mysql_host $mysql_user $mysql_passwd
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating file $props_file"
        exit 1;
fi


# 
# CREATE YRC_NRSEQ DATABASE
# 
mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"

echo "Creating YRC_NRSEQ"
mysql $mysql_str < $schema_dir/YRC_NRSEQ.sql

STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "Error creating YRC_NRSEQ database"
	exit 1;
fi
echo ""


# 
# UPLOAD THE STANDARD FASTA FILES
# 

# databases=(SGD WormBase FlyBase IPI_HUMAN HGNC SwissProt)
databases=(SGD WormBase FlyBase IPI_HUMAN)

# echo ${databases[@]}

jar="$java_dir/fastaparser.jar"


# uncompress all fasta files in the firectory
echo "gunzip $fasta_dir/*.gz"
gunzip $fasta_dir/*.gz
echo ""

for database in "${databases[@]}"
do
	echo "Uploading $database fasta file"
	
	
	myfile="$fasta_dir/$database.fasta"
	mylog="$log_dir/$database.fastaparser.out"
	
	
	# if [ -f $myfile.gz ] ; then
	# 	gunzip $myfile.gz
	# fi
	
	if [ ! -f $myfile ] ; then
		echo "File does not exist: $myfile"
		exit 1;
	fi
	
	
	if [ $database = 'SGD' ] ; then
		echo "$java -jar $jar --parser SGD --file $myfile > $mylog"
	  	$java -jar $jar --parser SGD --file $myfile > $mylog
		
		# species='--species yeast'
		# description="Saccharomyces Genome Database"
	fi
	if [ $database = 'WormBase' ] ; then
		
		echo "$java -jar $jar --parser Wormbase --file $myfile > $mylog"
	  	$java -jar $jar --parser Wormbase --file $myfile > $mylog
	
		# species='--species worm'
		# description="WormBase Database"
	fi
	if [ $database = 'FlyBase' ] ; then
		
		echo "$java -jar $jar --parser FlyBase --file $myfile > $mylog"
	  	$java -jar $jar --parser FlyBase --file $myfile > $mylog
	
		# species='--species fly'
		# description="FlyBase Database"	
	fi
	if [ $database = 'IPI_HUMAN' ] ; then
		species='--species human'
		description="Human IPI Database"
		
		echo "$java -jar $jar $species --name $database --description \"$description\" --file $myfile > $mylog"
		$java -jar $jar $species --name $database --description "$description" --file $myfile > $mylog
		
	fi
	if	 [ $database = 'SwissProt' ] ; then
		
		echo "$java -jar $jar --parser SwissProt --file $myfile > $mylog"
	  	$java -jar $jar --parser SwissProt --file $myfile > $mylog
	
		# description="Swiss-Prot Database"
	fi
	
	# if [ $database = 'HGNC' ] ; then
	# 	species='--species human'
	# 	description="HGNC(HUGO) Database"
	# fi		
	
	
	STATUS=$?
	if [ $STATUS -gt 0 ] ; then
		echo "There was an error uploading $database.fasta. Please look at $log_dir/$database.fastaparser.out for more details."
	        exit 1;
	fi
	
	# gzip $fasta_dir/$database.fasta
	echo "Done uploading $database fasta file."
	echo ""
	
done


exit 0
