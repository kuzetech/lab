#!/bin/bash

# Docker 构建和运行脚本
# 用法: ./docker-build.sh [build|run|push]

set -e

# 配置
IMAGE_NAME="pulsar-eos-processor"
IMAGE_TAG="1.0"
REGISTRY="" # 如果需要推送到私有仓库，设置这里，例如: "registry.example.com/"

FULL_IMAGE_NAME="${REGISTRY}${IMAGE_NAME}:${IMAGE_TAG}"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 构建镜像
build_image() {
    print_info "Building Docker image: ${FULL_IMAGE_NAME}"

    docker build \
        --build-arg BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
        --build-arg VERSION=${IMAGE_TAG} \
        -t ${FULL_IMAGE_NAME} \
        -t ${REGISTRY}${IMAGE_NAME}:latest \
        .

    print_success "Image built successfully: ${FULL_IMAGE_NAME}"

    # 显示镜像信息
    print_info "Image details:"
    docker images | grep ${IMAGE_NAME}
}

# 运行容器
run_container() {
    local file_path=${1:-"/tmp/test.log"}

    print_info "Running container with file: ${file_path}"

    # 检查文件是否存在
    if [ ! -f "$file_path" ]; then
        print_error "File not found: $file_path"
        print_info "Generating test data..."
        ./scripts/test-data-gen.sh 1000 "$file_path"
    fi

    # 运行容器
    docker run --rm \
        --name pulsar-eos-processor \
        --network lab-java-pulsar-eos_pulsar-network \
        -v $(pwd)/logs:/app/logs \
        -v $(dirname $file_path):/data \
        -e PULSAR_SERVICE_URL=pulsar://pulsar:6650 \
        -e MYSQL_HOST=mysql \
        ${FULL_IMAGE_NAME} \
        --file /data/$(basename $file_path) \
        --pulsar-url pulsar://pulsar:6650

    print_success "Container execution completed"
}

# 推送镜像
push_image() {
    if [ -z "$REGISTRY" ]; then
        print_error "Registry not configured. Please set REGISTRY variable in this script."
        exit 1
    fi

    print_info "Pushing image to registry: ${FULL_IMAGE_NAME}"
    docker push ${FULL_IMAGE_NAME}
    docker push ${REGISTRY}${IMAGE_NAME}:latest
    print_success "Image pushed successfully"
}

# 测试镜像
test_image() {
    print_info "Testing Docker image..."

    # 显示帮助信息
    docker run --rm ${FULL_IMAGE_NAME} --help || true

    print_success "Image test completed"
}

# 清理
clean() {
    print_info "Cleaning up Docker images..."
    docker rmi ${FULL_IMAGE_NAME} ${REGISTRY}${IMAGE_NAME}:latest || true
    print_success "Cleanup completed"
}

# 主函数
main() {
    local command=${1:-help}

    case "$command" in
        build)
            build_image
            ;;
        run)
            run_container "$2"
            ;;
        push)
            push_image
            ;;
        test)
            test_image
            ;;
        clean)
            clean
            ;;
        all)
            build_image
            test_image
            print_success "Build and test completed. Use './docker-build.sh run' to run the container"
            ;;
        help|--help|-h)
            echo "Docker Build and Run Script"
            echo ""
            echo "Usage: ./docker-build.sh [command] [options]"
            echo ""
            echo "Commands:"
            echo "  build          Build the Docker image"
            echo "  run [file]     Run the container (default file: /tmp/test.log)"
            echo "  push           Push the image to registry"
            echo "  test           Test the Docker image"
            echo "  clean          Remove Docker images"
            echo "  all            Build and test"
            echo "  help           Show this help message"
            echo ""
            echo "Examples:"
            echo "  ./docker-build.sh build"
            echo "  ./docker-build.sh run /tmp/test.log"
            echo "  ./docker-build.sh all"
            ;;
        *)
            print_error "Unknown command: $command"
            print_info "Use './docker-build.sh help' for usage information"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
