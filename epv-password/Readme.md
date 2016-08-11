Before running the code  please follow below pre-requisites

1. Install the pass word sdk jar to your maven repository by running the below command

mvn install:install-file -Dfile=javapasswordsdk.jar -DgroupId=com.amex -DartifactId=javapasswordsdk -Dversion=1.0 -Dpackaging=jar -Dgenerate=true 

2. include the dependency with the values used for groupId , artifactId, version in above step

3. compile the code using the pom provided , this will extract all the necessary classes from javapasswordsdk and embed it to the current jar

4. deploy the jar to AMQ