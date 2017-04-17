# sunfounder-raspi-4j
SunFounder super kit for raspberry pi exercices written in JAVA using Pi4J.

This project tries to study how to add jUnit testing and Java Abstractions on top of the Pi4j.

For more details on how to setup and run these exercices please see my blog [here](http://marcandreuf.blogspot.kr/2015/03/hello-good-to-see-you-reading-this-post.html)

In Summary just edit the pom.xml file section called

> "SETUP PROPERTIES FOR YOUR PI"

Then run maven commands as follows:

```
// For checking that the project builds properly.
mvn clean test -Dpi.transfer.dev=true -P FastTests

// To deploy the project into the py once the pom.xml properties have been setup.
mvn clean install -Dpi.transfer.dev=true -P FastTests
```

If you have any question contact me at

marcandreuf@gmail.com

Thanks for coding.
I hope it helps enjoy.