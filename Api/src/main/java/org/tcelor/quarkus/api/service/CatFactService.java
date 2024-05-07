package org.tcelor.quarkus.api.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.tcelor.quarkus.api.model.rest.CatFactResponse;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/* @Path("/extensions") we can do prefix */
@RegisterRestClient(configKey = "extensions-api")
public interface CatFactService {
    @GET
    @Path("/fact")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<CatFactResponse> getCatFact();
}
