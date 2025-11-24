package com.aizihe.codeaai.ai.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.time.Instant;

/**
 * 单文件生成结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Description("单文件生成结果类")
public class SingleFileGenerationResult {
    /**
     * 完整的、自包含的 HTML 文件内容。
     * 包含内联 <style> 和 <script>，无外部依赖。
     */
   @Description("完整的、自包含的 HTML 文件内容。")
    private String htmlContent;

    /**
     * 标题
     */
   // private String title;
    /**
     * 时间戳
     */
    @Description("生成时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date generatedAt;
}
