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

echo "=== 阶段 2: 删除大部分文件，触发 inode 清理 ==="
# 删除除了最后几个文件外的所有文件
# 这会导致 SQL DELETE 语句有超过 256 个占位符
for i in $(seq 1 330); do
    file_num=$(printf "%03d" $i)
    rm -f "$LOG_DIR/app-$file_num.log"
    if [ $((i % 50)) -eq 0 ]; then
        echo "Deleted $i files"
    fi
done

echo "=== 刷新间隔，触发 inode 清理 ==="
sleep 15

echo "=== 创建新文件继续生成日志 ==="
for i in $(seq 351 360); do
    echo "Creating file $i"
    file_num=$(printf "%03d" $i)
    echo "Log entry from file app-$file_num.log at $(date)" >> "$LOG_DIR/app-$file_num.log"
    sleep 2
done

sleep 300
