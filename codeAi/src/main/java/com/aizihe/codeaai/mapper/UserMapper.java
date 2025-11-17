package com.aizihe.codeaai.mapper;

import com.mybatisflex.core.BaseMapper;
import com.aizihe.codeaai.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 映射层。
 *
 * @author zhuge
 * @since yyyy-MM-dd
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
