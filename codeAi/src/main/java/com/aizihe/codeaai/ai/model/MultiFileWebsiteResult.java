package com.aizihe.codeaai.ai.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.Instant;

@Description("多文件生成")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiFileWebsiteResult {
    /**
     * index.html 文件内容
     * - 仅包含 HTML 结构
     * - 必须通过 <link rel="stylesheet" href="style.css"> 引入 CSS
     * - 必须在 </body> 前通过 <script src="script.js"></script> 引入 JS
     */
    @Description("index.html 文件内容,仅包含 HTML 结构")
    private String htmlContent;

    /**
     * style.css 文件内容
     * - 包含所有样式规则
     * - 使用 Flexbox 或 Grid 实现响应式布局
     */
    @Description("style.css 文件内容,包含所有样式规则")
    private String cssContent;

    /**
     * script.js 文件内容
     * - 包含所有原生 JavaScript 交互逻辑
     * - 无任何外部库依赖
     */
    @Description("script.js 文件内容,包含所有原生JavaScript交互逻辑")
    private String jsContent;

    // 网站标题（可从 HTML 中提取）
    // private String title;

    @Description("生成时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date generatedAt;
}