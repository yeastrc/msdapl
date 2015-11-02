#!/bin/bash
 
 dbname="msData_junit";
 user="root";
 
 basecmd="/usr/local/mysql/bin/mysql -u ${user}  -D ${dbname}";
 
tables=$(${basecmd} -e "SHOW TABLES;" | grep -v "+--" | grep -v  -i "Tables_in_${dbname}")
if [ $? -ne 0 ]; then
echo "Unable to retrieve the table names." >&2
  exit 1
fi
 
cmd=""
 
for table in ${tables}; do
cmd="${cmd} TRUNCATE ${table};"
done
 
 $(${basecmd} -e "${cmd}")


