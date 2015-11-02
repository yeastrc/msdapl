#!/bin/sh

# source the properties
. config.properties

base_dir=$1; # this is the directory where the file will be created

if [ $# -lt 1 ] ; then
	base_dir="."
fi


props_file="$base_dir/fasta_parser.properties"
echo "Creating properties file $props_file"

# Write fasta_parser.properties file
echo "# MySQL database properties" > $props_file
echo "" >> $props_file

echo "db.nrseq.host=$db_host" >> $props_file
echo "db.nrseq.name=YRC_NRSEQ" >> $props_file
echo "db.nrseq.username=$db_user" >> $props_file
echo "db.nrseq.password=$db_passwd" >> $props_file
echo "" >> $props_file

echo "db.ncbi_tax.host=$db_host" >> $props_file
echo "db.ncbi_tax.name=mainDb" >> $props_file
echo "db.ncbi_tax.username=$db_user" >>$props_file
echo "db.ncbi_tax.password=$db_passwd" >> $props_file
echo "" >> $props_file

echo "db.maindb.host=$db_host" >> $props_file
echo "db.maindb.name=mainDb" >> $props_file
echo "db.maindb.username=$db_user" >>$props_file
echo "db.maindb.password=$db_passwd" >> $props_file
exit 0