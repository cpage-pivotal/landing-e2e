package com.vmware.landing.model;

public class Workshop {

    private String _name;
    private String _title;
    private String _description;

    public Workshop(String name, String title, String description) {
        _name = name;
        _title = title;
        _description = description;
    }

    public String getName() {
        return _name;
    }

    public String getTitle() {
        return _title;
    }

    public String getDescription() {
        return _description;
    }

    @Override
    public String toString() {
        return "Workshop{" +
                "_name='" + _name + '\'' +
                ", _title='" + _title + '\'' +
                ", _description='" + _description + '\'' +
                '}';
    }
}
