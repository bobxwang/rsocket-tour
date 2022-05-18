package com.bob.rsocket.server.controller;

import com.bob.rsocket.model.StockModel;
import com.bob.rsocket.model.StockModelReq;
import com.bob.rsocket.server.repository.ReqResRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author: wangx
 * @date: 2022-05-12 11:12
 * @description:
 */
@Controller
public class StockModelController {

    @Autowired
    private ReqResRepository reqResRepository;

    @MessageMapping("currentStockModel")
    public Mono<StockModel> currentStockModel(StockModelReq req) {
        return reqResRepository.getOne(req.getCode());
    }

    @MessageMapping("feedStockModel")
    public Flux<StockModel> feedStockModel(StockModelReq req) {
        return reqResRepository.getAll(req.getCode());
    }

    @MessageMapping("collectStockModel")
    public Mono<Void> collectMarketData(StockModel stockModel) {
        reqResRepository.add(stockModel);
        return Mono.empty();
    }

    @MessageExceptionHandler
    public Mono<StockModel> handleException(Exception e) {
        return Mono.just(new StockModel(e.getMessage(), 0, LocalDateTime.now()));
    }

}
