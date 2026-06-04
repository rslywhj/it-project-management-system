package com.pm.lowcode.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.util.UserContext;
import com.pm.lowcode.domain.FormDefinition;
import com.pm.lowcode.domain.FormInstance;
import com.pm.lowcode.domain.ProcessInstanceRef;
import com.pm.lowcode.dto.*;
import com.pm.lowcode.mapper.FormDefinitionMapper;
import com.pm.lowcode.mapper.FormInstanceMapper;
import com.pm.lowcode.mapper.ProcessInstanceRefMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LowcodeService {

    private final FormDefinitionMapper formDefinitionMapper;
    private final FormInstanceMapper formInstanceMapper;
    private final ProcessInstanceRefMapper processInstanceRefMapper;

    // ==================== 表单定义管理 ====================

    @Transactional
    @RequirePermission("system:manage")
    public FormDefinitionVO createFormDefinition(FormDefinitionRequest request) {
        // 检查 formKey + version 唯一性
        Long exists = formDefinitionMapper.selectCount(
                new LambdaQueryWrapper<FormDefinition>()
                        .eq(FormDefinition::getFormKey, request.getFormKey())
                        .eq(FormDefinition::getVersion, 1));
        if (exists > 0) {
            throw new BusinessException(1080, "表单标识已存在");
        }

        FormDefinition form = new FormDefinition();
        BeanUtils.copyProperties(request, form);
        form.setVersion(1);
        form.setStatus("active");
        formDefinitionMapper.insert(form);
        return toFormDefinitionVO(form);
    }

    public PageResult<FormDefinitionVO> listFormDefinitions(int page, int size, Long projectId, String keyword) {
        LambdaQueryWrapper<FormDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), FormDefinition::getName, keyword)
                .eq(projectId != null, FormDefinition::getProjectId, projectId)
                .eq(FormDefinition::getStatus, "active")
                .orderByDesc(FormDefinition::getCreatedAt);

        Page<FormDefinition> result = formDefinitionMapper.selectPage(new Page<>(page, size), wrapper);
        List<FormDefinitionVO> voList = result.getRecords().stream()
                .map(this::toFormDefinitionVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public FormDefinitionVO getFormDefinition(Long formId) {
        FormDefinition form = formDefinitionMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException(1081, "表单定义不存在");
        }
        return toFormDefinitionVO(form);
    }

    public FormDefinitionVO getFormDefinitionByKey(String formKey) {
        FormDefinition form = formDefinitionMapper.selectOne(
                new LambdaQueryWrapper<FormDefinition>()
                        .eq(FormDefinition::getFormKey, formKey)
                        .eq(FormDefinition::getStatus, "active")
                        .orderByDesc(FormDefinition::getVersion)
                        .last("LIMIT 1"));
        if (form == null) {
            throw new BusinessException(1081, "表单定义不存在");
        }
        return toFormDefinitionVO(form);
    }

    @Transactional
    @RequirePermission("system:manage")
    public FormDefinitionVO updateFormDefinition(Long formId, FormDefinitionRequest request) {
        FormDefinition form = formDefinitionMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException(1081, "表单定义不存在");
        }
        form.setName(request.getName());
        form.setSchemaJson(request.getSchemaJson());
        form.setBindableEntity(request.getBindableEntity());
        formDefinitionMapper.updateById(form);
        return toFormDefinitionVO(form);
    }

    @Transactional
    @RequirePermission("system:manage")
    public FormDefinitionVO newVersion(Long formId, FormDefinitionRequest request) {
        FormDefinition oldForm = formDefinitionMapper.selectById(formId);
        if (oldForm == null) {
            throw new BusinessException(1081, "表单定义不存在");
        }

        // 将旧版本标记为废弃
        oldForm.setStatus("deprecated");
        formDefinitionMapper.updateById(oldForm);

        // 创建新版本
        FormDefinition newForm = new FormDefinition();
        newForm.setFormKey(oldForm.getFormKey());
        newForm.setName(request.getName() != null ? request.getName() : oldForm.getName());
        newForm.setSchemaJson(request.getSchemaJson() != null ? request.getSchemaJson() : oldForm.getSchemaJson());
        newForm.setVersion(oldForm.getVersion() + 1);
        newForm.setBindableEntity(request.getBindableEntity() != null ? request.getBindableEntity() : oldForm.getBindableEntity());
        newForm.setProjectId(oldForm.getProjectId());
        newForm.setStatus("active");
        formDefinitionMapper.insert(newForm);
        return toFormDefinitionVO(newForm);
    }

    @Transactional
    @RequirePermission("system:manage")
    public void deleteFormDefinition(Long formId) {
        formDefinitionMapper.deleteById(formId);
    }

    // ==================== 表单实例管理 ====================

    @Transactional
    public FormInstanceVO submitFormInstance(FormInstanceRequest request) {
        FormDefinition formDef = formDefinitionMapper.selectById(request.getFormDefinitionId());
        if (formDef == null) {
            throw new BusinessException(1081, "表单定义不存在");
        }

        FormInstance instance = new FormInstance();
        instance.setFormDefinitionId(request.getEntityId());
        instance.setEntityId(request.getEntityId());
        instance.setEntityType(request.getEntityType());
        instance.setDataJson(request.getDataJson());
        instance.setSubmittedBy(UserContext.getUserId());
        instance.setSubmittedAt(LocalDateTime.now());
        formInstanceMapper.insert(instance);

        return toFormInstanceVO(instance, formDef);
    }

    public FormInstanceVO getFormInstance(Long instanceId) {
        FormInstance instance = formInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException(1082, "表单实例不存在");
        }
        FormDefinition formDef = formDefinitionMapper.selectById(instance.getFormDefinitionId());
        return toFormInstanceVO(instance, formDef);
    }

    public List<FormInstanceVO> listFormInstances(String entityType, Long entityId) {
        List<FormInstance> instances = formInstanceMapper.selectList(
                new LambdaQueryWrapper<FormInstance>()
                        .eq(FormInstance::getEntityType, entityType)
                        .eq(FormInstance::getEntityId, entityId)
                        .orderByDesc(FormInstance::getCreatedAt));
        return instances.stream().map(inst -> {
            FormDefinition formDef = formDefinitionMapper.selectById(inst.getFormDefinitionId());
            return toFormInstanceVO(inst, formDef);
        }).collect(Collectors.toList());
    }

    // ==================== 流程管理（简化实现，预留 Flowable 集成接口） ====================

    @Transactional
    public ProcessInstanceVO startProcess(ProcessStartRequest request) {
        // TODO: 集成 Flowable 引擎启动流程
        // 当前为简化实现，记录流程实例关联

        ProcessInstanceRef ref = new ProcessInstanceRef();
        ref.setProcessInstanceId("mock-" + System.currentTimeMillis()); // TODO: 替换为 Flowable 返回的实例ID
        ref.setEntityId(request.getEntityId());
        ref.setEntityType(request.getEntityType());
        ref.setStatus("running");
        ref.setStartedBy(UserContext.getUserId());
        ref.setStartedAt(LocalDateTime.now());
        processInstanceRefMapper.insert(ref);

        // 如果有表单数据，保存表单实例
        if (StringUtils.hasText(request.getFormDataJson()) && request.getFormDefinitionId() != null) {
            FormInstance instance = new FormInstance();
            instance.setFormDefinitionId(request.getFormDefinitionId());
            instance.setEntityId(request.getEntityId());
            instance.setEntityType(request.getEntityType());
            instance.setDataJson(request.getFormDataJson());
            instance.setProcessInstanceId(ref.getProcessInstanceId());
            instance.setSubmittedBy(UserContext.getUserId());
            instance.setSubmittedAt(LocalDateTime.now());
            formInstanceMapper.insert(instance);
        }

        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setProcessInstanceId(ref.getProcessInstanceId());
        vo.setProcessDefinitionKey(request.getProcessDefinitionKey());
        vo.setEntityId(request.getEntityId());
        vo.setEntityType(request.getEntityType());
        vo.setStatus("running");
        vo.setStartedBy(UserContext.getUserId());
        vo.setStartedAt(LocalDateTime.now());
        vo.setCurrentTasks(new ArrayList<>());
        return vo;
    }

    @Transactional
    public void completeTask(String taskId, TaskCompleteRequest request) {
        // TODO: 集成 Flowable 引擎完成任务
        // 当前为简化实现

        log.info("Completing task {} with action {}", taskId, request.getAction());

        // 保存表单数据
        if (StringUtils.hasText(request.getFormDataJson())) {
            // 查找关联的流程实例
            ProcessInstanceRef ref = processInstanceRefMapper.selectOne(
                    new LambdaQueryWrapper<ProcessInstanceRef>()
                            .eq(ProcessInstanceRef::getStatus, "running")
                            .last("LIMIT 1"));

            if (ref != null) {
                FormInstance instance = new FormInstance();
                instance.setEntityId(ref.getEntityId());
                instance.setEntityType(ref.getEntityType());
                instance.setTaskId(taskId);
                instance.setDataJson(request.getFormDataJson());
                instance.setSubmittedBy(UserContext.getUserId());
                instance.setSubmittedAt(LocalDateTime.now());
                formInstanceMapper.insert(instance);
            }
        }
    }

    public ProcessInstanceVO getProcessInstance(String processInstanceId) {
        ProcessInstanceRef ref = processInstanceRefMapper.selectOne(
                new LambdaQueryWrapper<ProcessInstanceRef>()
                        .eq(ProcessInstanceRef::getProcessInstanceId, processInstanceId));
        if (ref == null) {
            throw new BusinessException(1083, "流程实例不存在");
        }

        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setProcessInstanceId(ref.getProcessInstanceId());
        vo.setEntityId(ref.getEntityId());
        vo.setEntityType(ref.getEntityType());
        vo.setStatus(ref.getStatus());
        vo.setStartedBy(ref.getStartedBy());
        vo.setStartedAt(ref.getStartedAt());
        vo.setCurrentTasks(new ArrayList<>());
        return vo;
    }

    public List<ProcessInstanceVO> listProcessInstances(String entityType, Long entityId) {
        List<ProcessInstanceRef> refs = processInstanceRefMapper.selectList(
                new LambdaQueryWrapper<ProcessInstanceRef>()
                        .eq(ProcessInstanceRef::getEntityType, entityType)
                        .eq(ProcessInstanceRef::getEntityId, entityId)
                        .orderByDesc(ProcessInstanceRef::getStartedAt));
        return refs.stream().map(ref -> {
            ProcessInstanceVO vo = new ProcessInstanceVO();
            vo.setProcessInstanceId(ref.getProcessInstanceId());
            vo.setEntityId(ref.getEntityId());
            vo.setEntityType(ref.getEntityType());
            vo.setStatus(ref.getStatus());
            vo.setStartedBy(ref.getStartedBy());
            vo.setStartedAt(ref.getStartedAt());
            vo.setCurrentTasks(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());
    }

    // ==================== VO 转换 ====================

    private FormDefinitionVO toFormDefinitionVO(FormDefinition form) {
        FormDefinitionVO vo = new FormDefinitionVO();
        BeanUtils.copyProperties(form, vo);
        return vo;
    }

    private FormInstanceVO toFormInstanceVO(FormInstance instance, FormDefinition formDef) {
        FormInstanceVO vo = new FormInstanceVO();
        BeanUtils.copyProperties(instance, vo);
        if (formDef != null) {
            vo.setFormName(formDef.getName());
            vo.setSchemaJson(formDef.getSchemaJson());
        }
        return vo;
    }
}
