package com.bob.rsocket.server.controller;

import com.bob.rsocket.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangx
 * @date: 2022-05-18 14:33
 * @description:
 */
@Slf4j
@Controller
public class RSocketController {

    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    @MessageMapping("request/response")
    public Message reqRes(Message message) {
        return Message.builder().id(message.getId() + 1).name(message.getName() + "->").build();
    }

    @MessageMapping("request/stream")
    Flux<Message> stream(Message request) {
        log.info("Received stream request [{}]", request);
        return Flux.interval(Duration.ofSeconds(1))
                .map(ix -> Message.builder().id(Instant.now().getEpochSecond()).name("name").build())
                .log();
    }

    @MessageMapping("fire-and-forget")
    public void fireAndForget(Message message) {
        log.info("received fire-and-forget request [{}]", message);
    }

    @MessageMapping("channel")
    public Flux<Message> channel(final Flux<Duration> settings) {
        log.info("Received channel request...");

        return settings
                .doOnNext(setting -> log.info("Channel frequency setting is {} second(s).", setting.getSeconds()))
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .switchMap(setting -> Flux.interval(setting)
                        .map(index -> Message.builder().id(Instant.now().getEpochSecond()).name("name").build()));
    }

    @ConnectMapping("shell-client")
    void connect(RSocketRequester requester, @Payload String client) {
        requester.rsocket().onClose()
                .doFirst(() -> {
                    log.info("Client: [{}] connected", client);
                    CLIENTS.add(requester);
                }).doOnError(error -> {
                    log.error("Channel to client [{}] closed", client);
                }).doFinally(cc -> {
                    CLIENTS.remove(requester);
                    log.info("Client [{}] disconnected", client);
                }).subscribe();

        requester.route("client-status")
                .data("open")
                .retrieveFlux(String.class)
                .doOnNext(s -> log.info("client [{}] free memory [{}]", client, s))
                .subscribe();
    }

    @PreDestroy
    void shutdown() {
        log.info("Want to detaching all remaining clients...");
        CLIENTS.stream().forEach(requester -> requester.rsocket().dispose());
        log.info("Shutting down now ...");
    }
}