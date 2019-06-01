# 项目启动顺序
1. 启动 zookeeper
2. 启动 redis
    redis 中应有key 为 ticket 的键,如 ticket:10
3. 启动 eureka-server
4. 启动 ticket-provider
5. 启动 ticket-comsumer
6. 启动 buy-ticket 买票客户端 