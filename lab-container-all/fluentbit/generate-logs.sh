#!/bin/sh

# 等待 fluent-bit 启动
sleep 5

LOG_DIR="/var/log/test-logs"
mkdir -p "$LOG_DIR"

echo "=== 阶段 1: 创建大量日志文件 (300+ 个文件) ==="
for i in $(seq 1 350); do
    echo "Creating file $i of 350"
    file_num=$(printf "%03d" $i)
    echo "Log entry from file app-$file_num.log at $(date)" >> "$LOG_DIR/app-$file_num.log"
done

echo "=== 等待 fluent-bit 跟踪所有文件 ==="
sleep 10
echo "=== 完成 fluent-bit 跟踪所有文件 ==="