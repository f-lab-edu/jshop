#!/bin/bash

cd /usr/local
wget https://github.com/prometheus/mysqld_exporter/releases/download/v0.15.1/mysqld_exporter-0.15.1.linux-amd64.tar.gz
tar -xzvf mysqld_exporter-0.15.1.linux-amd64.tar.gz

cd /usr/local/mysqld_exporter-0.15.1.linux-amd64

cat << EOF > .my.cnf
[client]
user = root
password = 1234
EOF

./mysqld_exporter