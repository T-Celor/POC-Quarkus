package org.tcelor.quarkus.api.resource.all;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.tcelor.quarkus.api.model.rest.DefaultResponse;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import io.quarkus.logging.Log; 

@Path("/api/public")
@ApplicationScoped
public class PublicResource {
    @Operation(summary = "Public access resource to proove auth can permit acces.")
    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<DefaultResponse> publicResource() {
        Log.info("Un utilisateur accede a cette api.");
        return Uni.createFrom().item(new DefaultResponse("Cette ressource est public et sans protection"));
    }
}
