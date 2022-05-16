##### Client
    一个客户端联接RScoket的服务端，暴露一个Http接口用来触发
##### Server
    是一个RSocket的服务器

##### RSocket
- Request/Response，即常见的RPC模式
  ``` java
  Mono<Payload> requestResponse(Payload var1)
  ```

- Fire And Forget，无回执数据发送

  > 网络通迅中，存在不需要接收方回执确认的调用模型，如打点采集，日志传输，metrics上报等

  ``` java
  Mono<Void> fireAndForget(Payload var1)
  ```

- Request/Stream，即 Pub/Sub 模式

  > 基于消息的发布订阅模式，也是消息中间件典型的通讯模式。
  >
  > 引入了被压Back Pressure概念，其实就是有限制的Push模型，消息订阅方发起订阅同时告知接下来要请求的消息最大数量，发送方在发送消息时引入计数器，保证推送的消息不超过最大消息数量。

  ``` java
  Flux<Payload> requestStream(Payload var1)
  ```

- Channel 

  > 通道是在连接上建立的一个虚拟的双向通迅管道，通过此管道可发送跟接收特定含义的消息。
  >
  > <font color=green>这跟常规Socket聊天有啥区别呢？</font>
  >
  > 一般来讲一个Channel中消息含义是固定的，处理逻辑也差不多，如果在IM中，可能会有多个Channel，如1对1聊天的Text Channel，群聊的Text Channel，单聊的Image Channel。Channel的创建跟关闭成本非常低，不需要创建物理连接，这也是WebSocket，Redis Stream做不到的，因为它们需要创建新的连接。

  ``` java
  Flux<Payload> requestChannel(Publisher<Payload> var1)
  ```

  考虑到Channel相互通迅中，第一个Message可能会有特殊含义，比如包含元信息，路由信息等，所以后面RSocket又增加了个ResponderRSocket接口，将第一个给独立出来

  ``` java
  interface ResponderRSocket extends RSocket {
    default Flux<Payload> requestChannel(Payload payload, Publisher<Payload> payloads) {
      return requestChannel(payloads);
    }
  }
  ```