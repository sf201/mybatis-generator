package com.zmor.mybatis.generator.bridge;

import com.zmor.mybatis.generator.model.DatabaseConfig;
import com.zmor.mybatis.generator.model.DbType;
import com.zmor.mybatis.generator.model.GeneratorConfig;
import com.zmor.mybatis.generator.plugins.DbRemarksCommentGenerator;
import com.zmor.mybatis.generator.util.ConfigHelper;
import com.zmor.mybatis.generator.util.DbUtil;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The bridge between GUI and the mybatis generator. All the operation to  mybatis generator should proceed through this
 * class
 * <p>
 * Created by Owen on 6/30/16.
 */
public class MybatisGeneratorBridge {

    private static final Logger _LOG = LoggerFactory.getLogger(MybatisGeneratorBridge.class);

    private GeneratorConfig generatorConfig;

    private DatabaseConfig selectedDatabaseConfig;

    private ProgressCallback progressCallback;

    private List<IgnoredColumn> ignoredColumns;

    private List<ColumnOverride> columnOverrides;

    public MybatisGeneratorBridge() {
    }

    public void setGeneratorConfig(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.selectedDatabaseConfig = databaseConfig;
    }

    public void generate() throws Exception {
        Configuration configuration = new Configuration();
        Context context = new Context(ModelType.CONDITIONAL);
        configuration.addContext(context);
        context.addProperty("javaFileEncoding", "UTF-8");
        String connectorLibPath = ConfigHelper.findConnectorLibPath(selectedDatabaseConfig.getDbType());
        _LOG.info("connectorLibPath: {}", connectorLibPath);
        configuration.addClasspathEntry(connectorLibPath);
        // Table configuration
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(generatorConfig.getTableName());
        tableConfig.setDomainObjectName(generatorConfig.getDomainObjectName());
        String schemaName = selectedDatabaseConfig.getSchema();
        if (DbType.MySQL.name().equals(selectedDatabaseConfig.getDbType())) {
            schemaName = selectedDatabaseConfig.getSchema();
        }else if(DbType.Oracle.name().equals(selectedDatabaseConfig.getDbType())) {
            schemaName = selectedDatabaseConfig.getUsername();
        }


        if (!generatorConfig.isUseExampe()) {
            tableConfig.setUpdateByExampleStatementEnabled(false);
            tableConfig.setCountByExampleStatementEnabled(false);
            tableConfig.setDeleteByExampleStatementEnabled(false);
            tableConfig.setSelectByExampleStatementEnabled(false);
        }

        if (DbType.MySQL.name().equals(selectedDatabaseConfig.getDbType())) {
            tableConfig.setSchema(selectedDatabaseConfig.getSchema());
        } else {
            tableConfig.setCatalog(selectedDatabaseConfig.getSchema());
        }
        if (generatorConfig.isUseSchemaPrefix()) {
            if (DbType.MySQL.name().equals(selectedDatabaseConfig.getDbType())) {
                tableConfig.setSchema(selectedDatabaseConfig.getSchema());
            } else if (DbType.Oracle.name().equals(selectedDatabaseConfig.getDbType())) {
                //Oracle的schema为用户名，如果连接用户拥有dba等高级权限，若不设schema，会导致把其他用户下同名的表也生成一遍导致mapper中代码重复
                tableConfig.setSchema(selectedDatabaseConfig.getUsername());
            } else {
                tableConfig.setCatalog(selectedDatabaseConfig.getSchema());
            }
        }
        // 针对 postgresql 单独配置
        if (DbType.valueOf(selectedDatabaseConfig.getDbType()).getDriverClass() == "org.postgresql.Driver") {
            tableConfig.setDelimitIdentifiers(true);
        }

        //添加GeneratedKey主键生成
        if (StringUtils.isNoneEmpty(generatorConfig.getGenerateKeys())) {
            tableConfig.setGeneratedKey(new GeneratedKey(generatorConfig.getGenerateKeys(), selectedDatabaseConfig.getDbType(), true, null));
        }

        // add ignore columns
        if (ignoredColumns != null) {
            ignoredColumns.stream().forEach(ignoredColumn -> {
                tableConfig.addIgnoredColumn(ignoredColumn);
            });
        }
        if (columnOverrides != null) {
            columnOverrides.stream().forEach(columnOverride -> {
                tableConfig.addColumnOverride(columnOverride);
            });
        }
        if (generatorConfig.isUseActualColumnNames()) {
            tableConfig.addProperty("useActualColumnNames", "true");
        }

        if (generatorConfig.isUseTableNameAlias()) {
            tableConfig.setAlias(generatorConfig.getTableName());
        }

        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        // http://www.mybatis.org/generator/usage/mysql.html
        if (DbType.MySQL.name().equals(selectedDatabaseConfig.getDbType())) {
            jdbcConfig.addProperty("nullCatalogMeansCurrent", "true");
        }
        jdbcConfig.setDriverClass(DbType.valueOf(selectedDatabaseConfig.getDbType()).getDriverClass());
        jdbcConfig.setConnectionURL(DbUtil.getConnectionUrlWithSchema(selectedDatabaseConfig));
        jdbcConfig.setUserId(selectedDatabaseConfig.getUsername());
        jdbcConfig.setPassword(selectedDatabaseConfig.getPassword());
        // java model
        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
        if (generatorConfig.isUseSchemaPackage()) {
            //使用schema名做为包名
            modelConfig.setTargetPackage(generatorConfig.getModelPackage() + "." + schemaName);
        } else {
            modelConfig.setTargetPackage(generatorConfig.getModelPackage());
        }

        modelConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getModelPackageTargetFolder());
        // Mapper configuration
        SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();
        if (generatorConfig.isUseSchemaPackage()) {
            //使用schema名做为包名
            mapperConfig.setTargetPackage(generatorConfig.getMappingXMLPackage() + "." + schemaName);
        } else {
            mapperConfig.setTargetPackage(generatorConfig.getMappingXMLPackage());
        }
        mapperConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getMappingXMLTargetFolder());
        // DAO
        JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
        daoConfig.setConfigurationType("XMLMAPPER");
        if (generatorConfig.isUseSchemaPackage()) {
            //使用schema名做包名
            daoConfig.setTargetPackage(generatorConfig.getDaoPackage() + "." + schemaName);
        } else {
            daoConfig.setTargetPackage(generatorConfig.getDaoPackage());
        }
        daoConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getDaoTargetFolder());


        context.setId("myid");
        context.addTableConfiguration(tableConfig);
        context.setJdbcConnectionConfiguration(jdbcConfig);
        context.setJdbcConnectionConfiguration(jdbcConfig);
        context.setJavaModelGeneratorConfiguration(modelConfig);
        context.setSqlMapGeneratorConfiguration(mapperConfig);
        context.setJavaClientGeneratorConfiguration(daoConfig);
        // Comment
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
        commentConfig.setConfigurationType(DbRemarksCommentGenerator.class.getName());
        if (generatorConfig.isComment()) {
            commentConfig.addProperty("columnRemarks", "true");
        }
        if (generatorConfig.isAnnotation()) {
            commentConfig.addProperty("annotations", "true");
        }
        context.setCommentGeneratorConfiguration(commentConfig);
        // set java file encoding
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, generatorConfig.getEncoding());

        //使用jdk1.8  时间
        if(generatorConfig.isLocalDate()){
            JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
            javaTypeResolverConfiguration.setConfigurationType("com.zmor.mybatis.generator.plugins.JavaTypeResolverJsr310Impl");
            context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        }

        //实体添加序列化
        /*PluginConfiguration serializablePluginConfiguration = new PluginConfiguration();
        serializablePluginConfiguration.addProperty("type", "org.mybatis.generator.plugins.SerializablePlugin");
        serializablePluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        context.addPluginConfiguration(serializablePluginConfiguration);*/
        // toString, hashCode, equals插件
        if (generatorConfig.isNeedToStringHashcodeEquals()) {
            PluginConfiguration pluginConfiguration1 = new PluginConfiguration();
            pluginConfiguration1.addProperty("type", "org.mybatis.generator.plugins.EqualsHashCodePlugin");
            pluginConfiguration1.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
            context.addPluginConfiguration(pluginConfiguration1);
            PluginConfiguration pluginConfiguration2 = new PluginConfiguration();
            pluginConfiguration2.addProperty("type", "org.mybatis.generator.plugins.ToStringPlugin");
            pluginConfiguration2.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
            context.addPluginConfiguration(pluginConfiguration2);
        }
        // limit/offset插件
        if (generatorConfig.isOffsetLimit()) {
            if (DbType.MySQL.name().equals(selectedDatabaseConfig.getDbType())
                    || DbType.PostgreSQL.name().equals(selectedDatabaseConfig.getDbType())) {
                PluginConfiguration pluginConfiguration = new PluginConfiguration();
                pluginConfiguration.addProperty("type", "com.zzg.mybatis.generator.plugins.MySQLLimitPlugin");
                pluginConfiguration.setConfigurationType("com.zmor.mybatis.generator.plugins.MySQLLimitPlugin");
                context.addPluginConfiguration(pluginConfiguration);
            }
        }
        //通用mapper插件
        if(generatorConfig.isUseTkMapper()){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.addProperty("mappers", generatorConfig.getBaseMapper());
            pluginConfiguration.setConfigurationType("com.zmor.mybatis.generator.plugins.MyTkMapperPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        //使用lombok插件
        if(generatorConfig.isLombok()){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.addProperty("author", generatorConfig.getAuthor());
            pluginConfiguration.setConfigurationType("com.zmor.mybatis.generator.plugins.LombokPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        if (generatorConfig.isGenerateBaseColumnA()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("com.zmor.mybatis.generator.plugins.BaseColumnListAPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (generatorConfig.isGenerateBatchInsert()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("com.zmor.mybatis.generator.plugins.BatchInsertPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (generatorConfig.isUseOracleSchema()) {
            tableConfig.setSchema(selectedDatabaseConfig.getUsername());
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("com.zmor.mybatis.generator.plugins.OracleSchemaPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        context.setTargetRuntime("MyBatis3");

        List<String> warnings = new ArrayList<>();
        Set<String> fullyqualifiedTables = new HashSet<>();
        Set<String> contexts = new HashSet<>();
        ShellCallback shellCallback = new DefaultShellCallback(true); // override=true
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, shellCallback, warnings);
        // if overrideXML selected, delete oldXML ang generate new one
        if (generatorConfig.isOverrideXML()) {
            String mappingXMLFilePath = getMappingXMLFilePath(generatorConfig);
            File mappingXMLFile = new File(mappingXMLFilePath);
            if (mappingXMLFile.exists()) {
                mappingXMLFile.delete();
            }
        }

        myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);
    }

    private String getMappingXMLFilePath(GeneratorConfig generatorConfig) {
        StringBuilder sb = new StringBuilder();
        sb.append(generatorConfig.getProjectFolder()).append("/");
        sb.append(generatorConfig.getMappingXMLTargetFolder()).append("/");
        String mappingXMLPackage = generatorConfig.getMappingXMLPackage();
        if (StringUtils.isNotEmpty(mappingXMLPackage)) {
            sb.append(mappingXMLPackage.replace(".", "/")).append("/");
        }
        sb.append(generatorConfig.getDomainObjectName()).append("Mapper.xml");
        return sb.toString();
    }

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public void setIgnoredColumns(List<IgnoredColumn> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public void setColumnOverrides(List<ColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }
}
