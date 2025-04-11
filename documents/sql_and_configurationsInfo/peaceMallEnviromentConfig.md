# 环境配置命令

创建统一网络组

```
docker network create pm-net
```

Mysql容器

```dockerfile

docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -v /root/mysql/data:/var/lib/mysql \
  -v /root/mysql/conf:/etc/mysql/conf.d \
  -v /root/mysql/init:/docker-entrypoint-initdb.d \
  --restart=always \
  --network pm-net \
  mysql
```

nacos

```
1. 将nacos.sql部署到虚拟机的mysql中
2. 将nacos目录复制到想要的目录下
docker run -d \
--name nacos \
--env-file ./nacos/custom.env \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--restart=always \
--network pm-net \
nacos/nacos-server:v2.1.0-slim
```

seata

```
docker run --name seata \
-p 8099:8099 \
-p 7099:7099 \
-e SEATA_IP=192.168.145.134 \
-v ./seata:/seata-server/resources \
--privileged=true \
--network pm-net \
--restart=always \
-d \
seataio/seata-server:1.5.2
```

sentinel

```
docker run --name sentinel \
  -d \
  --restart=unless-stopped \
  -p 8858:8858 \
  --network pm-net \
  bladex/sentinel-dashboard:1.8.6
```

rabbitMQ

```
docker run \
 -e RABBITMQ_DEFAULT_USER=watergun \
 -e RABBITMQ_DEFAULT_PASS=123456 \
 -v mq-plugins:/plugins \
 --name mq \
 --hostname mq \
 -p 15672:15672 \
 -p 5672:5672 \
 --network pm-net\
 -d \
 rabbitmq:3.8-management
```

mq插件

```
先查看位置
docker volume inspect mq-plugins
将插件放入位置里，然后执行
docker exec -it mq rabbitmq-plugins enable rabbitmq_delayed_message_exchange
```



elasitcSearch

```
docker run -d \
  --name es \
  -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
  -e "discovery.type=single-node" \
  -v es-data:/usr/share/elasticsearch/data \
  -v es-plugins:/usr/share/elasticsearch/plugins \
  --privileged \
  --network pm-net \
  -p 9200:9200 \
  -p 9300:9300 \
  elasticsearch:7.12.1
```

Kibana

```
docker run -d \
--name kibana \
-e ELASTICSEARCH_HOSTS=http://es:9200 \
--network=pm-net \
-p 5601:5601  \
kibana:7.12.1
```

redis

```Docker
docker run -d \
  --name redis6.2.6 \
  -p 6379:6379 \
  -v /data/dockerData/redis/conf/redis.config:/etc/redis/redis.conf \
  -v /data/dockerData/redis/data:/data \
  -v /data/dockerData/redis/logs:/logs \
  redis:6.2.6 \
  redis-server /etc/redis/redis.conf

```

