package com.vmware.landing.util;

import com.vmware.landing.config.EducatesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestUtilities {
    @Autowired
    EducatesProperties _educatesProperties;

    public void sendRequest(String tokenURL, RestTemplate restTemplate, HttpHeaders httpHeaders, LinkedMultiValueMap<String, String> params) {
        var request = new HttpEntity(params, httpHeaders);

        var response = restTemplate.postForEntity(tokenURL, request, String.class);

        var parser = JsonParserFactory.getJsonParser();
        var body = parser.parseMap(response.getBody());
        String accessToken = (String) body.get("access_token");
        _educatesProperties.setAccessToken(accessToken);
        String refreshToken = (String) body.get("refresh_token");
        System.out.println( "SETTING REFRESH TOKEN: " + refreshToken );
        _educatesProperties.setRefreshToken(refreshToken);
    }

}
