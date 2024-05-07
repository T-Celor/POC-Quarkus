package org.tcelor.quarkus.api.resource.all;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.microprofile.openapi.annotations.Operation;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class GuideResource {

    @Operation(summary = "Endpoint to expose guide page.")
    @GET
    @Path("/guide")
    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    public String getForGuide() {
        return getGuide();
    }

    @Operation(summary = "Endpoint to expose guide page.")
    @GET
    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    public String getForIndex() {
        return getGuide();
    }

    private String getGuide() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("pages/guide.html");
            byte[] fileBytes = inputStream.readAllBytes();
            return new String(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h1>Error reading the guide</h1></body></html>";
        }
    }
}