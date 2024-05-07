package org.tcelor.quarkus.api.model.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BufferRequest {
    /* Singleton */
    private static BufferRequest bufferRequest = null;

    private BufferRequest() {
        this.init();
    }
    
    public static synchronized BufferRequest getBufferRequest() {
        if (bufferRequest == null) {
            bufferRequest = new BufferRequest();
        }
        return bufferRequest;
    }

    /* Variables */
    private List<String> buffer;
    private ReentrantLock lock;
    private Boolean canConsum;

    public void init() {
        buffer = new ArrayList<>();
        lock = new ReentrantLock();
        canConsum = false;
    }

    public List<String> get() {
        return Collections.unmodifiableList(new ArrayList<>(buffer));
    }

    public Boolean canConsum() {
        return canConsum;
    }

    public void product(String newString) {
        lock.lock();
        try {
            buffer.add(newString);
            canConsum = true;
        } finally {
            lock.unlock();
        }
    }

    public String consum() {
        lock.lock();
        try {
            if (buffer != null && buffer.size() > 0) {
                if (buffer.size() == 1) {
                    canConsum = false; //The remove will delete the last data so we cannont consum anymore
                }
                return buffer.remove(0);
            }
            canConsum = false; //Error was somewhere
            return null;
        } finally {
            lock.unlock();
        }
    }
}