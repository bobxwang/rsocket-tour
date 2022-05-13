package com.bob.rsocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: wangx
 * @date: 2022-05-12 11:30
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockModel {

    // 股票编码
    private String code;

    // 当前价格
    private int price;

    // 当前时间
    private LocalDateTime now;
}
