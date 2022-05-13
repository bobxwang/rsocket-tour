package com.bob.rsocket.client.controller;

import com.bob.rsocket.model.StockModel;
import com.bob.rsocket.model.StockModelReq;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wangx
 * @date: 2022-05-12 14:43
 * @description:
 */
@RestController
public class StockRestController {

    @Autowired
    private RSocketRequester rSocketRequester;

    @GetMapping(value = "/feeds", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<StockModel> feeds(@RequestParam String code) {

        return rSocketRequester.route("feedStockModel").data(new StockModelReq(code))
                .retrieveFlux(StockModel.class);
    }
}
