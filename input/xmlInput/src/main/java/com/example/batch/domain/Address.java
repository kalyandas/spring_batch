package com.example.batch.domain;

/**
 * Created by kalyan on 19-10-2016.
 */
public class Address {
    String street,city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }
    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
