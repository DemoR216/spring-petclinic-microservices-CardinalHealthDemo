package org.springframework.samples.petclinic.customers.web;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

public class OwnerRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    @Digits(fraction = 0, integer = 12)
    private String telephone;

    public OwnerRequest() {
    }

    public OwnerRequest(String firstName, String lastName, String address, String city, String telephone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getTelephone() {
        return telephone;
    }
}
