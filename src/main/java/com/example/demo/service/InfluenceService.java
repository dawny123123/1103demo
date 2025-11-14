package com.example.demo.service;

import com.example.demo.dao.InfluenceDAO;
import com.example.demo.entity.Influence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 影响力服务类 - 实现业务逻辑与数据访问的分离
 */
@Service
public class InfluenceService {
    
    // 定义有效的活动类型集合
    private static final Set<String> VALID_TYPES = new HashSet<>(Arrays.asList(
        Influence.TYPE_SA_TRAINING,
        Influence.TYPE_LOGO,
        Influence.TYPE_CASE_STUDY,
        Influence.TYPE_COMPETITOR_ANALYSIS,
        Influence.TYPE_DEMO,
        Influence.TYPE_CONFERENCE_SHARING
    ));
    
    // 定义有效的状态集合
    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
        Influence.STATUS_PLANNED,
        Influence.STATUS_IN_PROGRESS,
        Influence.STATUS_COMPLETED,
        Influence.STATUS_CANCELLED
    ));
    
    // URL格式验证正则表达式
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$",
        Pattern.CASE_INSENSITIVE
    );
    
    // 注入数据访问层
    private final InfluenceDAO influenceDAO;

    @Autowired
    public InfluenceService(InfluenceDAO influenceDAO) {
        this.influenceDAO = influenceDAO;
    }

    /**
     * 创建影响力记录
     * @param influence 影响力记录对象
     * @return 创建成功返回true，失败返回false
     * @throws IllegalArgumentException 参数验证失败时抛出
     */
    public boolean createInfluence(Influence influence) {
        // 验证必填字段
        validateRequiredFields(influence);
        
        // 验证活动类型
        if (!VALID_TYPES.contains(influence.getType())) {
            throw new IllegalArgumentException("无效的活动类型: " + influence.getType());
        }
        
        // 验证状态
        if (!VALID_STATUSES.contains(influence.getStatus())) {
            throw new IllegalArgumentException("无效的状态值: " + influence.getStatus());
        }
        
        // 验证链接格式（如果提供）
        if (influence.getLink() != null && !influence.getLink().trim().isEmpty()) {
            if (!isValidUrl(influence.getLink())) {
                throw new IllegalArgumentException("链接格式不正确");
            }
        }
        
        // 验证名称长度
        if (influence.getName().length() > 200) {
            throw new IllegalArgumentException("名称长度不能超过200字符");
        }
        
        // 验证备注长度
        if (influence.getRemark() != null && influence.getRemark().length() > 2000) {
            throw new IllegalArgumentException("备注长度不能超过2000字符");
        }
        
        // 验证图片数量
        if (influence.getImageUrls() != null && influence.getImageUrls().size() > 10) {
            throw new IllegalArgumentException("最多支持上传10张图片");
        }
        
        return getInfluenceDAO().createInfluence(influence);
    }

    /**
     * 获取影响力记录
     * @param id 记录ID
     * @return 返回影响力记录对象，不存在返回null
     */
    public Influence getInfluence(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        return getInfluenceDAO().getInfluence(id);
    }

    /**
     * 更新影响力记录
     * @param influence 待更新的影响力记录对象
     * @return 更新成功返回true，失败返回false
     * @throws IllegalArgumentException 参数验证失败时抛出
     */
    public boolean updateInfluence(Influence influence) {
        // 验证必填字段
        validateRequiredFields(influence);
        
        // 验证活动类型
        if (!VALID_TYPES.contains(influence.getType())) {
            throw new IllegalArgumentException("无效的活动类型: " + influence.getType());
        }
        
        // 验证状态
        if (!VALID_STATUSES.contains(influence.getStatus())) {
            throw new IllegalArgumentException("无效的状态值: " + influence.getStatus());
        }
        
        // 验证链接格式（如果提供）
        if (influence.getLink() != null && !influence.getLink().trim().isEmpty()) {
            if (!isValidUrl(influence.getLink())) {
                throw new IllegalArgumentException("链接格式不正确");
            }
        }
        
        // 验证名称长度
        if (influence.getName().length() > 200) {
            throw new IllegalArgumentException("名称长度不能超过200字符");
        }
        
        // 验证备注长度
        if (influence.getRemark() != null && influence.getRemark().length() > 2000) {
            throw new IllegalArgumentException("备注长度不能超过2000字符");
        }
        
        // 验证图片数量
        if (influence.getImageUrls() != null && influence.getImageUrls().size() > 10) {
            throw new IllegalArgumentException("最多支持上传10张图片");
        }
        
        return getInfluenceDAO().updateInfluence(influence);
    }

    /**
     * 删除影响力记录
     * @param id 记录ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteInfluence(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        return getInfluenceDAO().deleteInfluence(id);
    }

    /**
     * 根据类型查询影响力记录列表
     * @param type 活动类型
     * @return 返回该类型的所有影响力记录列表
     * @throws IllegalArgumentException 当type无效时抛出
     */
    public List<Influence> getInfluencesByType(String type) {
        // 参数校验
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("活动类型不能为空");
        }
        
        // 验证类型有效性
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("无效的活动类型: " + type);
        }
        
        return getInfluenceDAO().getInfluencesByType(type);
    }

    /**
     * 获取所有影响力记录列表
     * @return 返回所有影响力记录列表，按活动时间降序排列
     */
    public List<Influence> getAllInfluences() {
        return getInfluenceDAO().getAllInfluences();
    }

    /**
     * 验证必填字段
     * @param influence 影响力记录对象
     * @throws IllegalArgumentException 必填字段缺失时抛出
     */
    private void validateRequiredFields(Influence influence) {
        if (influence == null) {
            throw new IllegalArgumentException("影响力记录对象不能为空");
        }
        if (influence.getId() == null || influence.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        if (influence.getName() == null || influence.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("活动名称不能为空");
        }
        if (influence.getType() == null || influence.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("活动类型不能为空");
        }
        if (influence.getStatus() == null || influence.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("状态不能为空");
        }
        if (influence.getEventTime() == null) {
            throw new IllegalArgumentException("活动时间不能为空");
        }
    }

    /**
     * 验证URL格式
     * @param url URL字符串
     * @return 格式正确返回true，否则返回false
     */
    private boolean isValidUrl(String url) {
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * 获取DAO实例（用于子类扩展）
     * @return InfluenceDAO实例
     */
    protected InfluenceDAO getInfluenceDAO() {
        return influenceDAO;
    }
}
