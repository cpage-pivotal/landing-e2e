package com.vmware.landing.util;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.TrainingPortal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    public void authenticateToPortal(TrainingPortal portal, List<TrainingPortal> unauthenticated) {
        String tokenURL = portal.getUriPrefix() + portal.getPortalDomain() + "/oauth2/token/";
        var restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBasicAuth(portal.getRobotClientId(), portal.getRobotSecret());

        var params = new LinkedMultiValueMap<String, String>();
        params.add("grant_type", "password");
        params.add("username", portal.getRobotUser());
        params.add("password", portal.getRobotPassword());

        try {
            sendRequest(tokenURL, restTemplate, httpHeaders, params, portal);
        } catch (HttpClientErrorException | ResourceAccessException ex) {
            System.out.println("Could not authenticate to " + portal);
            unauthenticated.add(portal);
        }
    }
}
