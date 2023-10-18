package com.zmor.mybatis.generator.controller;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.converts.OracleTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.zmor.mybatis.generator.bridge.MybatisGeneratorBridge;
import com.zmor.mybatis.generator.model.DatabaseConfig;
import com.zmor.mybatis.generator.model.DbType;
import com.zmor.mybatis.generator.model.GeneratorConfig;
import com.zmor.mybatis.generator.model.UITableColumnVO;
import com.zmor.mybatis.generator.util.ConfigHelper;
import com.zmor.mybatis.generator.util.DbUtil;
import com.zmor.mybatis.generator.util.MyStringUtils;
import com.zmor.mybatis.generator.view.AlertUtil;
import com.zmor.mybatis.generator.view.UIProgressCallback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.IgnoredColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.sql.SQLRecoverableException;
import java.util.*;

public class MainUIController extends BaseFXController {

    private static final Logger _LOG = LoggerFactory.getLogger(MainUIController.class);
    private static final String FOLDER_NO_EXIST = "部分目录不存在，是否创建";
    // tool bar buttons
    @FXML
    private Label connectionLabel;
    @FXML
    private Label configsLabel;
    @FXML
    private TextField modelTargetPackage;
    @FXML
    private TextField mapperTargetPackage;
    @FXML
    private TextField daoTargetPackage;
    @FXML
    private TextField tableNameField;
    @FXML
    private TextField domainObjectNameField;
    @FXML
    private TextField generateKeysField;    //主键ID
    @FXML
    private TextField modelTargetProject;
    @FXML
    private TextField mappingTargetProject;
    @FXML
    private TextField daoTargetProject;
    @FXML
    private TextField projectFolderField;
    @FXML
    private CheckBox offsetLimitCheckBox;
    @FXML
    private CheckBox commentCheckBox;
    @FXML
    private CheckBox overrideXML;
    @FXML
    private CheckBox needToStringHashcodeEquals;
    @FXML
    private CheckBox useTableNameAliasCheckbox;
    @FXML
    private CheckBox annotationCheckBox;
    @FXML
    private CheckBox useActualColumnNamesCheckbox;
    @FXML
    private CheckBox useExample;
    @FXML
    private CheckBox useSchemaPrefix;
    @FXML
    private CheckBox useSchemaPackage;
    @FXML
    private CheckBox generateBaseColumnListA;
    @FXML
    private CheckBox generateMybatisPlus;
    @FXML
    private TextField parentPackage;
    @FXML
    private CheckBox generateBatchInsert;
    @FXML
    private CheckBox useOracleSchema;
    @FXML
    private CheckBox useMPP;

    @FXML
    private TreeView<String> leftDBTree;

    // Current selected databaseConfig
    private DatabaseConfig selectedDatabaseConfig;
    // Current selected tableName
    private String tableName;

    private List<IgnoredColumn> ignoredColumns;

    private List<ColumnOverride> columnOverrides;

    @FXML
    private ChoiceBox<String> encodingChoice;

    @FXML
    private TextField baseMapper;
    @FXML
    private CheckBox tkMapper;
    @FXML
    private CheckBox lombok;
    @FXML
    private CheckBox localDate;
    @FXML
    private TextField author;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView dbImage = new ImageView("icons/computer.png");
        dbImage.setFitHeight(40);
        dbImage.setFitWidth(40);
        connectionLabel.setGraphic(dbImage);
        connectionLabel.setOnMouseClicked(event -> {
            DbConnectionController controller = (DbConnectionController) loadFXMLPage("新建数据库连接", FXMLPage.NEW_CONNECTION, false);
            controller.setMainUIController(this);
            controller.showDialogStage();
        });
        ImageView configImage = new ImageView("icons/config-list.png");
        configImage.setFitHeight(40);
        configImage.setFitWidth(40);
        configsLabel.setGraphic(configImage);
        configsLabel.setOnMouseClicked(event -> {
            GeneratorConfigController controller = (GeneratorConfigController) loadFXMLPage("配置", FXMLPage.GENERATOR_CONFIG, false);
            controller.setMainUIController(this);
            controller.showDialogStage();
        });

