package org.openapitools;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.openapitools.sdk.enums.AdditionalParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

import java.util.HashMap;

@SpringBootApplication(
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@ComponentScan(
    basePackages = {"org.openapitools", "org.openapitools.api" , "org.openapitools.configuration"},
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class OpenApiGeneratorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OpenApiGeneratorApplication.class, args);
    }

    @Bean(name = "org.openapitools.OpenApiGeneratorApplication.jsonNullableModule")
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }


//    @Autowired
//    KindeClientSDK kindeClientSDK;

//    @Value("${kinde.host}")
//    public String domain;
//
//    @Value("${kinde.redirect.url}")
//    public String redirectUri;
//
//    @Value("${kinde.post.logout.redirect.url}")
//    public String logoutRedirectUri;
//
//    @Value("${kinde.client.id}")
//    public String clientId;
//
//    @Value("${kinde.client.secret}")
//    public String clientSecret;
//
//    @Value("${kinde.grant.type}")
//    public String grantType;
//
//    public String scopes;

    @Override
    public void run(String... args) throws Exception {

//        KindeClientSDK kindeClientSDK= new KindeClientSDK(
//                    domain,
//                    redirectUri,
//                    clientId,
//                    clientSecret,
//                    grantType,
//                    logoutRedirectUri,
//                    scopes="openid profile email offline",
//                    new HashMap<>(),
//                    null
//            );

//        kindeClientSDK.login(AdditionalParameters.ADDITIONAL_PARAMETER);
//        kindeClientSDK.login(new HashMap<>());
//        kindeClientSDK.getToken();
//        kindeClientSDK.register(new HashMap<>());
//        kindeClientSDK.createOrg(new HashMap<>());
//        kindeClientSDK.logout();
    }
}