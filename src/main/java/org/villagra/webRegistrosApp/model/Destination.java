package org.villagra.webRegistrosApp.model;

public class Destination {

    private String id;
    private String destination;

    public Destination(String id, String destination) {
        this.id = id;
        this.destination = destination;
    }
    public Destination() {}



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


}

