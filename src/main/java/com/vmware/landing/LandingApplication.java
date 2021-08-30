package com.vmware.landing;

import com.vmware.landing.config.EducatesProperties;
import com.vmware.landing.model.TrainingPortal;
import com.vmware.landing.util.RequestUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

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
            List<TrainingPortal> unauthenticated = new ArrayList<>();
            System.out.println( "Authenticating to portals");

            for (TrainingPortal portal : _educatesProperties.getTrainingPortals()) {
                _requestUtilities.authenticateToPortal(portal, unauthenticated);
            }

            for (TrainingPortal portal : unauthenticated) {
                _educatesProperties.getTrainingPortals().remove(portal);
            }
        };
    }

}
