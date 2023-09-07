##Kinde Java SDK
The Kinde SDK for Java.

######PRs Welcome Kinde Docs Kinde Community

###Development
This project contains an SDK package with a class called KindClientSDK. Inside this class, you will find all the main functions and methods that you need to use in another project. Any changes related to the SDK should be made through this file.

Additionally, within the SDK package, there is a callback controller class where we have implemented a method for handling callbacks. If you need to make any changes to the callback request processing, you should do so in this file.

Furthermore, the SDK package includes sub-packages for enums, OAuth2, storage, and utilities. In the storage package, you will find functionality related to cookie storage, and within the OAuth2 package, we have classes for handling authorization code, client credentials, and PKCE (Proof Key for Code Exchange).

These components collectively make up the SDK, and you should make any necessary modifications or enhancements within the respective files to maintain and extend the SDK's functionality.

###Initial set up
Clone the repository to your machine:
```
git clone https://github.com/kinde-oss/kinde-java-sdk
```

Go into the project:
```
cd kinde-java-sdk
```

Install the dependencies:
```
mvn clean install
```


##Publishing
The core team handles publishing.

##Documentation
For details on integrating this SDK into your project, head over to the Kinde docs and see the Java SDK doc 👍🏼.

####Create a JAR file of sdk project
```
mvn clean install
```

####Execute the mvn install:install-file command to install the JAR file to your local repository:
```
mvn install:install-file -Dfile=/Users/rahoolkumar/Downloads/international-kinde-java/target/kindejava.jar -DgroupId=org.openapitools -DartifactId=openapi-spring -Dversion=1 -Dpackaging=jar
```
Make sure to replace ``` /Users/rahoolkumar/Downloads/international-kinde-java/target/kindejava.jar``` with the actual path to your JAR file.

####In the pom.xml of the other project where you want to use this dependency, add the following dependency entry:
```
<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-spring</artifactId>
    <version>1</version>
    <scope>compile</scope>
</dependency>
```
Save the pom.xml file.

Maven will automatically download the dependency from your local repository and make it available in your project.

###Project Setup
Add following properties in application.properties file

```
kinde.host=https://invozone.kinde.com
kinde.redirect.url=http://localhost:8080/api/auth/kinde_callback
kinde.post.logout.redirect.url=http://localhost:8080
kinde.client.id=b744e0d23fd1445dae2d9fd0845997f9
kinde.client.secret=NHUaiCR2ULZtR7FQLrfrJP1gkkMxK1odtWiRAyEjrLFBJqkkGNC
kinde.grant.type=authorization_code
```

Create KindeClientSDK object to use sdk methods
```
@PostConstruct
public void updateKindeClientSDK(){
    this.kindeClientSDK=new KindeClientSDK(
            domain,
            redirectUri,
            clientId,
            clientSecret,
            grantType,
            logoutRedirectUri
            );
}
```

Add @ComponentScan annotation in your main application. It should include the packages where your controllers from both the main application and the dependency project are located.
```
@ComponentScan(basePackages = {"com.example.demo.controller",
		"org.openapitools.api",
		"org.openapitools.model",
		"org.openapitools.configuration"})
```



###Contributing
Please refer to Kinde’s contributing guidelines.

###License
By contributing to Kinde, you agree that your contributions will be licensed under its MIT License.