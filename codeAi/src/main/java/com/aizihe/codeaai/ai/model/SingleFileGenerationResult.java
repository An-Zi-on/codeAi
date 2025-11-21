package com.aizihe.codeaai.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 单文件生成结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleFileGenerationResult {
    /**
     * 完整的、自包含的 HTML 文件内容。
     * 包含内联 <style> 和 <script>，无外部依赖。
     */
    private String htmlContent;

    /**
     * 标题
     */
    private String title;
    /**
     * 时间戳
     */
    private Instant generatedAt;
}
