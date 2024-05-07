package org.tcelor.quarkus.api.model.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ListResultRequest {

    /* Singleton */
    private static volatile ListResultRequest lResultRequest = null;

    private ListResultRequest() {
        this.init();
    }
    
    public static synchronized ListResultRequest getListResultRequest() {
        if (lResultRequest == null) {
            lResultRequest = new ListResultRequest();
        }
        return lResultRequest;
    }

    /* Variables */
    private List<String> listResultRequest;
    private Semaphore lock;

    public void init() {
        listResultRequest = new ArrayList<>();
        lock = new Semaphore(1);
    }

    public List<String> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(listResultRequest));
    }

    public void putResult(String result) {
        try {
            lock.acquire();
            listResultRequest.add(result);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            lock.release();
        }
    }    
}
