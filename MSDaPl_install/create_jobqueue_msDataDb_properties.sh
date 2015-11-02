#!/bin/sh

# source the properties
# Don't use shell-hostile constructs in the shell-readable properties file!
. config.properties

base_dir=$1; # this is the directory where the file will be created

if [ $# -lt 1 ] ; then
	base_dir="."
fi


props_file="$base_dir/msDataDB.properties"
echo "Creating properties file $props_file"

# Write msDataDB.properties file

echo 'db.driver=com.mysql.jdbc.Driver' > $props_file
echo "" >> $props_file

echo '# msData database' >> $props_file
echo "db.url=jdbc:mysql://$db_host/msData?autoReconnect=true" >> $props_file
echo 'db.name=msData' >> $props_file
echo "db.user=$app_db_user" >> $props_file
echo "db.password=$app_db_passwd" >> $props_file
echo "" >> $props_file

echo '# YRC_NRSEQ database' >> $props_file
echo "db.nrseq.url=jdbc:mysql://$db_host/YRC_NRSEQ?autoReconnect=true" >> $props_file
echo 'db.nrseq.name=YRC_NRSEQ' >> $props_file
echo "db.nrseq.user=$app_db_user" >> $props_file
echo "db.nrseq.password=$app_db_passwd" >> $props_file
echo "" >> $props_file

echo '# jobQueue database' >> $props_file
echo "db.jobqueue.url=jdbc:mysql://$db_host/jobQueue?autoReconnect=true" >> $props_file
echo 'db.jobqueue.name=jobQueue' >> $props_file
echo "db.jobqueue.user=$app_db_user" >> $props_file
echo "db.jobqueue.password=$app_db_passwd" >> $props_file
echo "" >> $props_file

echo '# mainDb database' >> $props_file
echo "db.maindb.url=jdbc:mysql://$db_host/mainDb?autoReconnect=true" >> $props_file
echo 'db.maindb.name=mainDb' >> $props_file
echo "db.maindb.user=$app_db_user" >> $props_file
echo "db.maindb.password=$app_db_passwd" >> $props_file
echo "" >> $props_file

echo 'db.maxactiveconn=100' >> $props_file
echo 'db.maxidleconn=30' >> $props_file
echo 'db.maxcheckouttime=20000' >> $props_file
echo 'db.maxwait=20000' >> $props_file
echo 'db.pingenabled=true' >> $props_file
echo 'db.pingquery=SELECT 1' >> $props_file
echo 'db.pingolderthan=300000' >> $props_file
echo 'db.pingnotusedfor=300000' >> $props_file
echo "" >> $props_file

echo '# This property determines how scan peaks are stored' >> $props_file
echo '# D: m/z stored as double, intensity stored as float' >> $props_file
echo '# S : m/z and intensity stored as string' >> $props_file
echo 'db.peakdata.storage=D' >> $props_file
echo "" >> $props_file

echo '# If true Ibatis DAO classes will be used.' >> $props_file
echo '# DO NOT CHANGE THIS' >> $props_file
echo 'dao.ibatis=true' >> $props_file
echo "" >> $props_file

echo '# Set to true if target and decoy sqt files should be backed up' >> $props_file
echo 'backup.sqt=false' >> $props_file
echo '# Directory where Sequest search files will be copied' >> $props_file
echo 'backup.dir=' >> $props_file
echo "" >> $props_file

echo '# When uploading pepxml files check the protein peptides matches found in the interact.pep.xml files' >> $props_file
echo '# against the one found by the ProteinPeptideMatchingService' >> $props_file
echo 'interact.pepxml.checkpeptideproteinmatches=false' >> $props_file


exit 0