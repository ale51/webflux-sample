package com.example.webfluxsample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class SampleController {

    @GetMapping("/nonblocking")
    public Mono<String> nonblocking(){

        WebClient webClient = WebClient
                .builder()
                .baseUrl("http://localhost:8000")
                .build();

        printWithThread(1);

        webClient
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();

        printWithThread(2);

        webClient
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();

        printWithThread(3);

        return Mono.just("Hello, World");
    }

    @GetMapping("/blocking")
    public Mono<String> blocking(){

        RestTemplate restTemplate = new RestTemplate();

        printWithThread(1);

        restTemplate.getForObject("http://localhost:8000", String.class);

        printWithThread(2);

        restTemplate.getForObject("http://localhost:8000", String.class);

        printWithThread(3);

        return Mono.just("Hello, World");
    }

    @GetMapping("/defer-test")
    public Mono<String> defer() throws InterruptedException {

        // just
        System.out.println("just");
        Mono<Long> just = Mono.just(System.currentTimeMillis());
        just.subscribe(SampleController::printWithThread);
        Thread.sleep(1000L);
        just.subscribe(SampleController::printWithThread);

        // defer
        System.out.println("defer");
        Mono<Long> defer = Mono.defer(()->{
            return Mono.just(System.currentTimeMillis());
        });
        defer.subscribe(SampleController::printWithThread);
        Thread.sleep(1000L);
        defer.subscribe(SampleController::printWithThread);

        return Mono.just("Hello, World");
    }

    // print with thread name and time.
    public static void printWithThread(Object obj) {
        System.out.println(System.currentTimeMillis() + ": " + Thread.currentThread().getName() + "\t" + obj);
    }
}
