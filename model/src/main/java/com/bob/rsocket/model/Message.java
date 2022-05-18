package com.bob.rsocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wangx
 * @date: 2022-05-18 14:37
 * @description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Long id;

    private String name;
}
