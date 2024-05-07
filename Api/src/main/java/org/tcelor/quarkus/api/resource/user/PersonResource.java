package org.tcelor.quarkus.api.resource.user;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.quarkus.api.entity.Person;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/persons")
@ApplicationScoped
public class PersonResource {

    @Operation(summary = "User access to retrieve all person that are stored in Postgre Database, in async way.")
    @GET
    @RolesAllowed({
        "user"
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Person>> get() {
        return Person.listAll();
    }

    @Operation(summary = "User access to find one person that is stored in Postgre Database, in async way.")
    @GET
    @Path("/{id}")
    @RolesAllowed({
        "user"
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> getSingle(Long id) {
        return Person.findById(id);
    }

    @Operation(summary = "User access to persist a person in async way in Postgre Database using reactive.")
    @POST
    @WithTransaction
    @RolesAllowed({
        "user"
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Person>> create(Person person) {
        return Panache.withTransaction(person::persist).replaceWith(RestResponse.status(Response.Status.CREATED, person));
    }

    @Operation(summary = "User access to delete a person, in async way, who is stored in Postgre Database using reactive.")
    @WithTransaction
    @DELETE
    @Path("/{id}")
    @RolesAllowed({
        "user"
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> delete(Long id) {
    return Person.findById(id)
            .flatMap(person -> {
               if (person != null) {
                   return Panache.withTransaction(person::delete).replaceWith(() -> RestResponse.status(Response.Status.OK, person));
               } else {
                    return Uni.createFrom().item(() -> RestResponse.status(Response.Status.NOT_FOUND));
               }
           });
    }
}
