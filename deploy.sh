#!/bin/bash
set -euo pipefail

#==========================================
#  IT项目管理系统 - 生产级部署脚本
#==========================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置
INSTALL_DIR="/opt/pm-system"
REPO_URL="https://github.com/rslywhj/it-project-management-system.git"
BRANCH="${DEPLOY_BRANCH:-main}"
HEALTH_CHECK_TIMEOUT=180  # 健康检查超时（秒）
HEALTH_CHECK_INTERVAL=5   # 检查间隔（秒）
BACKUP_DIR="/opt/pm-backups"
LOG_FILE="/var/log/pm-deploy-$(date +%Y%m%d_%H%M%S).log"

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1" | tee -a "$LOG_FILE"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1" | tee -a "$LOG_FILE"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

log_step() {
    echo -e "\n${BLUE}========================================${NC}" | tee -a "$LOG_FILE"
    echo -e "${BLUE}  $1${NC}" | tee -a "$LOG_FILE"
    echo -e "${BLUE}========================================${NC}\n" | tee -a "$LOG_FILE"
}

# 错误处理
error_exit() {
    log_error "$1"
    log_error "部署失败，请检查日志: $LOG_FILE"
    exit 1
}

# 回滚函数
rollback() {
    log_warn "开始回滚..."
    if [ -d "${BACKUP_DIR}/latest" ]; then
        cd "$INSTALL_DIR"
        docker compose down 2>/dev/null || true
        cp -r "${BACKUP_DIR}/latest/"* "$INSTALL_DIR/"
        docker compose up -d
        log_info "回滚完成"
    else
        log_error "没有可用的备份，无法回滚"
    fi
    exit 1
}

# 设置回滚陷阱
trap 'rollback' ERR

#==========================================
#  1. 环境检查
#==========================================
log_step "1/8 环境检查"

# 检查是否为root用户
if [ "$(id -u)" -ne 0 ]; then
    error_exit "请使用root用户或sudo运行此脚本"
fi

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    error_exit "Docker 未安装。请先手动安装 Docker：
    Ubuntu: apt-get install docker.io
    CentOS: yum install docker
    或参考官方文档: https://docs.docker.com/engine/install/"
fi

# 检查Docker版本（最低要求20.10）
DOCKER_VERSION=$(docker --version | grep -oP '\d+\.\d+' | head -1)
if [ "$(echo "$DOCKER_VERSION < 20.10" | bc -l)" -eq 1 ]; then
    error_exit "Docker版本过低 ($DOCKER_VERSION)，最低要求 20.10"
fi

# 检查Docker Compose
if ! docker compose version &> /dev/null; then
    error_exit "Docker Compose V2 未安装。请参考: https://docs.docker.com/compose/install/"
fi

# 检查Docker服务状态
if ! systemctl is-active --quiet docker; then
    log_warn "Docker服务未启动，正在启动..."
    systemctl start docker
    systemctl enable docker
fi

# 检查磁盘空间（至少需要5GB）
AVAILABLE_SPACE=$(df -BG / | awk 'NR==2 {print $4}' | sed 's/G//')
if [ "$AVAILABLE_SPACE" -lt 5 ]; then
    error_exit "磁盘空间不足: ${AVAILABLE_SPACE}G 可用，至少需要5GB"
fi

# 检查端口占用
check_port() {
    local port=$1
    local service=$2
    if ss -tlnp | grep -q ":${port} "; then
        error_exit "端口 ${port} 已被占用（${service}需要此端口）"
    fi
}

check_port 80 "frontend/nginx"
# 以下端口已改为内部暴露，不需要检查宿主机端口
# check_port 8080 "backend"
# check_port 3306 "mysql"
# check_port 6379 "redis"
# check_port 9000 "minio"
# check_port 9001 "minio-console"

log_info "环境检查通过"
log_info "  Docker版本: $(docker --version)"
log_info "  Compose版本: $(docker compose version)"
log_info "  可用磁盘: ${AVAILABLE_SPACE}G"

#==========================================
#  2. 代码拉取/更新
#==========================================
log_step "2/8 代码拉取/更新"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

if [ -d "$INSTALL_DIR" ]; then
    cd "$INSTALL_DIR"

    # 备份当前版本
    BACKUP_NAME="backup_$(date +%Y%m%d_%H%M%S)"
    log_info "备份当前版本到 ${BACKUP_DIR}/${BACKUP_NAME}"
    cp -r "$INSTALL_DIR" "${BACKUP_DIR}/${BACKUP_NAME}"

    # 保留最近5个备份
    cd "$BACKUP_DIR"
    ls -dt backup_* | tail -n +6 | xargs rm -rf 2>/dev/null || true
    cd "$INSTALL_DIR"

    # 拉取最新代码
    log_info "拉取最新代码 (branch: $BRANCH)"
    git fetch origin
    git checkout "$BRANCH"
    git pull origin "$BRANCH"

    # 更新软链接到最新备份
    ln -sfn "${BACKUP_DIR}/${BACKUP_NAME}" "${BACKUP_DIR}/latest"
else
    log_info "首次部署，克隆仓库"
    git clone -b "$BRANCH" "$REPO_URL" "$INSTALL_DIR"
    cd "$INSTALL_DIR"
fi

CURRENT_COMMIT=$(git rev-parse --short HEAD)
log_info "当前版本: $CURRENT_COMMIT"

#==========================================
#  3. 环境配置
#==========================================
log_step "3/8 环境配置"

