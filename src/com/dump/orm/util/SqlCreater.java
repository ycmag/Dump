package com.dump.orm.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SqlCreater {

    public static void createModel(String tableName,String path) throws IOException {
        if (tableName == null || tableName.length() == 0) {
            return;
        }
        createModelSentence(tableName, "src/" +path.replace(".","/") + "/" + tableName.substring(0, 1).toUpperCase()
                + tableName.substring(1).toLowerCase() + ".java",path);
    }

    private static void createModelSentence(String tableName, String outPath, String path) throws IOException {
        if (tableName == null || tableName.length() == 0) {
            return;
        }
        String beanName = tableName.substring(0, 1).toUpperCase()
                + tableName.substring(1).toLowerCase();
        Map<String, String> paramMap = new HashMap<>();
        try (ResultSet rs = JDBCUtil.executeQuery("desc " + tableName)) {
            while (rs.next()) {
                paramMap.put(rs.getString(1),
                        getType(rs.getString(2).toLowerCase()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
        writer.write("package " + path+";");
        writer.newLine();
        writer.newLine();
        writer.write("public class " + beanName + " {");
        writer.newLine();
        Set<String> keySet = paramMap.keySet();
        for (String paramName : keySet) {
            writer.write("\tprivate " + paramMap.get(paramName) + " " + paramName + ";");
            writer.newLine();
        }
        writer.newLine();
        writer.write("\tpublic " + beanName + "() {");
        writer.newLine();
        writer.newLine();
        writer.write("\t}");
        writer.newLine();
        for (String paramName : keySet) {
            writer.newLine();
            writer.write("\tpublic " + paramMap.get(paramName) + " get"
                    + paramName.substring(0, 1).toUpperCase() + paramName.substring(1) + "() {");
            writer.newLine();
            writer.write("\t\treturn " + paramName + ";");
            writer.newLine();
            writer.write("\t}");
            writer.newLine();
            writer.newLine();
            writer.write("\tpublic void set"
                    + paramName.substring(0, 1).toUpperCase() + paramName.substring(1) + "("
                    + paramMap.get(paramName) + " " + paramName + ") {");
            writer.newLine();
            writer.write("\t\tthis." + paramName + " = " + paramName + ";");
            writer.newLine();
            writer.write("\t}");
            writer.newLine();
        }
        writer.write("}");
        writer.flush();
        writer.close();
    }

    private static String getType(String types) {
        if (types.startsWith("int")) {
            return "int";
        } else if (types.startsWith("double")) {
            return "double";
        } else if (types.startsWith("char")) {
            return "String";
        } else if (types.startsWith("varchar")) {
            return "String";
        } else if (types.startsWith("nvarchar")) {
            return "String";
        } else if (types.startsWith("nchar")) {
            return "String";
        } else if (types.startsWith("text")) {
            return "String";
        } else if (types.startsWith("bit")) {
            return "boolean";
        } else if (types.startsWith("binary")) {
            return "byte[]";
        } else if (types.startsWith("image")) {
            return "byte[]";
        } else if (types.startsWith("real")) {
            return "float";
        } else if (types.startsWith("bigint")) {
            return "long";
        } else if (types.startsWith("tinyint")) {
            return "short";
        } else if (types.startsWith("smallint")) {
            return "short";
        } else if (types.startsWith("decimal")) {
            return "java.math.BigDecimal";
        } else if (types.startsWith("numeric")) {
            return "java.math.BigDecimal";
        } else if (types.startsWith("datetime")) {
            return "java.sql.Times";
        } else {
            return "Object";
        }
    }
}
