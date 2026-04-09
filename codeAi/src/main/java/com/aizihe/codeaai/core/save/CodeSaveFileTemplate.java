package com.aizihe.codeaai.core.save;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aizihe.codeaai.enums.CodeGenTypeEnum;
import com.aizihe.codeaai.exception.BusinessException;
import com.aizihe.codeaai.exception.ErrorCode;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeSaveFileTemplate <T> {
    // 保存在当前项目路径下   如: C:\Users\AnProject\private\codeAi
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + File.separator+"tmp"+File.separator+"code_output";


    public  final  File   execute(T result , Long appId) {
        //参数校验
        validate(result);
        //获取唯一路径
        String uniqueDir = buildUniqueDir(appId);
        //保存文件交给子类自行实现
        saveFile(result,uniqueDir,appId);
        //返回文件路径
        return new File(uniqueDir);
    }


    protected  void validate(Object parser) {
        if (parser == null ){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"解析代码为空");
        }
    }
    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     */
    protected   String buildUniqueDir(Long appId) {
        String bizType =  getFileType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", bizType, appId);
        //创建完整路径
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 保存文件路径 需要自行实现
     *
     * @param result    解析后的结果
     * @param uniqueDir 文件路径
     */
    protected abstract void saveFile(T result, String uniqueDir, Long appId);

    /**
     * 获取文件类型
     *
     * @return
     */
    protected abstract CodeGenTypeEnum getFileType();

    /**
     * 写入单个文件
     */
    protected  void writeToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
