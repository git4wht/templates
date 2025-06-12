package com.example.cli.dbchecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.cli.dbchecker.command.*;

import java.util.Arrays;

@SpringBootApplication
public class CliApplication implements CommandLineRunner {
    @Autowired
    private ExportCsv exportCsv;
    @Autowired
    private MergeCsv mergeCsv;

    public static void main(String[] args) {
        SpringApplication.run(CliApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.out.println("Example: exportCsv , mergeCsv");
            return;
        }
        String command = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (command) {
            case "exportCsv":
                exportCsv.execute(commandArgs);
                break;
            case "mergeCsv":
                mergeCsv.execute(commandArgs);
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }
}