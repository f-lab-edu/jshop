#!/bin/bash

docker run -d --name redis-01 -v /etc/redis.conf:/etc/redis/redis.conf -v /data/redis:/data --network jshop --network-alias db_redis redis redis-server /etc/redis/redis.conf
docker run -d --name mysql-master-01 --network jshop --network-alias db_mysql-master -p3306:3306 -v /data/mysql:/var/lib/mysql mysql:8.0