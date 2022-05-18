package com.bob.shell.client;

import com.bob.rsocket.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * @author: wangx
 * @date: 2022-05-18 14:56
 * @description:
 */
@Slf4j
@ShellComponent
public class RSocketShellClient {

    private final RSocketRequester rSocketRequester;

    @Autowired
    private ClientHandler clientHandler;

    @Autowired
    public RSocketShellClient(RSocketRequester.Builder builder) {
        this.rSocketRequester = builder.connectTcp("127.0.0.1", 7000).block();
    }

    @ShellMethod("Send one request. One response will be printed.")
    public void requestResponse() throws InterruptedException {
        log.info("\nSending one request. Waiting for one response...");
        Message message = this.rSocketRequester
                .route("request/response")
                .data(new Message(Instant.now().getEpochSecond(), "REQUEST"))
                .retrieveMono(Message.class)
                .block();
        log.info("\nResponse was: {}", message);
    }

    @ShellMethod("Send one request. Stream response will be printed.")
    public void requestStream() throws InterruptedException {
        log.info("\nSending one request. Waiting for responses...");
        this.rSocketRequester
                .route("request/stream")
                .data(new Message(Instant.now().getEpochSecond(), "REQUEST"))
                .retrieveFlux(Message.class)
                .subscribe(xx -> log.info("Response received: [{}]", xx));
    }

    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() throws InterruptedException {
        log.info("\nFire-And-Forget. Sending one request. Expect no response (check server log)...");
        this.rSocketRequester
                .route("fire-and-forget")
                .data(new Message(Instant.now().getEpochSecond(), "REQUEST"))
                .send()
                .block();
    }

    @ShellMethod("Stream some settings to the server. Stream of responses will be printed.")
    public void channel() {
        log.info("\n\nChannel (bi-directional streams)\nAsking for a stream of messages.\n\n");

        Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
        Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
        Mono<Duration> setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));
        Flux<Duration> settings = Flux.concat(setting1, setting2, setting3)
                .doOnNext(d -> log.info("\nSending setting for a {}-second interval.\n", d.getSeconds()));

        this.rSocketRequester
                .route("channel")
                .data(settings)
                .retrieveFlux(Message.class)
                .subscribe(message -> log.info("Received: {} \n(Type 's' to stop.)", message));
    }
}