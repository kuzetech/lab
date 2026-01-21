#!/bin/bash

# 测试数据生成脚本
# 生成指定行数的测试日志文件

set -e

# 默认配置
OUTPUT_DIR="${OUTPUT_DIR:-./data/input}"
OUTPUT_FILE="${OUTPUT_FILE:-test.log}"
LINE_COUNT="${LINE_COUNT:-1000}"

# 创建输出目录
mkdir -p "$OUTPUT_DIR"

OUTPUT_PATH="$OUTPUT_DIR/$OUTPUT_FILE"

echo "生成测试数据文件..."
echo "输出路径: $OUTPUT_PATH"
echo "行数: $LINE_COUNT"

# 清空或创建文件
> "$OUTPUT_PATH"

# 生成测试数据
for i in $(seq 1 $LINE_COUNT); do
    timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    level=$(( RANDOM % 4 ))
    case $level in
        0) log_level="INFO" ;;
        1) log_level="WARN" ;;
        2) log_level="ERROR" ;;
        3) log_level="DEBUG" ;;
    esac

    # 生成随机消息
    message="Line $i - $log_level - Test message at $timestamp - UUID: $(uuidgen 2>/dev/null || echo "$(date +%s)-$RANDOM")"
    echo "$message" >> "$OUTPUT_PATH"

    # 每100行显示进度
    if [ $((i % 100)) -eq 0 ]; then
        echo "已生成 $i 行..."
    fi
done

echo "测试数据生成完成！"
echo "文件大小: $(ls -lh "$OUTPUT_PATH" | awk '{print $5}')"
echo "行数: $(wc -l < "$OUTPUT_PATH")"
