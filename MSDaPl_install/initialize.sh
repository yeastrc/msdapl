#!/bin/sh

# ---------------------------------------------------------------------------------
# A shell script to:
# 1. Create directories for the associated programs:
#    a. fastaparser: program for uploading fasta files
#    b. jobqueue: program for uploading data
# 2. Create all the databases required for MSDaPl
# 3. Create a deployable war archive for MSDaPl (msdapl.war)
# ---------------------------------------------------------------------------------

# ---------------------------------------------------------------------------------
# 1. Create directories for the associated programs:
#    a. fastaparser: program for uploading fasta files
#    b. jobqueue: program for uploading data
# ---------------------------------------------------------------------------------
echo "creating fastaparser program directory"
sh create_fastaparser_dir.sh
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating fastaparser program directory"
	exit 1;
fi
echo ""

echo "creating jobqueue program directory"
sh create_jobqueue_dir.sh
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating jobqueue program directory"
	exit 1;
fi
echo ""


# ---------------------------------------------------------------------------------
# 2. Create all the databases required for MSDaPl
# ---------------------------------------------------------------------------------
echo 'Creating MSDaPl databases'
sh init_msdapl_databases.sh
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error in creating MSDaPl databases"
	exit 1;
fi
echo ""

echo 'Creating protein database and other biological databases'
sh create_biodb.sh
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error in creating the protein and other biological databases"
	exit 1;
fi
echo ""


# ---------------------------------------------------------------------------------
# 3. Create a deployable war archive for MSDaPl (msdapl.war)
# ---------------------------------------------------------------------------------
echo "Creating msdapl.war"
sh create_msdapl_war.sh
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error creating a deployable war"
	exit 1;
fi
echo ""

exit 0