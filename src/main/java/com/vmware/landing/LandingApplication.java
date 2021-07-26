package com.vmware.landing;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.util.RequestUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    CommandLineRunner fetchAccessToken() {
        return args -> {
            String tokenURL = "https://" + _educatesProperties.getPortalDomain() + "/oauth2/token/";
            var restTemplate = new RestTemplate();
            var httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.setBasicAuth(_educatesProperties.getRobotClientId(), _educatesProperties.getRobotSecret());

            var params = new LinkedMultiValueMap<String, String>();
            params.add("grant_type", "password");
            params.add("username", _educatesProperties.getRobotUser());
            params.add("password", _educatesProperties.getRobotPassword());

            _requestUtilities.sendRequest(tokenURL, restTemplate, httpHeaders, params);
        };
    }
}
