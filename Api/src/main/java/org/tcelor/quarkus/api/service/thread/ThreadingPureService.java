package org.tcelor.quarkus.api.service.thread;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.tcelor.quarkus.api.model.thread.BufferRequest;
import org.tcelor.quarkus.api.model.thread.ListResultRequest;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ThreadingPureService {

    public String host = "catfact.ninja";

    private String path = "/fact";

    private class RunnableTaskRequester implements Runnable {
        private BufferRequest bufferRequest = BufferRequest.getBufferRequest();
        private ListResultRequest listResultRequest = ListResultRequest.getListResultRequest();
        private Integer id;
        private CountDownLatch latch;

        public RunnableTaskRequester(Integer newId, CountDownLatch newLatch) {
            id = newId;
            latch = newLatch;
        }

        @Override
        public void run() {
            String dataToConsum = null;
            while (latch.getCount() > 0) {
                StringBuilder s = new StringBuilder();
                s.append("Thread requester " + id + " : ");
                System.out.println(s.toString() + "Scanning ...");
                listResultRequest.putResult(s.toString() + "Scanning ...");
                if (bufferRequest.canConsum() && (dataToConsum = bufferRequest.consum()) != null) {
                    System.out.println(s.toString() + "Found data -> " + dataToConsum + " -> perform an HTTP call using socket.");
                    listResultRequest.putResult(s.toString() + "Found data -> " + dataToConsum  + " -> perform an HTTP call using socket.");
                    try {
                        s.append(SocketHttp.getSocketHttp().execute(host, path).get());
                    } catch (InterruptedException | ExecutionException e) {
                        s.append(e.getMessage());
                    } finally {
                        String str = s.toString();
                        System.out.println(str);
                        listResultRequest.putResult(str);
                        latch.countDown();
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {}
            }
        }
    }
    
    private class RunnableTask implements Runnable {
        private BufferRequest bufferRequest = BufferRequest.getBufferRequest();
        private ListResultRequest listResultRequest = ListResultRequest.getListResultRequest();
        private Integer id;
        private CountDownLatch latch;

        public RunnableTask(Integer newId, CountDownLatch newLatch) {
            id = newId;
            latch = newLatch;
        }

        @Override
        public void run() {
            if (latch.getCount() > 0) {
                String str = "Thread builder " + id + " : Generate a HTTP GET call.";
                System.out.println(str);
                bufferRequest.product(str);
                listResultRequest.putResult(str);
                latch.countDown();
            }
        }
    }
    
    public Future<List<String>> execute(Integer numberCall) {
        BufferRequest.getBufferRequest().init();
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        CountDownLatch latchAskHttp = new CountDownLatch(numberCall);
        CountDownLatch latchRequester = new CountDownLatch(numberCall);

        try {
            /* Launch all requesters that will scout */
            IntStream.rangeClosed(1, 4).forEach(i -> {
                executorService.execute(new RunnableTaskRequester(i, latchRequester));
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ignore) {}
            });
            /* Launch adding in desync way */
            IntStream.rangeClosed(1, numberCall).forEach(i -> {
                executorService.execute(new RunnableTask(i, latchAskHttp));
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ignore) {}
            });
            /* Handling stop */
            if (latchAskHttp.await(10, TimeUnit.SECONDS) && latchRequester.await(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            return CompletableFuture.completedFuture(ListResultRequest.getListResultRequest().getAll());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        } finally {
            executorService.shutdown();
        }
    }
}