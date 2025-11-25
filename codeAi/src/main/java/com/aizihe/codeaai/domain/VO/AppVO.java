package com.aizihe.codeaai.domain.VO;

import com.aizihe.codeaai.domain.entity.App;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用 实体类。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    private String codeGenType;

    /**
     * 部署标识
     */
    private String deployKey;

    /**
     * 部署时间
     */
    private LocalDateTime deployedTime;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 创建用户id
     */
    @Column("userId")
    private Long userId;

    /**
     * 编辑时间
     */
    @Column("editTime")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    // ==================== VO 转换 ====================

    /**
     * 将 App 实体转换为 AppVO
     *
     * @param app App 实体
     * @return AppVO 视图对象
     */
    public static AppVO toVo(App app) {
        if (app == null) {
            return null;
        }
        return AppVO.builder()
                .id(app.getId())
                .appName(app.getAppName())
                .cover(app.getCover())
                .initPrompt(app.getInitPrompt())
                .codeGenType(app.getCodeGenType())
                .deployKey(app.getDeployKey())
                .deployedTime(app.getDeployedTime())
                .priority(app.getPriority())
                .userId(app.getUserId())
                .editTime(app.getEditTime())
                .createTime(app.getCreateTime())
                .build();
    }

    /**
     * 将 AppVO 转回 App 实体
     *
     * @param vo AppVO
     * @return App 实体
     */
    public static App fromVo(AppVO vo) {
        if (vo == null) {
            return null;
        }
        return App.builder()
                .id(vo.getId())
                .appName(vo.getAppName())
                .cover(vo.getCover())
                .initPrompt(vo.getInitPrompt())
                .codeGenType(vo.getCodeGenType())
                .deployKey(vo.getDeployKey())
                .deployedTime(vo.getDeployedTime())
                .priority(vo.getPriority())
                .userId(vo.getUserId())
                .editTime(vo.getEditTime())
                .createTime(vo.getCreateTime())
                .build();
    }
}
