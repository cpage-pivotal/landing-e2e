package com.vmware.landing;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.TrainingPortal;
import com.vmware.landing.util.RequestUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LandingApplication {

    @Autowired
    EducatesProperties _educatesProperties;

    @Autowired
    RequestUtilities _requestUtilities;

    public static void main(String[] args) {
        SpringApplication.run(LandingApplication.class, args);
    }

    @Bean
    CommandLineRunner fetchAccessTokens() {
        return args -> {
            System.out.println("Current Directory " + System.getProperty("user.dir"));

            List<TrainingPortal> unauthenticated = new ArrayList<>();

            for (TrainingPortal portal : _educatesProperties.getTrainingPortals()) {
                String tokenURL = "https://" + portal.getPortalDomain() + "/oauth2/token/";
                var restTemplate = new RestTemplate();
                var httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.setBasicAuth(portal.getRobotClientId(), portal.getRobotSecret());

                var params = new LinkedMultiValueMap<String, String>();
                params.add("grant_type", "password");
                params.add("username", portal.getRobotUser());
                params.add("password", portal.getRobotPassword());

                try {
                    _requestUtilities.sendRequest(tokenURL, restTemplate, httpHeaders, params, portal);
                } catch (HttpClientErrorException ex) {
                    System.out.println("Could not authenticate to " + portal);
                    unauthenticated.add(portal);
                }
            }

            for (TrainingPortal portal : unauthenticated) {
                _educatesProperties.getTrainingPortals().remove(portal);
            }
        };
    }
}
