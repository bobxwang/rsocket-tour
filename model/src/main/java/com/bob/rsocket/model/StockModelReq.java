package com.bob.rsocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wangx
 * @date: 2022-05-12 13:08
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockModelReq {

    private String code;
}
