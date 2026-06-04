#!/bin/bash
set -e

echo "=========================================="
echo "  IT项目管理系统 - 一键部署脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 1. 环境检查
echo -e "\n${YELLOW}[1/7] 环境检查...${NC}"

if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker 未安装，正在安装...${NC}"
    curl -fsSL https://get.docker.com | sh
    systemctl enable docker && systemctl start docker
fi

if ! command -v docker compose &> /dev/null; then
    echo -e "${RED}Docker Compose 未安装，请手动安装${NC}"
    exit 1
fi

echo -e "${GREEN}Docker 环境检查通过${NC}"

# 2. 代码拉取
echo -e "\n${YELLOW}[2/7] 代码拉取...${NC}"
INSTALL_DIR="/opt/pm-system"

if [ -d "$INSTALL_DIR" ]; then
    cd "$INSTALL_DIR"
    git pull origin main
else
    git clone https://github.com/rslywhj/it-project-management-system.git "$INSTALL_DIR"
    cd "$INSTALL_DIR"
fi

echo -e "${GREEN}代码拉取完成${NC}"

# 3. 环境配置
echo -e "\n${YELLOW}[3/7] 环境配置...${NC}"
if [ ! -f .env ]; then
    cp .env.example .env
    # 生成随机密码（只含字母数字，避免sed分隔符冲突）
    MYSQL_ROOT_PASS=$(openssl rand -hex 16)
    MYSQL_PASS=$(openssl rand -hex 16)
    REDIS_PASS=$(openssl rand -hex 16)
    JWT_SECRET=$(openssl rand -hex 32)
    MINIO_PASS=$(openssl rand -hex 16)

    sed -i "s|your_mysql_root_password|$MYSQL_ROOT_PASS|g" .env
    sed -i "s|your_mysql_password|$MYSQL_PASS|g" .env
    sed -i "s|your_redis_password|$REDIS_PASS|g" .env
    sed -i "s|your_jwt_secret_key_at_least_32_chars|$JWT_SECRET|g" .env
    sed -i "s|your_minio_password|$MINIO_PASS|g" .env

    echo -e "${GREEN}环境配置已生成${NC}"
else
    echo -e "${YELLOW}环境配置已存在，跳过${NC}"
fi

# 4. 数据库初始化
echo -e "\n${YELLOW}[4/7] 数据库初始化...${NC}"
docker compose up -d mysql
sleep 30
echo -e "${GREEN}数据库初始化完成${NC}"

# 5. 启动服务
echo -e "\n${YELLOW}[5/7] 启动服务...${NC}"
docker compose up -d
sleep 60
echo -e "${GREEN}服务启动完成${NC}"

# 6. 验证部署
echo -e "\n${YELLOW}[6/7] 验证部署...${NC}"
docker compose ps

# 7. 输出结果
echo -e "\n${YELLOW}[7/7] 部署完成！${NC}"
echo -e "\n${GREEN}=========================================="
echo "  部署成功！"
echo "=========================================="
echo ""
echo "  前端地址: http://$(hostname -I | awk '{print $1}')"
echo "  后端API: http://$(hostname -I | awk '{print $1}'):8080"
echo "  API文档: http://$(hostname -I | awk '{print $1}'):8080/swagger-ui.html"
echo ""
echo "  默认账号: admin"
echo "  默认密码: Admin@123456"
echo ""
echo "  请登录后立即修改默认密码！"
echo -e "==========================================${NC}"
