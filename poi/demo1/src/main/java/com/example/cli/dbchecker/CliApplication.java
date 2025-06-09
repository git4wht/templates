package com.example.cli.dbchecker;

import com.example.cli.dbchecker.service.ExportService;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class CliApplication implements CommandLineRunner {

    @Autowired
    private ExportService exportService;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CliApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
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