#!/bin/bash

# 测试数据生成脚本
# 用法: ./test-data-gen.sh [行数] [输出文件]

# 默认值
DEFAULT_LINES=1000
DEFAULT_OUTPUT="/tmp/test.log"

# 获取参数
LINES=${1:-$DEFAULT_LINES}
OUTPUT=${2:-$DEFAULT_OUTPUT}

echo "生成测试数据..."
echo "行数: $LINES"
echo "输出文件: $OUTPUT"

# 创建输出目录（如果不存在）
mkdir -p "$(dirname "$OUTPUT")"

# 生成 JSON 日志数据
for i in $(seq 1 $LINES); do
    timestamp=$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")
    level=("INFO" "WARN" "ERROR" "DEBUG")
    random_level=${level[$RANDOM % ${#level[@]}]}

    cat >> "$OUTPUT" << EOF
{"timestamp":"$timestamp","level":"$random_level","thread":"thread-$((RANDOM % 10))","logger":"com.example.service.Service$((RANDOM % 5))","message":"This is log message number $i with some random data: $RANDOM","user_id":$((RANDOM % 1000)),"request_id":"req-$(uuidgen | tr '[:upper:]' '[:lower:]')","duration_ms":$((RANDOM % 5000))}
EOF
done

echo "测试数据生成完成！"
echo "文件路径: $OUTPUT"
echo "文件大小: $(du -h "$OUTPUT" | cut -f1)"
echo "总行数: $(wc -l < "$OUTPUT")"
