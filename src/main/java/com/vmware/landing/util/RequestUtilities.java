package com.vmware.landing.util;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.TrainingPortal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RequestUtilities {
    @Autowired
    EducatesProperties _educatesProperties;

    public void sendRequest(String tokenURL, RestTemplate restTemplate, HttpHeaders httpHeaders,
                            LinkedMultiValueMap<String, String> params, TrainingPortal portal) {
        var request = new HttpEntity(params, httpHeaders);
        var response = restTemplate.postForEntity(tokenURL, request, String.class);
        var parser = JsonParserFactory.getJsonParser();
        var body = parser.parseMap(response.getBody());

        String accessToken = (String) body.get("access_token");
        System.out.println( "SETTING ACCESS TOKEN: " + accessToken );
        portal.setAccessToken(accessToken);
        String refreshToken = (String) body.get("refresh_token");
        portal.setRefreshToken(refreshToken);
    }

    public Map<String, Object> runGetRequest(String requestURL, TrainingPortal trainingPortal) {
        var httpHeaders = new HttpHeaders();
        System.out.println( "USING ACCESS TOKEN: " + trainingPortal.getAccessToken() );
        httpHeaders.setBearerAuth(trainingPortal.getAccessToken());

        var restTemplate = new RestTemplate();
        var entity = new HttpEntity<String>(httpHeaders);
        var response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, String.class);
        var parser = JsonParserFactory.getJsonParser();
        return parser.parseMap(response.getBody());
    }
}
