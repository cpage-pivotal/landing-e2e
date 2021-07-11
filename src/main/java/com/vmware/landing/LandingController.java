package com.vmware.landing;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.Workshop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LandingController {

    @Autowired
    private EducatesProperties _educatesProperties;

    @RequestMapping("/")
    public String home(Model model) {
        List<Workshop> workshops = fetchWorkshops();
        model.addAttribute("workshops", workshops);
        return "index";
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
        List<Workshop> result = new ArrayList<Workshop>();
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
