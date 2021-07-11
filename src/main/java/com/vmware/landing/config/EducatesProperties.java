package com.vmware.landing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "educates")
public class EducatesProperties {
    private String _portalDomain;
    private String _robotUser;
    private String _robotPassword;
    private String _robotClientId;
    private String _robotSecret;

    private String _indexUrl = "";
    private String _accessToken = "";

    public String getPortalDomain() {
        return _portalDomain;
    }

    public void setPortalDomain(String portalDomain) {
        _portalDomain = portalDomain;
    }

    public String getRobotUser() {
        return _robotUser;
    }

    public void setRobotUser(String robotUser) {
        _robotUser = robotUser;
    }

    public String getRobotPassword() {
        return _robotPassword;
    }

    public void setRobotPassword(String robotPassword) {
        _robotPassword = robotPassword;
    }

    public String getRobotClientId() {
        return _robotClientId;
    }

    public void setRobotClientId(String robotClientId) {
        _robotClientId = robotClientId;
    }

    public String getRobotSecret() {
        return _robotSecret;
    }

    public void setRobotSecret(String robotSecret) {
        _robotSecret = robotSecret;
    }

    public String getIndexUrl() {
        return _indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        _indexUrl = indexUrl;
    }

    public String getAccessToken() {
        return _accessToken;
    }

    public void setAccessToken(String accessToken) {
        _accessToken = accessToken;
    }
}
