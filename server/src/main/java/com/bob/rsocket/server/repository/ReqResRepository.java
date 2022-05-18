package com.bob.rsocket.server.repository;

import com.bob.rsocket.model.StockModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author: wangx
 * @date: 2022-05-12 13:10
 * @description:
 */
@Slf4j
@Repository
public class ReqResRepository {

    private static final int Bound = 100;
    private Random random = new Random();

    public Flux<StockModel> getAll(String code) {
        return Flux.fromStream(Stream.generate(() -> getStockModel(code))).log().delayElements(Duration.ofSeconds(1));
    }

    public Mono<StockModel> getOne(String code) {
        return Mono.just(getStockModel(code));
    }

    public void add(StockModel stockModel) {
        log.info("new stock model data [{}]", stockModel);
    }

    public StockModel getStockModel(String code) {
        return new StockModel(code, random.nextInt(Bound), LocalDateTime.now());
    }
}
