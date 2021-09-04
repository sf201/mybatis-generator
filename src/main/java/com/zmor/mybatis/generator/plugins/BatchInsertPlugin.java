package com.zmor.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.plugins.MapperAnnotationPlugin;
import tk.mybatis.mapper.generator.MapperPlugin;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liweizhou  2018/6/6
 */
public class BatchInsertPlugin extends PluginAdapter {



    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement sql = new XmlElement("insert");
        sql.addAttribute(new Attribute("id","insertBatch"));
        sql.addAttribute(new Attribute("useGeneratedKeys","false"));
        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "Base_Column_List"));
        sql.addElement(new TextElement(MessageFormat.format("insert into {0}(", introspectedTable.getFullyQualifiedTable().getIntrospectedTableName())));
        sql.addElement(include);
        sql.addElement(new TextElement(")"));
        XmlElement forEach = new XmlElement("foreach");
        forEach.addAttribute(new Attribute("collection", "list"));
        forEach.addAttribute(new Attribute("item", "item"));
        forEach.addAttribute(new Attribute("index", "index"));
        forEach.addAttribute(new Attribute("separator", "union all"));
        StringBuilder sb = new StringBuilder();
        sb.append("\tselect");
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            sb.append(System.lineSeparator()).append("\t\t");
            sb.append("#{item.").append(column.getActualColumnName()).append(",")
            .append("jdbcType=").append(column.getJdbcTypeName()).append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(System.lineSeparator()).append("\t\t");
        sb.append("from dual");
        forEach.addElement(new TextElement(sb.toString()));
        sql.addElement(forEach);
        document.getRootElement().addElement(sql);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType(MessageFormat.format("List<{0}>", introspectedTable.getFullyQualifiedTable().getDomainObjectName()));
        Method insertBatch = new Method();
        insertBatch.setVisibility(JavaVisibility.PUBLIC);
        insertBatch.setName("insertBatch");
        insertBatch.addParameter(new Parameter(listType, "list"));
        insertBatch.setReturnType(FullyQualifiedJavaType.getIntInstance());
        interfaze.addMethod(insertBatch);
        return true;
    }




}
