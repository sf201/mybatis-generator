package com.zmor.mybatis.generator.model;

/**
 * GeneratorConfig is the Config of mybatis generator config exclude database
 * config
 * <p>
 * Created by Owen on 6/16/16.
 */
public class GeneratorConfig {

    /**
     * 本配置的名称
     */
    private String name;

    private String connectorJarPath;

    private String projectFolder;

    private String modelPackage;

    private String modelPackageTargetFolder;

    private String daoPackage;

    private String daoTargetFolder;

    private String mapperName;

    private String mappingXMLPackage;

    private String mappingXMLTargetFolder;

    private String tableName;

    private String domainObjectName;

    private boolean offsetLimit;

    private boolean comment;

    private boolean overrideXML;

    private boolean needToStringHashcodeEquals;

    private boolean annotation;

    private boolean useActualColumnNames;

    private boolean useExampe;

    private String generateKeys;

    private String encoding;

    private boolean useTableNameAlias;

    private boolean useSchemaPrefix;

    private boolean useTkMapper;

    private boolean lombok;

    private boolean localDate;

    private String baseMapper;

    private String author;

    private boolean useSchemaPackage;
    private boolean generateBaseColumnA;
    private boolean generateBatchInsert;
    private boolean useOracleSchema;
    private boolean generateMybatisPlus;
    private String parentPackage;

    public boolean isUseMPP() {
        return useMPP;
    }

    public void setUseMPP(boolean useMPP) {
        this.useMPP = useMPP;
    }

    private boolean useMPP;

    public String getParentPackage() {
        return parentPackage;
    }

    public void setParentPackage(String parentPackage) {
        this.parentPackage = parentPackage;
    }

    public boolean isGenerateMybatisPlus() {
        return generateMybatisPlus;
    }

    public void setGenerateMybatisPlus(boolean generateMybatisPlus) {
        this.generateMybatisPlus = generateMybatisPlus;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isLombok() {
        return lombok;
    }

    public void setLombok(boolean lombok) {
        this.lombok = lombok;
    }

    public boolean isLocalDate() {
        return localDate;
    }

    public void setLocalDate(boolean localDate) {
        this.localDate = localDate;
    }

    public String getBaseMapper() {
        return baseMapper;
    }

    public void setBaseMapper(String baseMapper) {
        this.baseMapper = baseMapper;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public boolean isUseTkMapper() {
        return useTkMapper;
    }

    public void setUseTkMapper(boolean useTkMapper) {
        this.useTkMapper = useTkMapper;
    }

    public boolean isUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    public void setUseSchemaPrefix(boolean useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

    public boolean isUseExampe() {
        return useExampe;
    }

    public void setUseExampe(boolean useExampe) {
        this.useExampe = useExampe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDomainObjectName() {
        return domainObjectName;
    }

    public void setDomainObjectName(String domainObjectName) {
        this.domainObjectName = domainObjectName;
    }

    public String getConnectorJarPath() {
        return connectorJarPath;
    }

    public void setConnectorJarPath(String connectorJarPath) {
        this.connectorJarPath = connectorJarPath;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getModelPackageTargetFolder() {
        return modelPackageTargetFolder;
    }

    public void setModelPackageTargetFolder(String modelPackageTargetFolder) {
        this.modelPackageTargetFolder = modelPackageTargetFolder;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getDaoTargetFolder() {
        return daoTargetFolder;
    }

    public void setDaoTargetFolder(String daoTargetFolder) {
        this.daoTargetFolder = daoTargetFolder;
    }

    public String getMappingXMLPackage() {
        return mappingXMLPackage;
    }

    public void setMappingXMLPackage(String mappingXMLPackage) {
        this.mappingXMLPackage = mappingXMLPackage;
    }

    public String getMappingXMLTargetFolder() {
        return mappingXMLTargetFolder;
    }

    public void setMappingXMLTargetFolder(String mappingXMLTargetFolder) {
        this.mappingXMLTargetFolder = mappingXMLTargetFolder;
    }

    public boolean isOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(boolean offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isNeedToStringHashcodeEquals() {
        return needToStringHashcodeEquals;
    }

    public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public void setAnnotation(boolean annotation) {
        this.annotation = annotation;
    }

    public boolean isUseActualColumnNames() {
        return useActualColumnNames;
    }

    public void setUseActualColumnNames(boolean useActualColumnNames) {
        this.useActualColumnNames = useActualColumnNames;
    }

    public String getGenerateKeys() {
        return generateKeys;
    }

    public void setGenerateKeys(String generateKeys) {
        this.generateKeys = generateKeys;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean getUseTableNameAlias() {
        return useTableNameAlias;
    }

    public void setUseTableNameAlias(boolean useTableNameAlias) {
        this.useTableNameAlias = useTableNameAlias;
    }

    public boolean isUseTableNameAlias() {
        return useTableNameAlias;
    }

    public boolean isOverrideXML() {
        return overrideXML;
    }

    public void setOverrideXML(boolean overrideXML) {
        this.overrideXML = overrideXML;
    }

    public boolean isUseSchemaPackage() {
        return useSchemaPackage;
    }

    public void setUseSchemaPackage(boolean useSchemaPackage) {
        this.useSchemaPackage = useSchemaPackage;
    }

    public boolean isGenerateBaseColumnA() {
        return generateBaseColumnA;
    }

    public void setGenerateBaseColumnA(boolean generateBaseColumnA) {
        this.generateBaseColumnA = generateBaseColumnA;
    }

    public boolean isGenerateBatchInsert() {
        return generateBatchInsert;
    }

    public void setGenerateBatchInsert(boolean generateBatchInsert) {
        this.generateBatchInsert = generateBatchInsert;
    }

    public boolean isUseOracleSchema() {
        return useOracleSchema;
    }

    public void setUseOracleSchema(boolean useOracleSchema) {
        this.useOracleSchema = useOracleSchema;
    }
}
