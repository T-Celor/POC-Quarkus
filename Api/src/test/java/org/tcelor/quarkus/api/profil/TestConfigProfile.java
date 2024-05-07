package org.tcelor.quarkus.api.profil;

import java.util.Map;


import io.quarkus.test.junit.QuarkusTestProfile;

public class TestConfigProfile implements QuarkusTestProfile  {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("salutation-word", "test");
    }
}