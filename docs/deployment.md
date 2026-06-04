# IT项目管理系统 - 部署文档

## 目录

- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [环境配置](#环境配置)
- [部署方式](#部署方式)
- [CI/CD 流水线](#cicd-流水线)
- [监控与健康检查](#监控与健康检查)
- [故障排查](#故障排查)
- [回滚方案](#回滚方案)

---

## 环境要求

### 基础环境
- Docker 24.0+
- Docker Compose 2.20+
- Git 2.40+

### 服务器配置（生产环境）
- CPU: 4核+
- 内存: 8GB+
- 磁盘: 100GB+ SSD
- 操作系统: Ubuntu 22.04 LTS / CentOS 8+

### 服务器配置（开发环境）
- CPU: 2核+
- 内存: 4GB+
- 磁盘: 50GB+

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/rslywhj/it-project-management-system.git
cd it-project-management-system
```

### 2. 配置环境变量

```bash
cp .env.example .env
# 编辑 .env 文件，修改数据库密码、JWT密钥等敏感配置
vim .env
```

### 3. 一键启动（开发环境）

```bash
docker-compose up -d
```

### 4. 访问系统

- 前端: http://localhost
- 后端API: http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui.html
- MinIO控制台: http://localhost:9001

---

## 环境配置

### 环境变量说明

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `MYSQL_ROOT_PASSWORD` | MySQL root密码 | root |
| `MYSQL_PORT` | MySQL端口 | 3306 |
| `REDIS_PORT` | Redis端口 | 6379 |
| `MINIO_ROOT_USER` | MinIO管理员用户名 | minioadmin |
| `MINIO_ROOT_PASSWORD` | MinIO管理员密码 | minioadmin |
| `MINIO_API_PORT` | MinIO API端口 | 9000 |
| `MINIO_CONSOLE_PORT` | MinIO控制台端口 | 9001 |
| `SPRING_PROFILES_ACTIVE` | Spring激活的配置文件 | prod |
| `BACKEND_PORT` | 后端服务端口 | 8080 |
| `JWT_SECRET` | JWT签名密钥 | - |
| `FRONTEND_PORT` | 前端服务端口 | 80 |

### 多环境配置

项目支持以下环境配置：

- **开发环境 (dev)**: `application-dev.yml` - 本地开发使用
- **测试环境 (test)**: `application-test.yml` - 自动化测试使用
- **生产环境 (prod)**: `application-prod.yml` - 生产部署使用

---

## 部署方式

### 方式一：Docker Compose 部署（推荐）

#### 开发环境

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

#### 生产环境

```bash
# 使用生产配置启动
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 查看服务状态
docker-compose ps

# 查看资源使用
docker stats
```

### 方式二：单独构建镜像

#### 构建后端镜像

```bash
cd backend
docker build -t pm-backend:latest .
```

#### 构建前端镜像

```bash
cd frontend
docker build -t pm-frontend:latest .
```

### 方式三：本地开发（不用Docker）

#### 启动依赖服务

```bash
# 仅启动 MySQL、Redis、MinIO
docker-compose up -d mysql redis minio
```

#### 启动后端

```bash
cd backend
mvn spring-boot:run -pl pm-boot
```

#### 启动前端

```bash
cd frontend
npm install
npm run dev
```

---

## CI/CD 流水线

### GitHub Actions 工作流

项目配置了完整的 CI/CD 流水线：

```
代码推送 → 构建测试 → Docker镜像构建 → 部署
```

#### 触发条件

| 事件 | 分支 | 动作 |
|------|------|------|
| Push | develop | 构建 + 部署到 Staging |
| Push | main | 构建 + 部署到 Production |
| PR | main | 构建 + 测试 |

#### 配置 Secrets

在 GitHub 仓库设置中配置以下 Secrets：

```
# Staging 环境
STAGING_HOST=your-staging-server-ip
STAGING_USER=deploy
STAGING_SSH_KEY=your-ssh-private-key

# Production 环境
PRODUCTION_HOST=your-production-server-ip
PRODUCTION_USER=deploy
PRODUCTION_SSH_KEY=your-ssh-private-key
```

### 手动触发部署

```bash
# 在服务器上执行
cd /opt/pm-system
git pull origin main
docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
docker system prune -f
```

---

## 监控与健康检查

### 服务健康检查

所有服务都配置了 Docker 健康检查：

```bash
# 查看服务健康状态
docker-compose ps

# 查看特定服务健康详情
docker inspect --format='{{json .State.Health}}' pm-backend
```

### 端点说明

| 服务 | 健康检查端点 | 检查间隔 |
|------|-------------|----------|
| Backend | `GET /actuator/health` | 30s |
| Frontend | `GET /health` | 30s |
| MySQL | `mysqladmin ping` | 10s |
| Redis | `redis-cli ping` | 10s |
| MinIO | `mc ready local` | 10s |

### 日志查看

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend

# 查看最近100行日志
docker-compose logs --tail=100 backend
```

### 资源监控

```bash
# 实时资源使用
docker stats

# 查看磁盘使用
docker system df
```

---

## 故障排查

### 常见问题

#### 1. MySQL 启动失败

**症状**: MySQL 容器反复重启

**排查步骤**:
```bash
# 查看日志
docker-compose logs mysql

# 检查端口占用
netstat -tlnp | grep 3306

# 检查磁盘空间
df -h
```

**解决方案**:
- 检查密码配置是否正确
- 确保 3306 端口未被占用
- 清理磁盘空间或删除旧数据卷：`docker volume rm pm-system_mysql_data`

#### 2. 后端连接数据库失败

**症状**: 后端启动报错 `Communications link failure`

**排查步骤**:
```bash
# 检查 MySQL 是否就绪
docker-compose ps mysql

# 测试数据库连接
docker exec -it pm-mysql mysql -u root -p -e "SHOW DATABASES;"

# 检查网络
docker network inspect pm-system_pm-network
```

**解决方案**:
- 确保 MySQL 健康检查通过
- 检查 `SPRING_DATASOURCE_URL` 配置
- 等待 MySQL 完全启动后再启动后端

#### 3. 前端无法访问后端API

**症状**: 前端页面加载正常，但 API 请求失败

**排查步骤**:
```bash
# 检查后端是否运行
docker-compose ps backend

# 测试后端API
curl http://localhost:8080/actuator/health

# 检查 nginx 配置
docker exec -it pm-frontend nginx -t
```

**解决方案**:
- 检查 nginx.conf 中的代理配置
- 确保后端服务名称为 `backend`
- 检查 CORS 配置

#### 4. MinIO 文件上传失败

**症状**: 文件上传返回错误

**排查步骤**:
```bash
# 检查 MinIO 状态
docker-compose ps minio

# 访问 MinIO 控制台
# http://localhost:9001

# 检查存储桶是否存在
docker exec -it pm-minio mc ls local/
```

**解决方案**:
- 确保 MinIO 已创建 `pm-files` 存储桶
- 检查访问密钥配置
- 确保存储桶权限设置正确

---

## 回滚方案

### Docker 镜像回滚

```bash
# 1. 查看镜像历史
docker images | grep pm-

# 2. 停止当前服务
docker-compose down

# 3. 修改镜像标签
# 编辑 docker-compose.yml，将 image 标签改为之前的版本

# 4. 重新启动
docker-compose up -d
```

### 数据库回滚

```bash
# 1. 停止后端服务
docker-compose stop backend

# 2. 恢复数据库备份
docker exec -i pm-mysql mysql -u root -p pm_business < backup.sql

# 3. 重启服务
docker-compose start backend
```

### 完整回滚流程

```bash
# 1. 记录当前版本
git log --oneline -5

# 2. 回退到上一个版本
git checkout HEAD~1

# 3. 重新部署
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build

# 4. 验证服务
docker-compose ps
curl http://localhost:8080/actuator/health
```

---

## 备份策略

### 数据库备份

```bash
# 创建备份脚本
cat > /opt/pm-system/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/backups/mysql"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

docker exec pm-mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD pm_business > $BACKUP_DIR/pm_business_$DATE.sql

# 保留最近30天备份
find $BACKUP_DIR -name "*.sql" -mtime +30 -delete
EOF

chmod +x /opt/pm-system/backup.sh

# 添加定时任务（每天凌晨2点）
echo "0 2 * * * /opt/pm-system/backup.sh" | crontab -
```

### MinIO 数据备份

```bash
# 使用 mc 客户端备份
mc mirror local/pm-files /opt/backups/minio/pm-files
```

---

## 安全建议

1. **修改默认密码**: 部署前必须修改所有默认密码
2. **使用 HTTPS**: 生产环境配置 SSL 证书
3. **限制端口访问**: 使用防火墙只开放必要端口
4. **定期更新**: 定期更新 Docker 镜像和依赖
5. **日志审计**: 启用访问日志并定期审计
6. **备份验证**: 定期验证备份可恢复性

---

## 联系方式

如有部署问题，请联系运维团队或提交 Issue。