if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        cp .env.example .env
    else
        error_exit ".env.example 文件不存在"
    fi

    # 生成随机密码（只含字母数字，避免sed分隔符冲突）
    MYSQL_ROOT_PASS=$(openssl rand -hex 16)
    MYSQL_PASS=$(openssl rand -hex 16)
    REDIS_PASS=$(openssl rand -hex 16)
    JWT_SECRET=$(openssl rand -hex 32)
    MINIO_USER="pm_minio_$(openssl rand -hex 4)"
    MINIO_PASS=$(openssl rand -hex 16)

    sed -i "s|your_mysql_root_password|${MYSQL_ROOT_PASS}|g" .env
    sed -i "s|your_mysql_password|${MYSQL_PASS}|g" .env
    sed -i "s|your_redis_password|${REDIS_PASS}|g" .env
    sed -i "s|your_jwt_secret_key_at_least_32_chars|${JWT_SECRET}|g" .env
    sed -i "s|minioadmin|${MINIO_USER}|g" .env
    sed -i "s|your_minio_password|${MINIO_PASS}|g" .env

    log_info "环境配置已生成"
    log_warn "=========================================="
    log_warn "  请妥善保管以下凭据（仅显示一次）"
    log_warn "=========================================="
    log_warn "  MySQL Root密码: ${MYSQL_ROOT_PASS}"
    log_warn "  MySQL应用密码: ${MYSQL_PASS}"
    log_warn "  Redis密码: ${REDIS_PASS}"
    log_warn "  MinIO用户: ${MINIO_USER}"
    log_warn "  MinIO密码: ${MINIO_PASS}"
    log_warn "  JWT密钥: ${JWT_SECRET}"
    log_warn "=========================================="
    log_warn "  建议将这些凭据保存到密码管理器"
    log_warn "=========================================="
else
    log_info "环境配置已存在，跳过生成"
    # 验证.env文件包含必要的变量
    REQUIRED_VARS="MYSQL_ROOT_PASSWORD MYSQL_PASSWORD REDIS_PASSWORD JWT_SECRET"
    for var in $REQUIRED_VARS; do
        if ! grep -q "^${var}=" .env; then
            error_exit ".env 文件缺少必要的配置项: ${var}"
        fi
    done
fi

#==========================================
#  4. 停止旧服务
#==========================================
log_step "4/8 停止旧服务"

if docker compose ps --quiet 2>/dev/null | grep -q .; then
    log_info "停止现有服务..."
    docker compose down --timeout 30
    log_info "旧服务已停止"
else
    log_info "没有运行中的服务"
fi

#==========================================
#  5. 构建镜像
#==========================================
log_step "5/8 构建镜像"

log_info "构建后端镜像..."
docker compose build backend --no-cache

log_info "构建前端镜像..."
docker compose build frontend --no-cache

log_info "镜像构建完成"

#==========================================
#  6. 启动服务
#==========================================
log_step "6/8 启动服务"

log_info "启动所有服务..."
docker compose up -d

#==========================================
#  7. 健康检查
#==========================================
log_step "7/8 健康检查"

# 等待服务健康
wait_for_healthy() {
    local service=$1
    local timeout=$2
    local elapsed=0

    log_info "等待 ${service} 服务健康..."

    while [ $elapsed -lt $timeout ]; do
        local status=$(docker inspect --format='{{.State.Health.Status}}' "$(docker compose ps -q "$service")" 2>/dev/null || echo "unknown")

        if [ "$status" = "healthy" ]; then
            log_info "${service} 服务已就绪"
            return 0
        fi

        sleep $HEALTH_CHECK_INTERVAL
        elapsed=$((elapsed + HEALTH_CHECK_INTERVAL))
        echo -n "."
    done

    echo ""
    log_error "${service} 服务健康检查超时"
    docker compose logs "$service" --tail=50
    return 1
}

# 按依赖顺序检查服务
wait_for_healthy mysql 120 || error_exit "MySQL启动失败"
wait_for_healthy redis 60 || error_exit "Redis启动失败"
wait_for_healthy backend 180 || error_exit "后端服务启动失败"
wait_for_healthy frontend 60 || error_exit "前端服务启动失败"

#==========================================
#  8. 部署验证
#==========================================
log_step "8/8 部署验证"

# 获取服务器IP
SERVER_IP=$(hostname -I | awk '{print $1}')
FRONTEND_URL="http://${SERVER_IP}"
BACKEND_URL="http://${SERVER_IP}/api"

# 验证前端
log_info "验证前端服务..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$FRONTEND_URL" 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "200" ]; then
    log_info "前端服务正常 (HTTP $HTTP_CODE)"
else
    log_warn "前端服务返回 HTTP $HTTP_CODE"
fi

# 验证后端API
log_info "验证后端API..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${BACKEND_URL}/health" 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "200" ]; then
    log_info "后端API正常 (HTTP $HTTP_CODE)"
else
    log_warn "后端API返回 HTTP $HTTP_CODE"
fi

# 显示服务状态
log_info "服务状态:"
docker compose ps

# 清除回滚陷阱（部署成功）
trap - ERR

#==========================================
#  部署完成
#==========================================
echo -e "\n${GREEN}=========================================="
echo "  部署成功！"
echo "=========================================="
echo ""
echo "  版本: $CURRENT_COMMIT"
echo "  前端地址: $FRONTEND_URL"
echo "  后端API: ${BACKEND_URL}"
echo "  API文档: http://${SERVER_IP}:8080/swagger-ui.html"
echo ""
echo "  日志文件: $LOG_FILE"
echo ""
echo "  常用命令:"
echo "    查看日志: docker compose logs -f"
echo "    重启服务: docker compose restart"
echo "    停止服务: docker compose down"
echo "    查看状态: docker compose ps"
echo "==========================================${NC}"
