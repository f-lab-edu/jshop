#!/bin/bash

cd /usr/local
wget  https://github.com/oliver006/redis_exporter/releases/download/v1.62.0/redis_exporter-v1.62.0.linux-amd64.tar.gz
tar -xzvf redis_exporter-v1.62.0.linux-amd64.tar.gz

/usr/local/redis_exporter-v1.62.0.linux-amd64/redis_exporter --redis.password 1234