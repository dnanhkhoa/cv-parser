package main;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import model.Resume;
import parser.impl.ResumeParser;

public class App {

    public static void demo() {
        try {
            ResumeParser resumeParser = new ResumeParser();
            String resume = resumeParser.parse(new File("C:\\Users\\Anh Khoa\\Desktop\\samples\\sample_cv_en.doc"));
            System.out.println(resume);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                ResumeParser resumeParser = new ResumeParser();
                File pathIn = new File(args[0]);
                File pathOut = new File(args[1]);
                if (pathOut.exists()) {
                    System.err.println("Output path is invalid!");
                    return;
                }
                if (pathIn.isFile()) {
                    System.out.println("Parsing...");

                    long startTime = System.currentTimeMillis();

                    String resume = resumeParser.parse(pathIn);
                    FileUtils.writeStringToFile(pathOut, resume, UTF_8);

                    long endTime = System.currentTimeMillis();

                    System.out.println(String.format("Done! (%d milliseconds)", endTime - startTime));
                } else if (pathIn.isDirectory()) {
                    System.out.println("Preparing...");

                    File[] files = pathIn.listFiles();
                    Map<String, Resume> folderMap = new HashMap<>();
                    Gson gson = new Gson();

                    long startTime = System.currentTimeMillis();

                    for (File file : files) {
                        System.out.println(String.format("Parsing: %s", file.getName()));
                        String resume = resumeParser.parse(file);
                        folderMap.put(file.getName(), gson.fromJson(resume, Resume.class));
                    }

                    FileUtils.writeStringToFile(pathOut, new Gson().toJson(folderMap).replace("\\u0026", "&"), UTF_8);

                    long endTime = System.currentTimeMillis();

                    System.out.println(String.format("Done! (%d milliseconds)", endTime - startTime));
                } else {
                    System.err.println("Input path not found!");
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.err.println("Parameter is invalid!");
        }
    }
}