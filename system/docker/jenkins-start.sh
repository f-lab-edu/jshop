#!/bin/bash

docker run --name jenkins -d -p 8080:8080 -v /home/jhkim/jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkins