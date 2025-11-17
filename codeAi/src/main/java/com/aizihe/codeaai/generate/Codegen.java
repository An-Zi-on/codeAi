package com.aizihe.codeaai.generate;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Codegen {
    public static final String[] TABLE_NAME = {"user"};

    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/an_ai_code?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfigUseStyle1();
        //GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle1() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.setBasePackage("com.aizihe.codeaAI.result");

        //设置表前缀和只生成哪些表
        globalConfig.setGenerateTable(TABLE_NAME);

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityJdkVersion(17);

        //启用 Mapper 生成
        globalConfig.setMapperGenerateEnable(true);
        //启用 Service 生成
        globalConfig.enableService();
        //启用 ServiceImpl 生成
        globalConfig.enableServiceImpl();
        //启用 Controller 生成
        globalConfig.enableController();
        //启用 MapperXml 生成
        globalConfig.enableMapperXml();
        //设置逻辑删除
        globalConfig.setLogicDeleteColumn("isDelete");
        //设置日期格式
        globalConfig.setSince("yyyy-MM-dd");
        //
        //可以单独配置某个列
        //ColumnConfig columnConfig = new ColumnConfig();
        //columnConfig.setColumnName("");
        //columnConfig.setLarge(true);
        //columnConfig.setLogicDelete(true);
        //columnConfig.setVersion(true);
        //globalConfig.setColumnConfig("tb_account", columnConfig);

        return globalConfig;
    }

    public static GlobalConfig createGlobalConfigUseStyle2() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("com.test");

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setTablePrefix("tb_")
                .setGenerateTable("tb_account", "tb_account_session");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(17);

        //设置生成 mapper
        globalConfig.enableMapper();

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.getStrategyConfig()
                .setColumnConfig("tb_account", columnConfig);

        return globalConfig;
    }
}