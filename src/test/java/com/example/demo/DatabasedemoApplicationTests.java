package com.example.demo;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class DatabasedemoApplicationTests {
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        documentGeneration();
    }

    /**
     * 文档生成
     */
    @Test
    void documentGeneration() {

        //获取数据源，二选一
//        DataSource dataSource = getDataSource1();
        DataSource dataSource = getDataSource2();

        //生成配置
        EngineConfig engineConfig = EngineConfig.builder()
                //生成文件路径
                .fileOutputDir("D:\\code\\qwe\\doc")
                //打开目录
                .openOutputDir(true)
                // 文件类型(目前支持html、WORD、MD格式，个人体验后还是html格式生成后看起来比较舒服，建议使用)
//                .fileType(EngineFileType.WORD)
                .fileType(EngineFileType.HTML)
//                .fileType(EngineFileType.MD)
                //生成模板实现
                .produceType(EngineTemplateType.freemarker).build();

        //忽略表
        List<String> ignoreTableName = Arrays.asList("test_user","test_group");
        //忽略表前缀
        List<String> ignorePrefix = Arrays.asList("tab_","tbl_");
        //忽略表后缀
        List<String> ignoreSuffix = Arrays.asList("_test","_user");
        ProcessConfig processConfig = ProcessConfig.builder()
                //指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
                //根据名称指定表生成
                .designatedTableName(new ArrayList<>())
                //根据表前缀生成
                .designatedTablePrefix(new ArrayList<>())
                //根据表后缀生成
                .designatedTableSuffix(new ArrayList<>())
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
        //配置
        Configuration config = Configuration.builder()
                //版本
                .version("1.0.0")
                //描述
                .description("数据库设计文档")
                //数据源
                .dataSource(dataSource)
                //生成配置
                .engineConfig(engineConfig)
                //生成配置
                .produceConfig(processConfig)
                .build();
        //执行生成
        new DocumentationExecute(config).execute();
    }

    /**
     * 获取数据源方法1
     * 获取application.properties配置
     * @return
     */
    private DataSource getDataSource1() {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        return dataSource;
    }

    /**
     * 获取数据源方法2
     * @return
     */
    private DataSource getDataSource2() {
        //数据源
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/sk_wms?characterEncoding=UTF-8");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("qwe159852");
        //设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

}
