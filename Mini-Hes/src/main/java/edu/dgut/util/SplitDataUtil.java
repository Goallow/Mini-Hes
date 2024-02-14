package edu.dgut.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * @Author Goallow
 * @Date 2021/12/11 14:33
 * @Version 1.0
 */
public class SplitDataUtil {

    public static void splitData(String filePath, String trainFileName,
                                 String validationFileName, String testFileName, String trainValidationName,
                                 String separator) throws IOException {
        Path path = Paths.get(filePath);
        List<String> originDataList = Files.readAllLines(path);
        List<String> trainList = new ArrayList<>();
        List<String> validationList = new ArrayList<>();
        List<String> testList = new ArrayList<>();
        List<String> trainValidationList = new ArrayList<>();
        Path trainFilePath = Paths.get(trainFileName);
        Path validationFilePath = Paths.get(validationFileName);
        Path testFilePath = Paths.get(testFileName);
        Path trainValidationFilePath = Paths.get(trainValidationName);
        File trainFile = new File(trainFileName);
        File validationFile = new File(validationFileName);
        File testFile = new File(testFileName);
        File trainValidationFile = new File(trainValidationName);
        Map<String, List<String>> originDataMap = new HashMap<>(originDataList.size() + 1);
        Collections.shuffle(originDataList);
        for (String line : originDataList) {
            System.out.println(line);
            String[] data = line.split(separator);
            List<String> infoList;
            String str = data[1] + separator + data[2];
            if (!originDataMap.containsKey(data[0])) {
                infoList = new ArrayList<>();
            } else {
                infoList = originDataMap.get(data[0]);
            }
            infoList.add(str);
            originDataMap.put(data[0], infoList);
        }
        for (String userId : originDataMap.keySet()) {
            Collections.shuffle(originDataMap.get(userId));
        }
        for (String userId : originDataMap.keySet()) {
            int count = 0;
            List<String> infoList = originDataMap.get(userId);
            for (String info : infoList) {
                int index = count % 5;
                String data = userId + separator + info;
                if (index == 3) {
                    validationList.add(data);
                    trainValidationList.add(data);
                } else if (index == 4) {
                    testList.add(data);
                } else {
                    trainList.add(data);
                    trainValidationList.add(data);
                }
                count += 1;
            }
        }

        if (!trainFile.exists()) {
            trainFile.createNewFile();
        }
        if (!validationFile.exists()) {
            validationFile.createNewFile();
        }
        if (!testFile.exists()) {
            testFile.createNewFile();
        }
        if (!trainValidationFile.exists()) {
            trainValidationFile.createNewFile();
        }

        int count = 0;
        System.out.println("正在写入训练集");
        for (String str : trainList) {
            try(BufferedWriter bw = Files.newBufferedWriter(trainFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                bw.write(str + "\n");
            }
        }
        System.out.println("正在写入验证集");
        for (String str : validationList) {
            try(BufferedWriter bw = Files.newBufferedWriter(validationFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                bw.write(str + "\n");
            }
        }
        System.out.println("正在写入测试集");
        for (String str : testList) {
            try (BufferedWriter bw = Files.newBufferedWriter(testFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                bw.write(str + "\n");
            }
        }
        System.out.println("正在写入训练验证集");
        for (String str : trainValidationList) {
            try (BufferedWriter bw = Files.newBufferedWriter(trainValidationFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                bw.write(str + "\n");
            }
        }
    }
    public static void main(String[] args) throws IOException {
        /**
        Path path = Paths.get("D:/aa.txt");
        String data = Files.readString(path);
        System.out.println(data);
         */
        String filePath = "C:\\Users\\Goallow\\Documents\\Data\\10m\\ratings.dat";
        String testPath = "C:\\Users\\Goallow\\Documents\\Data\\10m\\10m_test_8tv2t_6t2v.txt";
        String trainPath = "C:\\Users\\Goallow\\Documents\\Data\\10m\\10m_train_8tv2t_6t2v.txt";
        String trainValidationPath = "C:\\Users\\Goallow\\Documents\\Data\\10m\\10m_train_validation_8tv2t_6t2v.txt";
        String validationPath = "C:\\Users\\Goallow\\Documents\\Data\\10m\\10m_validation_8tv2t_6t2v.txt";
        SplitDataUtil.splitData(filePath, trainPath, validationPath, testPath, trainValidationPath, "::");
    }
}
