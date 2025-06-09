package com.example.cli.dbchecker.common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class SqlFileReader {

    /**
     * 读取 SQL 文件内容，按分号 ; 分隔成多条语句
     */
    public static List<ExportSql> readSqlStatements(String fileDir) throws Exception {
        List<ExportSql> sqls = new ArrayList<>();

        Files.list(Paths.get(fileDir))
                .filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".sql"))
                .forEach(sqlFile -> {
                    String content = null;
                    try {
                        content = new String(Files.readAllBytes(sqlFile));
                        String fileName = sqlFile.getFileName().toString();
                        ExportSql exportSql = new ExportSql(fileName);
                        exportSql.setSql(content);
                        sqls.add(exportSql);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
        Collections.sort(sqls, Comparator
                .comparing(ExportSql::getName)
                .thenComparingInt(ExportSql::getIndex));
        return sqls;
    }
}
