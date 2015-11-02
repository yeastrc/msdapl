#  Set tomcat.home to root of Tomcat installation

ant -Dtomcat.home=/data/webtools/apache-tomcat-7.0.53 -Dwebapp.path=../WebRoot -f PreCompilation_Ant_Script.xml > sysout.txt 2>syserr.txt


