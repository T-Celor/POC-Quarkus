package org.tcelor.quarkus.api.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    @ConfigProperty(name = "salutation-word")
    private String salutationWord;

    public String salutation(String name) {
        return salutationWord + " " + name;
    }

}
