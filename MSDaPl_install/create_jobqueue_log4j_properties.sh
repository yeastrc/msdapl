#!/bin/sh

# source the properties
# Don't use shell-hostile constructs in the shell-readable properties file!
. config.properties

base_dir=$1; # this is the directory where the file will be created

if [ $# -lt 1 ] ; then
	base_dir="."
fi


props_file="$base_dir/log4j.properties"
echo "Creating properties file $props_file"

log_dir="log"

# Write jobqueue.app.properties file

echo 'log4j.rootLogger=INFO, stdout' > $props_file
echo "" >> $props_file
echo 'log4j.additivity.uplodlog=false' >> $props_file
echo 'log4j.additivity.protinferlog=false' >> $props_file
echo 'log4j.additivity.jobqueuelog=false' >> $props_file
echo "" >> $props_file

echo '# Ibatis' >> $props_file
echo 'log4j.logger.com.ibatis=INFO, dblog' >> $props_file
echo '# shows SQL of prepared statements' >> $props_file
echo 'log4j.logger.java.sql.Connection=INFO, dblog' >> $props_file
echo '# shows parameters inserted into prepared statements' >> $props_file
echo 'log4j.logger.java.sql.PreparedStatement=INFO, dblog' >> $props_file
echo '# shows query results' >> $props_file
echo 'log4j.logger.java.sql.ResultSet=INFO, dblog' >> $props_file
echo 'log4j.logger.java.sql.Statement=INFO, dblog' >> $props_file
echo "" >> $props_file

echo '# job queue log' >> $props_file
echo 'log4j.logger.org.yeastrc.jqs=INFO, jobqueuelog' >> $props_file
echo "" >> $props_file
echo '# upload log' >> $props_file
echo 'log4j.logger.org.yeastrc.ms=INFO, uploadlog' >> $props_file
echo "" >> $props_file
echo '# protein inference log' >> $props_file
echo 'log4j.logger.edu.uwpr.protinfer=INFO, protinferlog' >> $props_file
echo "" >> $props_file

echo '# jobqueuelog output' >> $props_file
echo 'log4j.appender.jobqueuelog=org.apache.log4j.RollingFileAppender' >> $props_file
echo "log4j.appender.jobqueuelog.File=$log_dir/jobqueue.log" >> $props_file
echo 'log4j.appender.jobqueuelog.MaxFileSize=50MB' >> $props_file
echo 'log4j.appender.jobqueuelog.MaxBackupIndex=10' >> $props_file
echo 'log4j.appender.jobqueuelog.layout=org.apache.log4j.PatternLayout' >> $props_file
echo 'log4j.appender.jobqueuelog.layout.ConversionPattern=%5p [%t] [%d{dd MMM yyyy HH:mm:ss}] - %m%n' >> $props_file
echo "" >> $props_file

echo '# uploadlog output' >> $props_file
echo 'log4j.appender.uploadlog=org.apache.log4j.RollingFileAppender' >> $props_file
echo "log4j.appender.uploadlog.File=$log_dir/upload.log" >> $props_file
echo 'log4j.appender.uploadlog.MaxFileSize=50MB' >> $props_file
echo 'log4j.appender.uploadlog.MaxBackupIndex=10' >> $props_file
echo 'log4j.appender.uploadlog.layout=org.apache.log4j.PatternLayout' >> $props_file
echo 'log4j.appender.uploadlog.layout.ConversionPattern=%5p [%t] [%d{dd MMM yyyy HH:mm:ss}] - %m%n' >> $props_file
echo "" >> $props_file


echo '# protinferlog output' >> $props_file
echo 'log4j.appender.protinferlog=org.apache.log4j.RollingFileAppender' >> $props_file
echo "log4j.appender.protinferlog.File=$log_dir/protinfer.log" >> $props_file
echo 'log4j.appender.protinferlog.MaxFileSize=50MB' >> $props_file
echo 'log4j.appender.protinferlog.MaxBackupIndex=10' >> $props_file
echo 'log4j.appender.protinferlog.layout=org.apache.log4j.PatternLayout' >> $props_file
echo 'log4j.appender.protinferlog.layout.ConversionPattern=%5p [%t] [%d{dd MMM yyyy HH:mm:ss}] - %m%n' >> $props_file
echo "" >> $props_file

echo '# db log output' >> $props_file
echo 'log4j.appender.dblog=org.apache.log4j.RollingFileAppender' >> $props_file
echo "log4j.appender.dblog.File=$log_dir/db.log" >> $props_file
echo 'log4j.appender.dblog.MaxFileSize=50MB' >> $props_file
echo 'log4j.appender.dblog.MaxBackupIndex=10' >> $props_file
echo 'log4j.appender.dblog.layout=org.apache.log4j.PatternLayout' >> $props_file
echo 'log4j.appender.dblog.layout.ConversionPattern=%5p [%t] [%d{dd MMM yyyy HH:mm:ss}] - %m%n' >> $props_file
echo "" >> $props_file

echo '# Console output' >> $props_file
echo 'log4j.appender.stdout=org.apache.log4j.ConsoleAppender' >> $props_file
echo 'log4j.appender.stdout.layout=org.apache.log4j.PatternLayout' >> $props_file
echo 'log4j.appender.stdout.layout.ConversionPattern=%5p [%t] [%d{dd MMM yyyy HH\:mm\:ss}] - %m%n' >> $props_file


exit 0