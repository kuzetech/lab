#!/bin/bash

# 脚本用途：在 logs 文件夹下创建指定数量的日志文件
# 使用方式：./generate-log-files.sh <数量> [前缀名]

set -e

# 参数检查
if [ $# -lt 1 ]; then
    echo "使用方式: $0 <文件数量> [文件前缀名]"
    echo "示例: $0 10 app"
    exit 1
fi

NUM_FILES=$1
PREFIX=${2:-"app"}
LOGS_DIR="./logs"

# 创建 logs 目录（如果不存在）
mkdir -p "$LOGS_DIR"

echo "开始生成 $NUM_FILES 个日志文件..."

for ((i=1; i<=NUM_FILES; i++)); do
    LOG_FILE="$LOGS_DIR/${PREFIX}-${i}.log"
    
    # 生成示例日志内容
    {
        echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 日志文件 $i 已创建"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')] DEBUG: 这是一条测试日志"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 文件名: $(basename $LOG_FILE)"
    } > "$LOG_FILE"
    
    echo "✓ 已创建: $LOG_FILE"
done

echo "完成！共创建了 $NUM_FILES 个日志文件"