#!/bin/bash

# 快速测试脚本
# 启动 Pulsar 和运行 EOS 应用程序

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 显示帮助
show_help() {
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  start       启动 Pulsar 服务"
    echo "  stop        停止所有服务"
    echo "  gen-data    生成测试数据"
    echo "  build       构建项目"
    echo "  run         运行 EOS 应用（需要先启动 Pulsar）"
    echo "  run-docker  使用 Docker Compose 运行完整测试"
    echo "  logs        查看应用日志"
    echo "  clean       清理数据和日志"
    echo "  help        显示帮助"
    echo ""
    echo "快速测试流程:"
    echo "  1. $0 start      # 启动 Pulsar"
    echo "  2. $0 gen-data   # 生成测试数据"
    echo "  3. $0 build      # 构建项目"
    echo "  4. $0 run        # 运行应用"
}

# 启动 Pulsar
start_pulsar() {
    echo_info "启动 Pulsar 服务..."
    docker-compose up -d pulsar

    echo_info "等待 Pulsar 启动..."
    for i in {1..30}; do
        if docker exec pulsar bin/pulsar-admin brokers healthcheck &>/dev/null; then
            echo_info "Pulsar 已就绪！"
            return 0
        fi
        echo "等待中... ($i/30)"
        sleep 2
    done

    echo_error "Pulsar 启动超时"
    return 1
}

# 停止服务
stop_services() {
    echo_info "停止所有服务..."
    docker-compose down
    echo_info "服务已停止"
}

# 生成测试数据
gen_data() {
    echo_info "生成测试数据..."
    bash "$SCRIPT_DIR/test-data-gen.sh"
}

# 构建项目
build_project() {
    echo_info "构建项目..."
    mvn clean package -DskipTests
    echo_info "构建完成！"
}

# 运行应用（本地）
run_app() {
    INPUT_FILE="${1:-./data/input/test.log}"
    OFFSET_FILE="${2:-./data/offset/offset.json}"

    if [ ! -f "$INPUT_FILE" ]; then
        echo_error "输入文件不存在: $INPUT_FILE"
        echo_info "请先运行: $0 gen-data"
        exit 1
    fi

    echo_info "运行 EOS 应用..."
    echo_info "输入文件: $INPUT_FILE"
    echo_info "偏移量文件: $OFFSET_FILE"

    java -jar target/lab-java-pulsar-eos2-1.0-SNAPSHOT.jar \
        --service-url pulsar://localhost:6650 \
        --topic persistent://public/default/eos-test-topic \
        --input "$INPUT_FILE" \
        --offset "$OFFSET_FILE" \
        --batch-size 100
}

# 使用 Docker Compose 运行
run_docker() {
    echo_info "使用 Docker Compose 运行完整测试..."

    # 确保测试数据存在
    if [ ! -f "./data/input/test.log" ]; then
        gen_data
    fi

    # 构建并运行
    docker-compose up --build
}

# 查看日志
show_logs() {
    if [ -f "./logs/pulsar-eos.log" ]; then
        tail -f ./logs/pulsar-eos.log
    else
        echo_warn "日志文件不存在，尝试查看 Docker 日志..."
        docker-compose logs -f eos-app
    fi
}

# 清理数据
clean_data() {
    echo_info "清理数据和日志..."
    rm -rf ./data/offset/*
    rm -rf ./logs/*
    echo_info "清理完成"
}

# 主逻辑
case "${1:-help}" in
    start)
        start_pulsar
        ;;
    stop)
        stop_services
        ;;
    gen-data)
        gen_data
        ;;
    build)
        build_project
        ;;
    run)
        run_app "$2" "$3"
        ;;
    run-docker)
        run_docker
        ;;
    logs)
        show_logs
        ;;
    clean)
        clean_data
        ;;
    help|*)
        show_help
        ;;
esac
