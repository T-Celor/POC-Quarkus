package org.tcelor.quarkus.api.resource.admin;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.tcelor.quarkus.api.model.rest.DefaultResponse;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api/admin")
public class AdminResource {

    @Operation(summary = "Acces to proove that you got an admin user.")
    @GET
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<DefaultResponse> adminResource() {
        return Uni.createFrom().item(new DefaultResponse("Vous êtes log en tant qu'admin."));
    }
}
