#!/bin/bash

mkdir -p ./data/log
sh genlog.sh log 2000 2026-03-20_00:00:00 1 ./data/users.log ./data/log/ 1
