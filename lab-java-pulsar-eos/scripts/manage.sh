#!/bin/bash

# Pulsar EOS 服务管理脚本
# 用法: ./manage.sh [start|stop|restart|status|logs|clean]

set -e

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

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 启动服务
start_services() {
    print_info "Starting Pulsar and MySQL services..."
    docker-compose up -d





            SELECT



        SELECT


    print_success "Services stopped"
}

# 重启服务
restart_services() {
    print_info "Restarting services..."
    stop_services
    sleep 3
    start_services
}

# 显示状态
show_status() {
    echo ""
    print_info "Service Status:"
    docker-compose ps

    echo ""
    print_info "Pulsar Health:"
    if curl -s http://localhost:8080/admin/v2/brokers/health > /dev/null 2>&1; then
        print_success "Pulsar is healthy"
    else
        print_error "Pulsar is not responding"
    fi

    echo ""
    print_info "MySQL Status:"
    if docker exec -i mysql-offset-store mysql -upulsar_user -ppulsar_pass -e "SELECT 1" > /dev/null 2>&1; then
        print_success "MySQL is ready"

        # 显示数据库统计
        echo ""
        print_info "Database Statistics:"
        docker exec -i mysql-offset-store mysql -upulsar_user -ppulsar_pass pulsar_offset -e "
            SELECT
                COUNT(*) as total_files,
                SUM(CASE WHEN status='COMPLETED' THEN 1 ELSE 0 END) as completed,
                SUM(CASE WHEN status='PROCESSING' THEN 1 ELSE 0 END) as processing,
                SUM(CASE WHEN status='FAILED' THEN 1 ELSE 0 END) as failed,
                SUM(processed_lines) as total_lines_processed
            FROM file_offsets;
        "
    else
        print_error "MySQL is not ready"
    fi
}

# 查看日志
show_logs() {
    local service=$1

    if [ -z "$service" ]; then
        print_info "Showing all logs (Ctrl+C to exit)..."
        docker-compose logs -f
    else
        print_info "Showing logs for $service (Ctrl+C to exit)..."
        docker-compose logs -f "$service"
    fi
}

# 清理数据
clean_data() {
    read -p "Are you sure you want to clean all data? (yes/no): " confirm

    if [ "$confirm" = "yes" ]; then
        print_warning "Stopping services and removing volumes..."
        docker-compose down -v
        print_success "Data cleaned"

        print_info "Removing build artifacts..."
        rm -rf target/
        print_success "Build artifacts removed"
    else
        print_info "Clean cancelled"
    fi
}

# 查看数据库
view_database() {
    print_info "Connecting to MySQL..."
    docker exec -it mysql-offset-store mysql -upulsar_user -ppulsar_pass pulsar_offset
}

# 查看文件偏移量
view_offsets() {
    print_info "File Offsets:"
    docker exec -i mysql-offset-store mysql -upulsar_user -ppulsar_pass pulsar_offset -e "
        SELECT
            file_path,
            CONCAT(ROUND(processed_lines / total_lines * 100, 2), '%') as progress,
            status,
            processed_lines,
            total_lines,
            updated_at
        FROM file_offsets
        ORDER BY updated_at DESC;
    "
}

# 消费 Pulsar 消息
consume_messages() {
    local count=${1:-10}

    print_info "Consuming $count messages from Pulsar topic..."
    docker exec -it pulsar-standalone bin/pulsar-client consume \
        persistent://public/default/log-messages \
        --subscription-name test-sub \
        --num-messages "$count"
}

# 查看主题统计
topic_stats() {
    print_info "Topic Statistics:"
    docker exec -it pulsar-standalone bin/pulsar-admin topics stats \
        persistent://public/default/log-messages
}

# 显示帮助
show_help() {
    echo "Pulsar EOS Service Management Script"
    echo ""
    echo "Usage: ./manage.sh [command] [options]"
    echo ""
    echo "Commands:"
    echo "  start              Start Pulsar and MySQL services"
    echo "  stop               Stop all services"
    echo "  restart            Restart all services"
    echo "  status             Show service status"
    echo "  logs [service]     Show logs (all or specific service: pulsar, mysql)"
    echo "  clean              Clean all data and build artifacts"
    echo "  db                 Connect to MySQL database"
    echo "  offsets            View file processing offsets"
    echo "  consume [count]    Consume messages from Pulsar (default: 10)"
    echo "  stats              Show topic statistics"
    echo "  help               Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./manage.sh start"
    echo "  ./manage.sh logs pulsar"
    echo "  ./manage.sh consume 20"
    echo "  ./manage.sh offsets"
}

# 主函数
main() {
    local command=${1:-help}

    case "$command" in
        start)
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs "$2"
            ;;
        clean)
            clean_data
            ;;
        db)
            view_database
            ;;
        offsets)
            view_offsets
            ;;
        consume)
            consume_messages "$2"
            ;;
        stats)
            topic_stats
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Unknown command: $command"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
