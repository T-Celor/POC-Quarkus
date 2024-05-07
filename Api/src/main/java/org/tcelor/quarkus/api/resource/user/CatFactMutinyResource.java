package org.tcelor.quarkus.api.resource.user;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.quarkus.api.model.rest.CatFactResponse;
import org.tcelor.quarkus.api.service.CatFactService;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;

import io.quarkus.logging.Log;
import java.time.Duration;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api/cat")
@ApplicationScoped
public class CatFactMutinyResource {
    @Inject
    @RestClient
    CatFactService apiCatFact;

    @Operation(summary = "Acces to proove as a user that you are able to make an external api and handle error or provoque a timeout with mutiny.")
    @GET
    @RolesAllowed("user")
    @Path("/fact")
    public Uni<RestResponse<CatFactResponse>> getFact() {
        return apiCatFact.getCatFact()
                .ifNoItem().after(Duration.ofMillis(500))
                .failWith(new Exception("💥"))
                .onItem().transform((catFactResponse -> {
                    Log.info("👍 " + catFactResponse);
                    return RestResponse.status(Response.Status.OK, catFactResponse);
                }))
                .onFailure().recoverWithUni(err -> {
                    Log.info(err.getMessage());
                    if (err.getMessage().equals("Timeout")) 
                        return Uni.createFrom().item(RestResponse.status(Response.Status.REQUEST_TIMEOUT));
                    return Uni.createFrom().item(RestResponse.status(500));
                });
    }
}