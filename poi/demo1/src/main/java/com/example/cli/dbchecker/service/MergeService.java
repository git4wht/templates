package com.example.cli.dbchecker.service;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Service
public class MergeService {
    private CsvParser parser;

    @PostConstruct
    private void init() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(false);
        parser = new CsvParser(settings);
    }

    public void mergeCsvFileToExcel(List<File> csvDirList, File outputFile) {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (File csvDir : csvDirList) {
                File[] csvFiles = csvDir.listFiles();

                for (File csvFile : csvFiles) {
                    importCsv(workbook, csvFile);
                }
            }

            try (OutputStream out = Files.newOutputStream(outputFile.toPath())) {
                workbook.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void importCsv(Workbook workbook, File csvFile) {
        String fileName = csvFile.getName();
        String sheetName = fileName.split("-")[0];
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        List<String[]> rows = parser.parseAll(csvFile);
        int startRow = sheet.getLastRowNum();
        startRow += 3;
        for (int i = 0; i < rows.size(); i++) {
            Row row = sheet.createRow(startRow + i);
            String[] cols = rows.get(i);
            for (int j = 0; j < cols.length; j++) {
                row.createCell(j).setCellValue(cols[j]);
            }
        }
    }

}
