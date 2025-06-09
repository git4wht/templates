package com.example.cli.dbchecker.common;

import lombok.Data;

@Data
public class ExportSql {
    private String fileName;
    private String name;
    private int index;
    private String sql;

    public ExportSql(String fileName) {
        this.fileName = fileName;
        String[] parts = fileName.split("-");
        this.name = parts[0];
        try {
            this.index = Integer.parseInt(parts[1], 10);
        } catch (Exception e) {
            this.index = 9999;
        }
    }
}
