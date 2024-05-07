package org.tcelor.quarkus.api.resource.user;

import java.time.Duration;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.quarkus.api.entity.User;
import org.tcelor.quarkus.api.model.rest.CreateUser;
import org.tcelor.quarkus.api.model.rest.DefaultResponse;
import org.tcelor.quarkus.api.service.UserService;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@ApplicationScoped
public class UserResource {

    @Inject
    private UserService userService;

    @Operation(summary = "Admin and User access to proove that resources can be roles restricted.")
    @GET
    @RolesAllowed({ "user", "admin" })
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/me")
    public Uni<DefaultResponse> me(@Context SecurityContext securityContext) {
        return Uni.createFrom()
                .item(new DefaultResponse(userService.salutation(securityContext.getUserPrincipal().getName())));
    }

    @Operation(summary = "Admin acces to persist a user with encrypted password.")
    @WithTransaction
    @POST
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> create(CreateUser user) {
        return User.add(user.username, user.password, "user")
                .replaceWith(() -> RestResponse.status(Response.Status.CREATED));
    }

    @Operation(summary = "Admin access to get all users informations stored in the database.")
    @WithTransaction
    @GET
    @RolesAllowed({ "admin" })
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<List<PanacheEntityBase>>> getUsers() {
        return User.listAll()
                .ifNoItem().after(Duration.ofMillis(5000)).failWith(new Exception("💥"))
                .onItem().transform((users -> {
                    return RestResponse.status(Response.Status.OK, users);
                }));
    }
}