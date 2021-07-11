package com.vmware.landing;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.Workshop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class LandingApplication {

    @Autowired
    EducatesProperties _educatesProperties;

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
            var request = new HttpEntity(params, httpHeaders);

            var response = restTemplate.postForEntity(tokenURL, request, String.class);

            var parser = JsonParserFactory.getJsonParser();
            var body = parser.parseMap(response.getBody());
            String accessToken = (String) body.get("access_token");
            _educatesProperties.setAccessToken(accessToken);
        };
    }
}
