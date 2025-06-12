package com.example.cli.dbchecker.command;

import com.example.cli.dbchecker.service.MergeService;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MergeCsv {
    @Autowired
    private MergeService mergeService;

    public void execute(String... args) throws Exception {
        Options options = new Options();

        options.addOption("s", "csv-export-dirs", true, "Csv file paths (required).When multiple folders are specified, separate them with commas. ");
        options.addOption("o", "output-dir", true, "Output directory for CSV files (required)");
        options.addOption("h", "help", false, "Show help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h") || !cmd.hasOption("s") || !cmd.hasOption("o")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("csv-export-tool", options);
            return;
        }

        String argCsvDirs = cmd.getOptionValue("s");
        String argOutDir = cmd.getOptionValue("o");

        String[] csvDirArray = argCsvDirs.split(",");

        List<File> csvDirList = new ArrayList<File>();
        for (String csvFileDir : csvDirArray) {
            File csvDir = new File(csvFileDir);
            if (!csvDir.exists() && !csvDir.isDirectory()) {
                System.err.println("CSV file directory does not exist: " + csvFileDir);
            } else {
                csvDirList.add(csvDir);
            }
        }

        if (csvDirList.isEmpty()) {
            System.err.println("No csv file specified");
            System.exit(1);
        }

        File outDir = new File(argOutDir);
        if (!outDir.exists()) {
            boolean created = outDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create output directory: " + argOutDir);
                System.exit(1);
            }
        }

        File outFile = new File(argOutDir + LocalDateTime.now().format(DateTimeFormatter.ofPattern("/yyyyMMdd_HHmmss")) + ".xlsx");

        System.out.println("Starting merge...");
        mergeService.mergeCsvFileToExcel(csvDirList, outFile);
        System.out.println("Merge completed.");
    }
}
