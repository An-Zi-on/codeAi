package com.aizihe.codeaai.ai.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aizihe.codeaai.ai.model.MultiFileWebsiteResult;
import com.aizihe.codeaai.ai.model.SingleFileGenerationResult;
import com.aizihe.codeaai.ai.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 *  代码保存文件工具类
 */
public class CodeFileSaver {

    // 保存在当前项目路径下   如: C:\Users\AnProject\private\codeAi
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + File.separator+"tmp"+File.separator+"code_output";

    /**
     * 保存 HtmlCodeResult
     */
    public static File saveHtmlCodeResult(SingleFileGenerationResult result,Long appId) {
        //构建子目录
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.GEN_TYPE_HTML.getValue(),appId);
        //在子目录下写入生成的 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlContent());
        return new File(baseDirPath);
    }

    /**
     * 保存 MultiFileCodeResult
     */
    public static File saveMultiFileCodeResult(MultiFileWebsiteResult result,Long appId) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.GEN_MULTI_FILE.getValue(),appId);
        writeToFile(baseDirPath, "index.html", result.getHtmlContent());
        writeToFile(baseDirPath, "style.css", result.getCssContent());
        writeToFile(baseDirPath, "script.js", result.getJsContent());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     */
    private static String buildUniqueDir(String bizType,Long appId) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     */
    private static void writeToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
