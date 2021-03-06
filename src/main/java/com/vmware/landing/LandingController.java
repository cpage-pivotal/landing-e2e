package com.vmware.landing;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.TrainingPortal;
import com.vmware.landing.model.Workshop;
import com.vmware.landing.util.RequestUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        var portalWorkshops = new LinkedHashMap<TrainingPortal, List<Workshop>>();
        for (var portal : _educatesProperties.getTrainingPortals()) {
            List<Workshop> workshops;
            try {
                workshops = fetchWorkshops(portal);
            }
            catch ( HttpClientErrorException ex )
            {
                System.out.println( "Error retreiving workshops for " + portal );
                System.out.println( "Reauthenticating to " + portal);
                List<TrainingPortal> unauthenticated = new ArrayList<>();
                _requestUtilities.authenticateToPortal( portal, unauthenticated );
                if (!unauthenticated.isEmpty()) {
                    System.out.println( "Removing " + portal);
                    _educatesProperties.getTrainingPortals().remove(portal);
                    continue;
                }
                workshops = fetchWorkshops(portal);
            }
            portalWorkshops.put(portal, workshops);
        }
        model.addAttribute("portalWorkshops", portalWorkshops);
        return "index";
    }

    @Scheduled(fixedRate = 43200000, initialDelay = 43200000)
    public void refresh() {
        System.out.println( "REFRESHING TOKENS" );
        for (TrainingPortal portal : _educatesProperties.getTrainingPortals()) {
            String tokenURL = portal.getUriPrefix() + portal.getPortalDomain() + "/oauth2/token/";
            var restTemplate = new RestTemplate();
            var httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            var params = new LinkedMultiValueMap<String, String>();
            params.add("grant_type", "refresh_token");
            params.add("refresh_token", portal.getRefreshToken());
            params.add("client_id", portal.getRobotClientId());
            params.add("client_secret", portal.getRobotSecret());

            _requestUtilities.sendRequest(tokenURL, restTemplate, httpHeaders, params, portal);
        }
    }

    @RequestMapping("/portal/{portalId}/workshop/{environmentId}")
    public RedirectView launchWorkshop(@PathVariable("portalId") String portalId,
            @PathVariable("environmentId") String environmentId) {

        TrainingPortal trainingPortal = new TrainingPortal();
        for (TrainingPortal portal : _educatesProperties.getTrainingPortals()) {
            if (portal.getPortalDomain().equals(portalId))
                trainingPortal = portal;
        }

        String requestURL = trainingPortal.getUriPrefix() + trainingPortal.getPortalDomain() +
                "/workshops/environment/" + environmentId + "/request/?index_url=" + trainingPortal.getIndexUrl();
        Map<String, Object> body = _requestUtilities.runGetRequest(requestURL, trainingPortal);

        String workshopUrl = trainingPortal.getUriPrefix() + trainingPortal.getPortalDomain() + body.get("url");
        return new RedirectView(workshopUrl);
    }

    private List<Workshop> fetchWorkshops(TrainingPortal portal) {
        List<Workshop> result = new ArrayList<>();

        String environmentsURL = portal.getUriPrefix() + portal.getPortalDomain() + "/workshops/catalog/environments/";
        Map<String, Object> body = _requestUtilities.runGetRequest(environmentsURL, portal);

        List<Map<String, Object>> environments = (List<Map<String, Object>>) body.get("environments");
        for (Map<String, Object> environment : environments) {
            String environmentName = (String) environment.get("name");
            Map<String, Object> workshopData = (Map<String, Object>) environment.get("workshop");
            Workshop workshop = new Workshop(environmentName, (String) workshopData.get("title"),
                    (String) workshopData.get("description"));
            result.add(workshop);
        }

        return result;
    }
}
