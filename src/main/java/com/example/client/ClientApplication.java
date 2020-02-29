package com.example.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ClientApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Bean
    public CommandLineRunner run(ClientConfig config) {
        return args -> {
            int num = 10;
            final RestTemplate restTemplate = new RestTemplateBuilder().build();
            final List<Future<Pair<RequestEntity<?>, ResponseEntity<String>>>> list = new ArrayList<>();
            final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


            for (int i = 1; i < num; i++) {
                URI uri = createURI(config, i);
                Future<Pair<RequestEntity<?>, ResponseEntity<String>>> submit = executorService.submit(() -> {
                    RequestEntity<?> request = createRequest(uri);
                    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

                    return new Pair<>(request, response);
                });

                list.add(submit);
            }

            executorService.shutdown();
            log.info("Starting validation");
            for (int i = 0; i < list.size(); i++) {
                validate(list.get(i));
            }
        };
    }

    private void validate(Future<Pair<RequestEntity<?>, ResponseEntity<String>>> pairFuture) {
        try {
            Pair<RequestEntity<?>, ResponseEntity<String>> pair = pairFuture.get();
            RequestEntity<?> request = pair.getRequest();
            ResponseEntity<String> response = pair.getResponse();

            List<String> responseClientID = response.getHeaders().get("client.ID");
            List<String> requestClientID = request.getHeaders().get("client.ID");

            if (responseClientID == null || requestClientID == null
                    || responseClientID.size() != requestClientID.size()
                    || responseClientID.size() != 1
                    || !requestClientID.get(0).equals(responseClientID.get(0))) {
                log.error("Validation failed on request with ClientID {}", request.getUrl().toString());
                throw new RuntimeException("validation failed");
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("future throws error", e);
            throw new RuntimeException("future error");
        }
        log.info("All messages validated");
    }

    private URI createURI(ClientConfig config, int id) {
        return UriComponentsBuilder.newInstance().
                scheme(config.getScheme()).
                host(config.getHost()).
                port(config.getPort()).
                path(config.getApi()).
                build(id);
    }

    private RequestEntity<?> createRequest(URI uri) {

        return RequestEntity.get(uri).
                accept(MediaType.APPLICATION_JSON).
                header("client.ID", UUID.randomUUID().toString()).
                header("client.ThreadName", Thread.currentThread().getName()).
                header("client.SendingTime", LocalDateTime.now().toString())
                .build();
    }
}
