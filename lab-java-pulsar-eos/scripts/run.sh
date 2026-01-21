#!/bin/bash

# Pulsar EOS 文件处理器 - 运行脚本
# 用法: ./run.sh [file_path] [options]

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Java
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java not found. Please install Java 11 or higher."
        exit 1
    fi
















# 检查 Docker Compose
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null && ! command -v docker &> /dev/null; then
        print_error "Docker Compose not found. Please install Docker and Docker Compose."
        exit 1
    fi
    print_info "Docker Compose is available"
}

# 启动服务
start_services() {
    print_info "Starting Pulsar and MySQL services..."
    docker-compose up -d

    print_info "Waiting for services to be ready..."
    sleep 10

    # 检查服务状态
    if docker-compose ps | grep -q "Up"; then
        print_success "Services are running"
    else
        print_error "Failed to start services"
        docker-compose logs
        exit 1
    fi
}

# 检查服务
check_services() {
    print_info "Checking Pulsar health..."
    if curl -s http://localhost:8080/admin/v2/brokers/health > /dev/null; then
        print_success "Pulsar is healthy"
    else
        print_warning "Pulsar may not be ready yet"
    fi

    print_info "Checking MySQL connection..."
    if docker exec -i mysql-offset-store mysql -upulsar_user -ppulsar_pass -e "SELECT 1" > /dev/null 2>&1; then
        print_success "MySQL is ready"
    else
        print_warning "MySQL may not be ready yet"
    fi
}

# 构建项目
build_project() {
    print_info "Building project..."
    mvn clean package -DskipTests

    if [ -f target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar ]; then
        print_success "Build completed successfully"
    else
        print_error "Build failed"
        exit 1
    fi
}

# 运行应用
run_application() {
    local file_path="$1"
    shift
    local extra_args="$@"

    if [ -z "$file_path" ]; then
        print_error "File path is required"
        echo "Usage: ./run.sh <file_path> [options]"
        exit 1
    fi

    if [ ! -f "$file_path" ]; then
        print_error "File not found: $file_path"
        exit 1
    fi

    print_info "Running application with file: $file_path"
    java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file "$file_path" $extra_args
}

# 主函数
main() {
    echo "======================================"
    echo "  Pulsar EOS File Processor"
    echo "======================================"
    echo ""

    # 检查依赖
    check_java
    check_maven
    check_docker_compose
    echo ""

    # 检查 JAR 是否存在
    if [ ! -f target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar ]; then
        print_warning "JAR not found. Building project..."
        build_project
    else
        print_info "Using existing JAR file"
    fi
    echo ""

    # 检查服务是否运行
    if ! docker-compose ps | grep -q "Up"; then
        print_warning "Services are not running. Starting services..."
        start_services
    else
        print_info "Services are already running"
    fi
    echo ""

    # 检查服务健康
    check_services
    echo ""

    # 运行应用
    if [ $# -eq 0 ]; then
        print_info "Usage: ./run.sh <file_path> [options]"
        print_info "Example: ./run.sh /tmp/test.log"
        print_info "Example: ./run.sh /tmp/test.log --batch-size 50 --topic my-topic"
        exit 0
    fi

    run_application "$@"
}

# 执行主函数
main "$@"
