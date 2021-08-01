package com.vmware.landing.config;

import com.vmware.landing.model.TrainingPortal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "educates")
public class EducatesProperties {
    private List<TrainingPortal> _trainingPortals = new ArrayList<>();

    public EducatesProperties() {
    }

    public List<TrainingPortal> getTrainingPortals() {
        return _trainingPortals;
    }

    public void setTrainingPortals(List<TrainingPortal> trainingPortals) {
        _trainingPortals = trainingPortals;
    }
}
