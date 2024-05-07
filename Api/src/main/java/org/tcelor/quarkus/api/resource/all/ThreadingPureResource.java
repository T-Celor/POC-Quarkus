package org.tcelor.quarkus.api.resource.all;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.tcelor.quarkus.api.model.rest.DefaultResponse;
import org.tcelor.quarkus.api.service.thread.ThreadingPureService;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/thread")
public class ThreadingPureResource {

    @Inject
    ThreadingPureService threadingPureService;

    @Operation(description = "Api for working on simple exercice in multithread, using :\n - CountDownLatch,\n - ExecutorServices,\n - Futur,\n - Producer-Consumer Pattern, \n - Shared resource,\n - Socket for performing HTTP GET,\n - Socket for retriving result in async way.\n\n This entry point is non blocking and natively async (without using reactive library).", 
        summary = "Natively (without library) reactive async Api for using multithreads and sockets (use pool of 5 threads).")
    @GET
    @Path("/simpleWork/{numberCall}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public CompletableFuture<DefaultResponse> makeSimpleWork(@PathParam(value = "numberCall") Integer numberCall) {
        CompletableFuture<DefaultResponse> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            Future<List<String>> result = threadingPureService.execute(numberCall);
            try {
                future.complete(new DefaultResponse(result.get()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return future;
    }
}
