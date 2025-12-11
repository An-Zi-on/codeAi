package com.aizihe.codeaai.config;

import com.aizihe.codeaai.domain.common.DeployConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web 配置类，用于配置静态资源映射
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 使用统一的常量定义，确保路径一致性
        String fileSaveRootDir = DeployConstant.CODE_OUTPUT_ROOT_DIR;
        
        // 规范化路径：将 Windows 路径中的 \ 转换为 /，确保 URL 路径正确
        String normalizedPath = fileSaveRootDir.replace("\\", "/");

        // 确保路径以 / 结尾（addResourceLocations 要求）
        if (!normalizedPath.endsWith("/")) {
            normalizedPath += "/";
        }

        // 映射 URL 路径 /static/** 到外部文件目录
        // file: 协议路径统一使用 / 作为分隔符，跨平台兼容
        String resourceLocation = "file:" + normalizedPath;
        
        registry.addResourceHandler("/static/**")
                .addResourceLocations(resourceLocation);

        // 检查目录是否存在，如果不存在则记录警告
        File dir = new File(fileSaveRootDir);
        if (!dir.exists()) {
            log.warn("静态资源目录不存在: {}, 请确保目录已创建", fileSaveRootDir);
        } else {
            log.info("静态资源映射已配置: /static/** -> {}", resourceLocation);
        }
    }
}