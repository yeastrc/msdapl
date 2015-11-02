#!/bin/sh

# ---------------------------------------------------------------------------------
# A shell script to create a directory for jobqqueue program and copy
# all the required file
# ---------------------------------------------------------------------------------

# source the properties
# Don't use shell-hostile constructs in the shell-readable properties file!
. config.properties

# create the directories
jobqueuedir="job_queue"
echo "creating $jobqueuedir"
mkdir $jobqueuedir
echo "creating $jobqueuedir/lib"
mkdir $jobqueuedir/lib
echo "creating $jobqueuedir/log"
mkdir $jobqueuedir/log

# copy the required jars
echo "copying required jars"
cp java/jobqueue.jar $jobqueuedir
cp java/lib/mysql-connector-java-5.1.6-bin.jar $jobqueuedir/lib
cp java/lib/mslib.jar $jobqueuedir/lib
cp java/lib/commons-dbcp-1.2.2.jar $jobqueuedir/lib
cp java/lib/commons-pool-1.4.jar $jobqueuedir/lib
cp java/lib/commons-collections-3.2.1.jar $jobqueuedir/lib
cp java/lib/commons-codec-1.4.jar $jobqueuedir/lib
cp java/lib/ibatis-2.3.0.677.jar $jobqueuedir/lib
cp java/lib/log4j-1.2.15.jar $jobqueuedir/lib
cp java/lib/mail.jar $jobqueuedir/lib
cp java/lib/yrc_utils.jar $jobqueuedir/lib
cp java/lib/yrc_nrseq.jar $jobqueuedir/lib
cp java/lib/protinfer.jar $jobqueuedir/lib

# create log4j.properties file
sh create_jobqueue_log4j_properties.sh $jobqueuedir
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating log4j properties file"
        exit 1;
fi


# create jobqueue.app.properties file
sh create_jobqueue_app_properties.sh $jobqueuedir
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating file jobqueue app properties file"
        exit 1;
fi

# create msDataDb.properties file
sh create_jobqueue_msDataDB_properties.sh $jobqueuedir
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating file jobqueue msDataDB properties file"
        exit 1;
fi

# copy the ibatis configuration files
echo "copying conf/NrSeqSqlMapConfig.xml.jobqueue"
cp conf/NrSeqSqlMapConfig.xml.jobqueue $jobqueuedir/NrSeqSqlMapConfig.xml
echo "copying conf/SqlMapConfig.xml.jobqueue"
cp conf/SqlMapConfig.xml.jobqueue $jobqueuedir/SqlMapConfig.xml