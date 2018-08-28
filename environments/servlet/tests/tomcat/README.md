Tomcat profiles
===============

Tomcat 8.x - the default profile
--------------------------------

By default **tomcat8** profile is used:

        mvn clean test -Dincontainer

To override the default Tomcat 8 version:

        mvn clean test -Dincontainer -Dtomcat.version=8.5.22

Tomcat 9.x profile
------------------

To enable **tomcat9** profile:

        mvn clean test -Dincontainer -Dtomcat9

To override the default Tomcat 9 version:

        mvn clean test -Dincontainer -Dtomcat9 -Dtomcat.version=9.0.10