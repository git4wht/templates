package com.example.cli.dbchecker.common;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class CsvUtil {

    public static void writeToCsv(File file, List<String> headers, List<Map<String, Object>> rows) throws IOException {
        CsvWriterSettings settings = new CsvWriterSettings();
        settings.setHeaders(headers.toArray(new String[0]));

        try {
            CsvWriter writer = new CsvWriter(new FileWriter(file), settings);
            writer.writeHeaders();

            for (Map<String, Object> row : rows) {
                Object[] values = headers.stream()
                        .map(h -> row.getOrDefault(h, ""))
                        .toArray();
                writer.writeRow(values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