        leftDBTree.setShowRoot(false);
        leftDBTree.setRoot(new TreeItem<>());
        Callback<TreeView<String>, TreeCell<String>> defaultCellFactory = TextFieldTreeCell.forTreeView();
        leftDBTree.setCellFactory((TreeView<String> tv) -> {
            TreeCell<String> cell = defaultCellFactory.call(tv);
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int level = leftDBTree.getTreeItemLevel(cell.getTreeItem());
                TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
                TreeItem<String> treeItem = treeCell.getTreeItem();
                if (level == 1) {
                    final ContextMenu contextMenu = new ContextMenu();
                    MenuItem item1 = new MenuItem("关闭连接");
                    item1.setOnAction(event1 -> treeItem.getChildren().clear());
                    MenuItem item2 = new MenuItem("编辑连接");
                    item2.setOnAction(event1 -> {
                        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        DbConnectionController controller = (DbConnectionController) loadFXMLPage("编辑数据库连接", FXMLPage.NEW_CONNECTION, false);
                        controller.setMainUIController(this);
                        controller.setConfig(selectedConfig);
                        controller.showDialogStage();
                    });
                    MenuItem item3 = new MenuItem("删除连接");
                    item3.setOnAction(event1 -> {
                        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        try {
                            ConfigHelper.deleteDatabaseConfig(selectedConfig);
                            this.loadLeftDBTree();
                        } catch (Exception e) {
                            AlertUtil.showErrorAlert("Delete connection failed! Reason: " + e.getMessage());
                        }
                    });
                    contextMenu.getItems().addAll(item1, item2, item3);
                    cell.setContextMenu(contextMenu);
                }
                if (event.getClickCount() == 2) {
                    treeItem.setExpanded(true);
                    if (level == 1) {
                        System.out.println("index: " + leftDBTree.getSelectionModel().getSelectedIndex());
                        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        try {
                            List<String> tables = DbUtil.getTableNames(selectedConfig);
                            if (tables != null && tables.size() > 0) {
                                ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
                                children.clear();
                                for (String tableName : tables) {
                                    TreeItem<String> newTreeItem = new TreeItem<>();
                                    ImageView imageView = new ImageView("icons/table.png");
                                    imageView.setFitHeight(16);
                                    imageView.setFitWidth(16);
                                    newTreeItem.setGraphic(imageView);
                                    newTreeItem.setValue(tableName);
                                    children.add(newTreeItem);
                                }
                            }
                        } catch (SQLRecoverableException e) {
                            _LOG.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert("连接超时");
                        } catch (Exception e) {
                            _LOG.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert(e.getMessage());
                        }
                    } else if (level == 2) { // left DB tree level3
                        String tableName = treeCell.getTreeItem().getValue();
                        selectedDatabaseConfig = (DatabaseConfig) treeItem.getParent().getGraphic().getUserData();
                        this.tableName = tableName;
                        tableNameField.setText(tableName);
                        domainObjectNameField.setText(MyStringUtils.dbStringToCamelStyle(tableName));
                    }
                }
            });
            return cell;
        });
        loadLeftDBTree();
        setTooltip();
        //默认选中第一个，否则如果忘记选择，没有对应错误提示
        encodingChoice.getSelectionModel().selectFirst();
    }

    private void setTooltip() {
        encodingChoice.setTooltip(new Tooltip("生成文件的编码，必选"));
        generateKeysField.setTooltip(new Tooltip("insert时可以返回主键ID"));
        offsetLimitCheckBox.setTooltip(new Tooltip("是否要生成分页查询代码"));
        commentCheckBox.setTooltip(new Tooltip("使用数据库的列注释作为实体类字段名的Java注释 "));
        useActualColumnNamesCheckbox.setTooltip(new Tooltip("是否使用数据库实际的列名作为实体类域的名称"));
        useTableNameAliasCheckbox.setTooltip(new Tooltip("在Mapper XML文件中表名使用别名，并且列全部使用as查询"));
        overrideXML.setTooltip(new Tooltip("重新生成时把原XML文件覆盖，否则是追加"));
        useSchemaPackage.setTooltip(new Tooltip("使用schema或username做为包的子包名称，以进行分类，（特别对于oracle）。"));
        generateBaseColumnListA.setTooltip(new Tooltip("在XML文件中，生成Base_Column_List_A,以a做为别名。"));
        generateMybatisPlus.setTooltip(new Tooltip("是否生成mybatisPlus代码，将使用mybatisPlus代码生成器生成代码。"));
        parentPackage.setTooltip(new Tooltip("生成mybatisPlus代码时，所有项目父包"));
        generateBatchInsert.setTooltip(new Tooltip("是否生成batchInsert方法（仅对于oracle）。"));
    }

    void loadLeftDBTree() {
        TreeItem rootTreeItem = leftDBTree.getRoot();
        rootTreeItem.getChildren().clear();
        try {
            List<DatabaseConfig> dbConfigs = ConfigHelper.loadDatabaseConfig();
            for (DatabaseConfig dbConfig : dbConfigs) {
                TreeItem<String> treeItem = new TreeItem<>();
                treeItem.setValue(dbConfig.getName());
                ImageView dbImage = new ImageView("icons/computer.png");
                dbImage.setFitHeight(16);
                dbImage.setFitWidth(16);
                dbImage.setUserData(dbConfig);
                treeItem.setGraphic(dbImage);
                rootTreeItem.getChildren().add(treeItem);
            }
        } catch (Exception e) {
            _LOG.error("connect db failed, reason: {}", e);
            AlertUtil.showErrorAlert(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
        }
    }

    @FXML
    public void chooseProjectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
        if (selectedFolder != null) {
            projectFolderField.setText(selectedFolder.getAbsolutePath());
        }
    }

    @FXML
    public void generateCode() {
        if (tableName == null) {
            AlertUtil.showWarnAlert("请先在左侧选择数据库表");
            return;
        }
        String result = validateConfig();
        if (result != null) {
            AlertUtil.showErrorAlert(result);
            return;
        }
        GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
        if (!checkDirs(generatorConfig)) {
            return;
        }
        if (generatorConfig.isGenerateMybatisPlus()) {
            //生成mybatisPlus代码
            try {
                mybatisPlusGenerator(generatorConfig);
                AlertUtil.showInfoAlert("代码生成成功!");
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showErrorAlert(e.getMessage());
            }
        } else {
            MybatisGeneratorBridge bridge = new MybatisGeneratorBridge();
            bridge.setGeneratorConfig(generatorConfig);
            bridge.setDatabaseConfig(selectedDatabaseConfig);
            bridge.setIgnoredColumns(ignoredColumns);
            bridge.setColumnOverrides(columnOverrides);
            UIProgressCallback alert = new UIProgressCallback(Alert.AlertType.INFORMATION);
            bridge.setProgressCallback(alert);
            alert.show();
            try {
                bridge.generate();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showErrorAlert(e.getMessage());
            }
        }

    }

    private void mybatisPlusGenerator(GeneratorConfig generatorConfig) throws ClassNotFoundException {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = generatorConfig.getProjectFolder();
        gc.setOutputDir(projectPath + File.separator +  generatorConfig.getModelPackageTargetFolder());
//        gc.setSwagger2(cbEnableSwagger.isSelected());
//        if(cbAutoKey.isSelected()){
//            gc.setIdType(IdType.AUTO);
//        }else {
//            gc.setIdType(IdType.INPUT);
//        }
        gc.setIdType(IdType.INPUT);
        gc.setAuthor(generatorConfig.getAuthor());
        gc.setMapperName("%sMapper");
        gc.setFileOverride(generatorConfig.isOverrideXML());
        if (!generatorConfig.isLocalDate()) {
            gc.setDateType(DateType.ONLY_DATE);
        }
        gc.setOpen(false);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DbType dbType = DbType.valueOf(selectedDatabaseConfig.getDbType());
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(DbUtil.getConnectionUrlWithSchema(selectedDatabaseConfig));
        // dsc.setSchemaName("public");
        dsc.setTypeConvert(new OracleTypeConvert());
        dsc.setDriverName(dbType.getDriverClass());
        dsc.setUsername(selectedDatabaseConfig.getUsername());
        dsc.setPassword(selectedDatabaseConfig.getPassword());
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        // 设置模块名
//        pc.setModuleName(txtModuleName.getText().trim());
        pc.setParent("");
//        pc.setParent(generatorConfig.getParentPackage());
        pc.setMapper(generatorConfig.getDaoPackage() );
        pc.setEntity(generatorConfig.getModelPackage());
        pc.setXml("");
//        pc.setMapper(getMybatisPlusChildPackageName(generatorConfig.getParentPackage(),generatorConfig.getDaoPackage()) );
//        pc.setEntity(getMybatisPlusChildPackageName(generatorConfig.getParentPackage(),generatorConfig.getModelPackage()));
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("basePackage", generatorConfig.getBaseMapper());
                if (generatorConfig.isUseOracleSchema()) {
                    map.put("useSchema", true);
                } else {
                    map.put("useSchema", false);
                }
                this.setMap(map);
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("templates/mymapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return projectPath +"/"+ generatorConfig.getMappingXMLTargetFolder()+"/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);

        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                boolean isCreate = true;

                //覆盖generator core 方法
                if(FileType.CONTROLLER.equals(fileType)){
                    isCreate = false;
                }else if(FileType.ENTITY.equals(fileType)){
                    isCreate = true;
                }else if(FileType.MAPPER.equals(fileType)){
                    isCreate = true;
                }else if(FileType.OTHER.equals(fileType)){
                    //XML = OTHER 有些惊奇
                    isCreate = true;
                }else if(FileType.SERVICE.equals(fileType) || FileType.SERVICE_IMPL.equals(fileType)){
                    isCreate = false;
                }

                // 全局判断【默认】
                if(isCreate){
                    File file = new File(filePath);
                    boolean exist = file.exists();
                    if (!exist) {
                        file.getParentFile().mkdirs();
                    }
                }

                return isCreate;
            }
        });

        mpg.setCfg(cfg);
        TemplateConfig tc = new TemplateConfig();
        tc.setXml(null);
        tc.setController(null);
        tc.setService(null);
        tc.setServiceImpl(null);
        tc.setMapper("templates/mymapper.java");
        if (generatorConfig.isUseMPP()) {
            tc.setEntity("templates/mppentity.java");
        } else {
            tc.setEntity("templates/myentity.java");
        }
        mpg.setTemplate(tc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setSuperEntityClass("com.baomidou.mybatisplus.samples.generator.common.BaseEntity");
        strategy.setEntityLombokModel(generatorConfig.isLombok());


//        if(StringUtils.isNotBlank(txtSuperController.getText())){
//            //设置BaseController
//            strategy.setSuperControllerClass(txtSuperController.getText());
//        }

//        if(StringUtils.isNotBlank(txtLogicDeletedField.getText())){
//            //设置逻辑删除栏位
//            strategy.setLogicDeleteFieldName(txtLogicDeletedField.getText().trim());
//        }

        //把ListView 的数据转成 数组
        List<String> lstTable = new ArrayList<>(16);
        lstTable.add(generatorConfig.getTableName());
        String[] arrTable = lstTable.toArray(new String[lstTable.size()]);

        strategy.setEntityColumnConstant(true);
        strategy.setInclude(arrTable);
//        strategy.setSuperEntityColumns("id");
        strategy.setControllerMappingHyphenStyle(true);

//        if(StringUtils.isNotBlank(txtTablePrefix.getText())){
//            strategy.setTablePrefix(txtTablePrefix.getText());
//        }
        mpg.setStrategy(strategy);
        // 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    private String getMybatisPlusChildPackageName(String parentPackage, String childPackage) {
        if (StringUtils.isAnyEmpty(parentPackage, childPackage)) {
            return childPackage;
        }
        //子包包含父包，且比父包更长
        if (childPackage.contains(parentPackage)&&childPackage.length()>parentPackage.length()) {
            return childPackage.replace(parentPackage, "").substring(1);
        }
        return childPackage;
    }

    private String validateConfig() {
        String projectFolder = projectFolderField.getText();
        if (StringUtils.isEmpty(projectFolder)) {
            return "项目目录不能为空";
        }
        if (StringUtils.isEmpty(domainObjectNameField.getText())) {
            return "类名不能为空";
        }
        if (StringUtils.isAnyEmpty(modelTargetPackage.getText(), mapperTargetPackage.getText(), daoTargetPackage.getText())) {
            return "包名不能为空";
        }
        if(tkMapper.isSelected()){
            if(StringUtils.isBlank(baseMapper.getText())){
                return "通用mapper时继承不为空";
            }
        }
        return null;
    }

    @FXML
    public void saveGeneratorConfig() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("保存当前配置");
        dialog.setContentText("请输入配置名称");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();
            if (StringUtils.isEmpty(name)) {
                AlertUtil.showErrorAlert("名称不能为空");
                return;
            }
            _LOG.info("user choose name: {}", name);
            try {
                GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
                generatorConfig.setName(name);
                ConfigHelper.deleteGeneratorConfig(name);
                ConfigHelper.saveGeneratorConfig(generatorConfig);
            } catch (Exception e) {
                _LOG.error("删除配置失败",e);
                AlertUtil.showErrorAlert("删除配置失败");
            }
        }
    }

    public GeneratorConfig getGeneratorConfigFromUI() {
        GeneratorConfig generatorConfig = new GeneratorConfig();
        generatorConfig.setProjectFolder(projectFolderField.getText());
        generatorConfig.setModelPackage(modelTargetPackage.getText());
        generatorConfig.setGenerateKeys(generateKeysField.getText());
        generatorConfig.setModelPackageTargetFolder(modelTargetProject.getText());
        generatorConfig.setDaoPackage(daoTargetPackage.getText());
        generatorConfig.setDaoTargetFolder(daoTargetProject.getText());
        generatorConfig.setMappingXMLPackage(mapperTargetPackage.getText());
        generatorConfig.setMappingXMLTargetFolder(mappingTargetProject.getText());
        generatorConfig.setTableName(tableNameField.getText());
        generatorConfig.setDomainObjectName(domainObjectNameField.getText());
        generatorConfig.setOffsetLimit(offsetLimitCheckBox.isSelected());
        generatorConfig.setComment(commentCheckBox.isSelected());
        generatorConfig.setOverrideXML(overrideXML.isSelected());
        generatorConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEquals.isSelected());
        generatorConfig.setUseTableNameAlias(useTableNameAliasCheckbox.isSelected());
        generatorConfig.setAnnotation(annotationCheckBox.isSelected());
        generatorConfig.setUseActualColumnNames(useActualColumnNamesCheckbox.isSelected());
        generatorConfig.setEncoding(encodingChoice.getValue());
        generatorConfig.setUseExampe(useExample.isSelected());
        generatorConfig.setUseSchemaPrefix(useSchemaPrefix.isSelected());
        generatorConfig.setUseSchemaPackage(useSchemaPackage.isSelected());
        generatorConfig.setGenerateBaseColumnA(generateBaseColumnListA.isSelected());
        generatorConfig.setGenerateBatchInsert(generateBatchInsert.isSelected());
        generatorConfig.setUseOracleSchema(useOracleSchema.isSelected());
        generatorConfig.setGenerateMybatisPlus(generateMybatisPlus.isSelected());
        generatorConfig.setParentPackage(parentPackage.getText());
        generatorConfig.setUseMPP(useMPP.isSelected());
        generatorConfig.setUseTkMapper(tkMapper.isSelected());
        generatorConfig.setLombok(lombok.isSelected());
        generatorConfig.setBaseMapper(baseMapper.getText());
        generatorConfig.setAuthor(author.getText());
        generatorConfig.setLocalDate(localDate.isSelected());
        return generatorConfig;
    }

    public void setGeneratorConfigIntoUI(GeneratorConfig generatorConfig) {
        projectFolderField.setText(generatorConfig.getProjectFolder());
        modelTargetPackage.setText(generatorConfig.getModelPackage());
        generateKeysField.setText(generatorConfig.getGenerateKeys());
        modelTargetProject.setText(generatorConfig.getModelPackageTargetFolder());
        daoTargetPackage.setText(generatorConfig.getDaoPackage());
        daoTargetProject.setText(generatorConfig.getDaoTargetFolder());
        mapperTargetPackage.setText(generatorConfig.getMappingXMLPackage());
        mappingTargetProject.setText(generatorConfig.getMappingXMLTargetFolder());
        encodingChoice.setValue(generatorConfig.getEncoding());
        tkMapper.setSelected(generatorConfig.isUseTkMapper());
        baseMapper.setText(generatorConfig.getBaseMapper());
        lombok.setSelected(generatorConfig.isLombok());
        localDate.setSelected(generatorConfig.isLocalDate());
        author.setText(generatorConfig.getAuthor());
        useSchemaPackage.setSelected(generatorConfig.isUseSchemaPackage());
        generateBaseColumnListA.setSelected(generatorConfig.isGenerateBaseColumnA());
        generateMybatisPlus.setSelected(generatorConfig.isGenerateMybatisPlus());
        parentPackage.setText(generatorConfig.getParentPackage());
        useMPP.setSelected(generatorConfig.isUseMPP());
        generateBatchInsert.setSelected(generatorConfig.isGenerateBatchInsert());
        useOracleSchema.setSelected(generatorConfig.isUseOracleSchema());
    }

    @FXML
    public void openTableColumnCustomizationPage() {
        if (tableName == null) {
            AlertUtil.showWarnAlert("请先在左侧选择数据库表");
            return;
        }
        SelectTableColumnController controller = (SelectTableColumnController) loadFXMLPage("定制列", FXMLPage.SELECT_TABLE_COLUMN, true);
        controller.setMainUIController(this);
        try {
            // If select same schema and another table, update table data
            if (!tableName.equals(controller.getTableName())) {
                List<UITableColumnVO> tableColumns = DbUtil.getTableColumns(selectedDatabaseConfig, tableName);
                controller.setColumnList(FXCollections.observableList(tableColumns));
                controller.setTableName(tableName);
            }
            controller.showDialogStage();
        } catch (Exception e) {
            _LOG.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    public void setIgnoredColumns(List<IgnoredColumn> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public void setColumnOverrides(List<ColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }

    /**
     * 检查并创建不存在的文件夹
     *
     * @return
     */
    private boolean checkDirs(GeneratorConfig config) {
        List<String> dirs = new ArrayList<>();
        dirs.add(config.getProjectFolder());
        dirs.add(FilenameUtils.normalize(config.getProjectFolder().concat("/").concat(config.getModelPackageTargetFolder())));
        dirs.add(FilenameUtils.normalize(config.getProjectFolder().concat("/").concat(config.getDaoTargetFolder())));
        dirs.add(FilenameUtils.normalize(config.getProjectFolder().concat("/").concat(config.getMappingXMLTargetFolder())));
        boolean haveNotExistFolder = false;
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                haveNotExistFolder = true;
            }
        }
        if (haveNotExistFolder) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText(FOLDER_NO_EXIST);
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.isPresent()) {
                if (ButtonType.OK == optional.get()) {
                    try {
                        for (String dir : dirs) {
                            FileUtils.forceMkdir(new File(dir));
                        }
                        return true;
                    } catch (Exception e) {
                        AlertUtil.showErrorAlert("创建目录失败，请检查目录是否是文件而非目录");
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

}
