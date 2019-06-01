package cn.gl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuyTicket {

    public static void main(String[] args) throws InterruptedException {
        String ticketUrl = "http://localhost:8770/ticket";
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
//        List<String> uris = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            uris.add(ticketUrl);
//        }
        Runnable runnable = ()->{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ticketUrl))
                    .build();
            try {
                HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
                String body = res.body();
                System.out.println(body);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 20; i++) {
            executorService.execute(runnable);
        }
        executorService.shutdown();


//        CompletableFuture.allOf(uris.stream()
//                .map(URI::create)
//                .map(HttpRequest::newBuilder)
//                .map(reqBuilder -> reqBuilder.build())
//                .map(request -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
//                .toArray(CompletableFuture[]::new)
//        ).join();


//        uris.stream()
//                .map(URI::create)
//                .map(HttpRequest::newBuilder)
//                .map(reqBuilder -> reqBuilder.build())
//                .map(request -> {
//                    try {
//                        return client.send(request, HttpResponse.BodyHandlers.ofString());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    return "send err";
//                }).forEach(System.out::println);

    }
}
