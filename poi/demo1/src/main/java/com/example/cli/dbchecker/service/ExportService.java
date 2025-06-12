package com.example.cli.dbchecker.service;

import com.example.cli.dbchecker.common.CsvUtil;
import com.example.cli.dbchecker.common.ExportSql;
import com.example.cli.dbchecker.common.SqlFileReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExportService {

    private final SqlSessionFactory sqlSessionFactory;

    public ExportService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void exportSqlFileToCsv(String sqlFileDir, String outputDir) throws Exception {
        List<ExportSql> exportList = SqlFileReader.readSqlStatements(sqlFileDir);
        HashMap<String, Integer> countMap = new HashMap<>();

        try (SqlSession session = sqlSessionFactory.openSession()) {
            int index = 1;
            for (ExportSql export : exportList) {
                log.info("Executing SQL #" + index);
                List<Map<String, Object>> results = session.selectList("executeRawSql", export.getSql());
                Integer counter = countMap.get(export.getName());
                if (counter == null) {
                    counter = 1;
                } else {
                    counter++;
                }
                countMap.put(export.getName(), counter);

                if (results.isEmpty()) {
                    log.info("No results for SQL #" + export.getFileName());
                } else {
                    log.info("Results for SQL #" + export.getFileName() + ": " + results.size());
                    // 获取所有列名，保证列顺序
                    List<String> headers = results.get(0).keySet().stream().toList();

                    File outputFile = Paths.get(outputDir, export.getName() + "-" + index + ".csv").toFile();
                    CsvUtil.writeToCsv(outputFile, headers, results);
                    log.info("Exported to " + outputFile.getAbsolutePath());
                }
                index++;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
