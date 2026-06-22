-- H2-compatible schema for testing
-- Simplified version of MySQL schema

-- System tables
CREATE TABLE IF NOT EXISTS sys_org (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_name VARCHAR(100) NOT NULL,
    org_code VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    org_type VARCHAR(30),
    sort_order INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(500),
    org_id BIGINT,
    status VARCHAR(20) DEFAULT 'active',
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    remark VARCHAR(500),
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0,
    CONSTRAINT uk_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    data_scope VARCHAR(30) DEFAULT 'self',
    sort_order INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    is_system INT DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0,
    CONSTRAINT uk_role_code UNIQUE (role_code)
);

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL,
    permission_name VARCHAR(200) NOT NULL,
    module VARCHAR(50) NOT NULL,
    operation VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_permission_code UNIQUE (permission_code)
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    project_id BIGINT,
    promotion_unit_id BIGINT,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT uk_user_role_project UNIQUE (user_id, role_id, project_id)
);

CREATE TABLE IF NOT EXISTS sys_dict (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_code VARCHAR(50) NOT NULL,
    dict_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0,
    CONSTRAINT uk_dict_code UNIQUE (dict_code)
);

CREATE TABLE IF NOT EXISTS sys_dict_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_id BIGINT NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    item_value VARCHAR(200),
    sort_order INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_dict_item UNIQUE (dict_id, item_code)
);

CREATE TABLE IF NOT EXISTS sys_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(30) DEFAULT 'system',
    related_entity_type VARCHAR(50),
    related_entity_id BIGINT,
    is_read INT DEFAULT 0,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Business tables
CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(30),
    status VARCHAR(20) DEFAULT 'planning',
    promotion_enabled INT DEFAULT 0,
    start_date DATE,
    end_date DATE,
    org_id BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0,
    CONSTRAINT uk_project_code UNIQUE (project_code)
);

CREATE TABLE IF NOT EXISTS project_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(50),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_project_member UNIQUE (project_id, user_id)
);

CREATE TABLE IF NOT EXISTS requirement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    priority VARCHAR(20) DEFAULT 'medium',
    status VARCHAR(20) DEFAULT 'draft',
    source VARCHAR(30),
    category VARCHAR(30),
    assigned_to BIGINT,
    milestone_id BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    parent_task_id BIGINT,
    requirement_id BIGINT,
    type VARCHAR(30),
    priority VARCHAR(20) DEFAULT 'medium',
    status VARCHAR(20) DEFAULT 'todo',
    assigned_to BIGINT,
    wbs_code VARCHAR(50),
    planned_start DATE,
    planned_end DATE,
    actual_start DATE,
    actual_end DATE,
    completion_rate DECIMAL(5,2) DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS task_dependency (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    depends_on_task_id BIGINT NOT NULL,
    dependency_type VARCHAR(10) DEFAULT 'FS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_task_dependency UNIQUE (task_id, depends_on_task_id)
);

CREATE TABLE IF NOT EXISTS milestone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    planned_date DATE,
    actual_date DATE,
    status VARCHAR(20) DEFAULT 'pending',
    sort_order INT DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS delivery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(30),
    description TEXT,
    milestone_id BIGINT,
    promotion_unit_id BIGINT,
    file_path VARCHAR(500),
    status VARCHAR(20) DEFAULT 'draft',
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP,
    review_comment TEXT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- Test tables
CREATE TABLE IF NOT EXISTS test_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(30),
    iteration VARCHAR(50),
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'draft',
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS test_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    test_plan_id BIGINT,
    title VARCHAR(200) NOT NULL,
    module VARCHAR(100),
    description TEXT,
    precondition TEXT,
    steps TEXT,
    expected_result TEXT,
    priority VARCHAR(20) DEFAULT 'medium',
    type VARCHAR(30),
    requirement_id BIGINT,
    status VARCHAR(20) DEFAULT 'draft',
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS test_execution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_case_id BIGINT NOT NULL,
    test_plan_id BIGINT,
    status VARCHAR(20),
    actual_result TEXT,
    remark TEXT,
    bug_id BIGINT,
    executed_by BIGINT,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bug (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    test_plan_id BIGINT,
    test_case_id BIGINT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    steps_to_reproduce TEXT,
    expected_result TEXT,
    actual_result TEXT,
    severity VARCHAR(20),
    priority VARCHAR(20) DEFAULT 'medium',
    type VARCHAR(30),
    status VARCHAR(20) DEFAULT 'open',
    assigned_to BIGINT,
    resolved_at TIMESTAMP,
    resolution TEXT,
    environment VARCHAR(200),
    requirement_id BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- Risk tables
CREATE TABLE IF NOT EXISTS risk (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(30),
    probability VARCHAR(20),
    impact VARCHAR(20),
    status VARCHAR(20) DEFAULT 'open',
    owner_id BIGINT,
    mitigation_plan TEXT,
    contingency_plan TEXT,
    identified_date DATE,
    review_date DATE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS issue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(30),
    severity VARCHAR(20),
    status VARCHAR(20) DEFAULT 'open',
    assigned_to BIGINT,
    due_date DATE,
    resolution TEXT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- Resource tables
CREATE TABLE IF NOT EXISTS resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    skill_tags VARCHAR(500),
    availability VARCHAR(20) DEFAULT 'available',
    workload_percent INT DEFAULT 0,
    capacity_hours_per_week DECIMAL(5,2) DEFAULT 40,
    remark VARCHAR(500),
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0,
    CONSTRAINT uk_resource_user_project UNIQUE (user_id, project_id)
);

CREATE TABLE IF NOT EXISTS work_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    task_id BIGINT,
    work_date DATE NOT NULL,
    hours DECIMAL(5,2) NOT NULL,
    work_type VARCHAR(30),
    description VARCHAR(500),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Knowledge tables
CREATE TABLE IF NOT EXISTS knowledge_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    category VARCHAR(50),
    tags VARCHAR(500),
    status VARCHAR(20) DEFAULT 'draft',
    view_count INT DEFAULT 0,
    author_id BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(30),
    content TEXT,
    status VARCHAR(20) DEFAULT 'active',
    is_system INT DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

-- Promotion tables
CREATE TABLE IF NOT EXISTS promotion_unit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    org_name VARCHAR(200) NOT NULL,
    org_code VARCHAR(50) NOT NULL,
    responsible_user_id BIGINT,
    status VARCHAR(20) DEFAULT 'pending',
    completion_rate DECIMAL(5,2) DEFAULT 0,
    current_stage_id BIGINT,
    expected_start_date DATE,
    expected_end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    remark VARCHAR(500),
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS promotion_stage_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    is_locked INT DEFAULT 0,
    estimated_days INT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS promotion_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    promotion_unit_id BIGINT NOT NULL,
    stage_template_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    completion_rate DECIMAL(5,2) DEFAULT 0,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin user for testing
INSERT INTO sys_user (id, username, password, real_name, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'active');

-- Insert default roles for testing
INSERT INTO sys_role (id, role_code, role_name, data_scope, is_system) VALUES
(1, 'super_admin', '系统管理员', 'all', 1),
(2, 'project_manager', '项目经理', 'project', 1),
(3, 'developer', '开发人员', 'self', 1);

-- Assign super_admin role to admin user
INSERT INTO sys_user_role (user_id, role_id, status) VALUES (1, 1, 'active');
