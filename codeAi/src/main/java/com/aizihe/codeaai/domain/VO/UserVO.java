package com.aizihe.codeaai.domain.VO;

import com.aizihe.codeaai.domain.entity.User;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 账号（脱敏后）
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    // ==================== 静态转化方法 ====================

    /**
     * 将 User 实体脱敏转换为 UserVO（安全输出给前端）
     *
     * @param user 原始用户实体
     * @return 脱敏后的 UserVO
     */
    public static UserVO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUserAccount(maskAccount(user.getUserAccount())); // 脱敏账号
        vo.setUserName(user.getUserName());
        vo.setUserAvatar(user.getUserAvatar());
        vo.setUserProfile(user.getUserProfile());
        vo.setUserRole(user.getUserRole());
        // 注意：不设置 password！
        return vo;
    }

    /**
     * 将 UserVO 反向转换为 User 实体（谨慎使用，通常只用于更新非敏感字段）
     *
     * @param vo 用户视图对象
     * @return User 实体（不含密码等敏感信息）
     */
    public static User toEntity(UserVO vo) {
        if (vo == null) {
            return null;
        }

        return User.builder()
                .id(vo.getId())
                .userAccount(vo.getUserAccount())
                .userName(vo.getUserName())
                .userAvatar(vo.getUserAvatar())
                .userProfile(vo.getUserProfile())
                .userRole(vo.getUserRole())
                // 注意：password 不设置，避免覆盖
                .build();
    }

    /**
     * 对账号进行脱敏处理（如手机号/邮箱）
     * 示例：
     * - 手机号: 138****1234
     * - 邮箱: a***@qq.com
     * - 其他: ****1234
     *
     * @param account 账号
     * @return 脱敏后的账号
     */
    private static String maskAccount(String account) {
        if (account == null || account.isEmpty()) {
            return "";
        }

        // 判断是否为手机号（11位数字，以1开头）
        if (account.matches("1[3-9]\\d{9}")) {
            return account.substring(0, 3) + "****" + account.substring(7);
        }

        // 判断是否为邮箱
        if (account.contains("@")) {
            int atIndex = account.indexOf('@');
            if (atIndex > 0) {
                String prefix = account.substring(0, atIndex);
                String suffix = account.substring(atIndex);
                if (prefix.length() <= 2) {
                    return "*".repeat(prefix.length()) + suffix;
                }
                return prefix.charAt(0) + "***" + suffix;
            }
        }

        // 默认：保留最后4位，前面用*代替
        if (account.length() <= 4) {
            return "*".repeat(account.length());
        }
        return "****" + account.substring(account.length() - 4);
    }
}