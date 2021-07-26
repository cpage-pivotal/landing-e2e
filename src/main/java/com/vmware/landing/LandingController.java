package com.vmware.landing;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.Workshop;
import com.vmware.landing.util.RequestUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LandingController {

    @Autowired
    private EducatesProperties _educatesProperties;

    @Autowired
    private RequestUtilities _requestUtilities;

    @RequestMapping("/")
    public String home(Model model) {
        List<Workshop> workshops = fetchWorkshops();
        model.addAttribute("workshops", workshops);
        return "index";
    }

    @Scheduled(fixedRate = 43200000,initialDelay = 43200000)
    public void refresh() {
        String tokenURL = "https://" + _educatesProperties.getPortalDomain() + "/oauth2/token/";
        var restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var params = new LinkedMultiValueMap<String, String>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", _educatesProperties.getRefreshToken());
        params.add("client_id", _educatesProperties.getRobotClientId());
        params.add("client_secret", _educatesProperties.getRobotSecret());

        _requestUtilities.sendRequest(tokenURL, restTemplate, httpHeaders, params);
    }

    @RequestMapping("/workshops/{environmentId}")
    public RedirectView launchWorkshop(@PathVariable("environmentId") String environmentId) {
        String requestURL = "https://" + _educatesProperties.getPortalDomain() +
                "/workshops/environment/" + environmentId + "/request/?index_url=" + _educatesProperties.getIndexUrl();
        var restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(_educatesProperties.getAccessToken());
        var entity = new HttpEntity<String>(httpHeaders);
        var response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, String.class );

        var parser = JsonParserFactory.getJsonParser();
        var body = parser.parseMap(response.getBody());

        String workshopUrl = "https://" + _educatesProperties.getPortalDomain() + body.get("url");
        return new RedirectView(workshopUrl);
    }

    private List<Workshop> fetchWorkshops() {
        List<Workshop> result = new ArrayList<>();
        String environmentsURL = "https://" + _educatesProperties.getPortalDomain() + "/workshops/catalog/environments/";
        var restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(_educatesProperties.getAccessToken());
        var entity = new HttpEntity<String>(httpHeaders);
        var response = restTemplate.exchange(environmentsURL, HttpMethod.GET, entity, String.class);

        var parser = JsonParserFactory.getJsonParser();
        var body = parser.parseMap(response.getBody());

        List<Map<String, Object>> environments = (List<Map<String, Object>>) body.get("environments");
        for (Map<String, Object> environment : environments) {
            String environmentName = (String) environment.get("name");
            Map<String, Object> workshopData = (Map<String, Object>) environment.get("workshop");
            Workshop workshop = new Workshop(environmentName, (String) workshopData.get("title"), (String) workshopData.get("description"));
            result.add(workshop);
        }

        return result;
    }
}
