package com.zmor.mybatis.generator.plugins;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.JDBCConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Liweizhou  2018/6/6
 */
public class BaseColumnListAPlugin extends PluginAdapter {

    private String author;

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id","Base_Column_List_A"));
        StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            cnt++;
            if (cnt % 5 == 0) {
                sb.append(System.lineSeparator()).append("\t");
            }
            sb.append("a.").append(column.getActualColumnName()).append(",");
        }
        TextElement textElement = new TextElement(sb.deleteCharAt(sb.length() - 1).toString());
        sql.addElement(textElement);
        document.getRootElement().addElement(sql);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

}
