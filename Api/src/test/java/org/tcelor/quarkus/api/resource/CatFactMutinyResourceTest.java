package org.tcelor.quarkus.api.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tcelor.quarkus.api.model.rest.CatFactResponse;
import org.tcelor.quarkus.api.resource.user.CatFactMutinyResource;
import org.tcelor.quarkus.api.service.CatFactService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@QuarkusTest
public class CatFactMutinyResourceTest {

    @RestClient
    @Inject
    CatFactService mockCatFactService;

    CatFactMutinyResource catFactMutinyResource;

    @BeforeEach
    public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        mockCatFactService = Mockito.mock(CatFactService.class);
       catFactMutinyResource = new CatFactMutinyResource();
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void succedGetFact() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        CatFactResponse catFactResponse = new CatFactResponse();
        catFactResponse.fact = "test";
        when(mockCatFactService.getCatFact()).thenReturn(Uni.createFrom().item(catFactResponse));
        
        Field apiCatFact = CatFactMutinyResource.class.getDeclaredField("apiCatFact");
        apiCatFact.setAccessible(true);
        apiCatFact.set(catFactMutinyResource, mockCatFactService);

        Uni<RestResponse<CatFactResponse>> response = catFactMutinyResource.getFact();

        response.subscribe().with(restResponse -> {
            assertEquals(Response.Status.OK.getStatusCode(), restResponse.getStatusInfo().getStatusCode());
            assertEquals("test", restResponse.readEntity(CatFactResponse.class).fact);
        });
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void timeoutGetFact() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        CatFactResponse catFactResponse = new CatFactResponse();
        catFactResponse.fact = "test";
        when(mockCatFactService.getCatFact()).thenAnswer(invocation -> {
            try {
                Thread.sleep(3000); 
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Uni.createFrom().item(catFactResponse);
        });


        Field apiCatFact = CatFactMutinyResource.class.getDeclaredField("apiCatFact");
        apiCatFact.setAccessible(true);
        apiCatFact.set(catFactMutinyResource, mockCatFactService);

        Uni<RestResponse<CatFactResponse>> response = catFactMutinyResource.getFact();

        response.subscribe().with(restResponse -> {
            assertEquals(Response.Status.REQUEST_TIMEOUT.getStatusCode(), restResponse.getStatusInfo().getStatusCode());
            assertEquals("test", restResponse.readEntity(CatFactResponse.class).fact);
        });
    }
}