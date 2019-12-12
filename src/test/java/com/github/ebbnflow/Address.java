package com.github.ebbnflow;

import com.google.common.base.Objects;

public class Address {
    private String streetName;
    private String streetNumber;
    private String city;
    private String state;
    private String zip;

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equal(streetName, address.streetName) &&
                Objects.equal(streetNumber, address.streetNumber) &&
                Objects.equal(city, address.city) &&
                Objects.equal(state, address.state) &&
                Objects.equal(zip, address.zip);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(streetName, streetNumber, city, state, zip);
    }

    @Override
    public String toString() {
        return "Address{" +
                "streetName='" + streetName + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }
}
