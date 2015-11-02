#!/bin/sh

# source the properties
# Don't use shell-hostile constructs in the shell-readable properties file!
. config.properties

base_dir=$1; # this is the directory where the file will be created

if [ $# -lt 1 ] ; then
	base_dir="."
fi


props_file="$base_dir/jobqueue.app.properties"
echo "Creating properties file $props_file"

# Write jobqueue.app.properties file

echo "webapp.path=$webapp_path" > $props_file
echo "noreply.sender=$noreply_sender" >> $props_file
echo "" >> $props_file
echo "# directory where data from a remote server will be copied prior to upload" >> $props_file
echo "# remote.data.copy.dir=" >> $props_file
echo "" >> $props_file
echo "# If false ms job uploads will not run" >> $props_file
echo "job.upload.ms=true" >> $props_file
echo "" >> $props_file
echo "# If false protein inference will not run" >> $props_file
echo "job.proteininfer=true" >> $props_file
echo "" >> $props_file
echo "# If false percolator jobs will not run" >> $props_file
echo "# This should be set to false unless running on the MacCoss lab servers" >> $props_file
echo "# If false percolator jobs will not run" >> $props_file
echo "job.runperc=false" >> $props_file


exit 0