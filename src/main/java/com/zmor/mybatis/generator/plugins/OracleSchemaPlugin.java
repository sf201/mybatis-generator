package com.zmor.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Liweizhou  2018/6/6
 */
public class OracleSchemaPlugin extends PluginAdapter {



    @Override
    public boolean validate(List<String> list) {
        return true;
    }



    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableAnnotation = "@Table(name=\"" + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName() + "\",schema=\""
                + introspectedTable.getTableConfiguration().getSchema().toUpperCase() + "\")";
        List<String> annotations = topLevelClass.getAnnotations();
        int index = 0;
        for (index = annotations.size() - 1; index >= 0;index--) {
            if (annotations.get(index).startsWith("@Table")) {
                annotations.remove(index);
                break;
            }
        }
        topLevelClass.addAnnotation(tableAnnotation);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }
}
