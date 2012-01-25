#!/bin/bash
# Startup script for jenkins status monitor

(java -jar ${project.build.finalName}.jar)&