Jetty profiles
==============

Jetty 12.x profile - the default profile
----------------------------------------

To enable **jetty-embedded-12** profile:

        mvn clean test -Dincontainer

To override the default Jetty 12 version:

        mvn clean test -Dincontainer -Djetty.version=12.x.y.z