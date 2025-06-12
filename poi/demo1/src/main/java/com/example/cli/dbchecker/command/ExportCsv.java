package com.example.cli.dbchecker.command;

import com.example.cli.dbchecker.service.ExportService;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ExportCsv {

    @Autowired
    private ExportService exportService;

    public void execute(String... args) throws Exception {
        Options options = new Options();

        options.addOption("s", "sql-file-dir", true, "SQL file path (required)");
        options.addOption("o", "output-dir", true, "Output directory for CSV files (required)");
        options.addOption("h", "help", false, "Show help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h") || !cmd.hasOption("s") || !cmd.hasOption("o")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("csv-export-tool", options);
            return;
        }

        String sqlFileDir = cmd.getOptionValue("s");
        String outputDir = cmd.getOptionValue("o");

        File sqlDir = new File(sqlFileDir);
        if (!sqlDir.exists()) {
            System.err.println("SQL file directory does not exist: " + sqlFileDir);
            System.exit(1);
        }
        outputDir += LocalDateTime.now().format(DateTimeFormatter.ofPattern("/yyyyMMdd_HHmmss"));
        File outDir = new File(outputDir);
        if (!outDir.exists()) {
            boolean created = outDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create output directory: " + outputDir);
                System.exit(1);
            }
        }

        System.out.println("Starting export...");
        exportService.exportSqlFileToCsv(sqlFileDir, outputDir);
        System.out.println("Export completed.");
    }
}
