package com.vmware.landing.model;

import java.util.Objects;

public class TrainingPortal {
    private String _name;
    private String _portalDomain;
    private String _robotUser = "robot@eduk8s";
    private String _robotPassword;
    private String _robotClientId;
    private String _robotSecret;
    private String _indexUrl;
    private boolean _secure = true;
    private String _accessToken = "";
    private String _refreshToken = "";

    public TrainingPortal() {
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

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

    public String getRefreshToken() {
        return _refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        _refreshToken = refreshToken;
    }

    public boolean isSecure() {
        return _secure;
    }

    public void setSecure(boolean secure) {
        _secure = secure;
    }

    public String getUriPrefix() {
        String result = "https://";
        if ( !_secure )
            result = "http://";
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingPortal that = (TrainingPortal) o;
        return getPortalDomain().equals(that.getPortalDomain());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPortalDomain());
    }

    @Override
    public String toString() {
        return "TrainingPortal{" +
                "_portalDomain='" + _portalDomain + '\'' +
                '}';
    }
}
