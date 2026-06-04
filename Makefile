.PHONY: help dev prod stop clean logs ps backup

# Default target
help:
	@echo "IT Project Management System - Makefile"
	@echo ""
	@echo "Usage:"
	@echo "  make dev        - Start development environment"
	@echo "  make prod       - Start production environment"
	@echo "  make stop       - Stop all services"
	@echo "  make clean      - Stop and remove all containers, volumes"
	@echo "  make logs       - View logs"
	@echo "  make ps         - Show service status"
	@echo "  make backup     - Backup database"
	@echo "  make build      - Build all images"
	@echo "  make restart    - Restart all services"

# Development environment
dev:
	docker-compose up -d

# Production environment
prod:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Stop all services
stop:
	docker-compose down

# Clean everything
clean:
	docker-compose down -v --rmi local
	docker system prune -f

# View logs
logs:
	docker-compose logs -f

# Show service status
ps:
	docker-compose ps

# Build images
build:
	docker-compose build

# Restart services
restart:
	docker-compose restart

# Backup database
backup:
	@mkdir -p backups
	@echo "Backing up database..."
	docker exec pm-mysql mysqldump -u root -p$${MYSQL_ROOT_PASSWORD:-root} pm_business > backups/pm_business_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Backup completed!"

# Restore database
restore:
	@if [ -z "$(FILE)" ]; then echo "Usage: make restore FILE=path/to/backup.sql"; exit 1; fi
	docker exec -i pm-mysql mysql -u root -p$${MYSQL_ROOT_PASSWORD:-root} pm_business < $(FILE)
	@echo "Restore completed!"

# Initialize database only
db-init:
	docker-compose up -d mysql
	@echo "Waiting for MySQL to be ready..."
	@sleep 10
	docker exec -i pm-mysql mysql -u root -p$${MYSQL_ROOT_PASSWORD:-root} pm_business < database/V001__create_schema.sql
	docker exec -i pm-mysql mysql -u root -p$${MYSQL_ROOT_PASSWORD:-root} pm_business < database/V002__init_base_data.sql
	@echo "Database initialized!"
