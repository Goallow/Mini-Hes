package edu.dgut.util;

import edu.dgut.algorithm.Constant;
import edu.dgut.pojo.Entry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author Goallow
 * @Date 2021/11/15 9:33
 * @Version 1.0
 */
public class FileUtil {
    /**
     * 读取数据集
     * @param fileName 数据集文件名称
     * @param separator 数据集每行的分割符号
     * @return 矩阵实体列表
     * @throws Exception
     */
    public static List<Entry> loadDatasetFile(String fileName, String separator) throws IOException {
        String entryInfo = null;
        String filePath = Constant.PATH + fileName;
        List<Entry> entryList = new ArrayList<>();
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while ((entryInfo = bufferedReader.readLine()) != null) {
            String[] entryFeatureValue = entryInfo.split(separator);
            Entry entry = new Entry();
            entry.setUserId(Integer.parseInt(entryFeatureValue[0]));
            entry.setItemId(Integer.parseInt(entryFeatureValue[1]));
            entry.setRating(Double.parseDouble(entryFeatureValue[2]));
            entryList.add(entry);
        }
        return entryList;
    }


    public static List<Entry> loadDatasetFile(String path, String fileName, String separator) throws IOException {
        String entryInfo = null;
        String filePath = path + fileName;
        List<Entry> entryList = new ArrayList<>();
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while ((entryInfo = bufferedReader.readLine()) != null) {
            String[] entryFeatureValue = entryInfo.split(separator);
            Entry entry = new Entry();
            entry.setUserId(Integer.parseInt(entryFeatureValue[0]));
            entry.setItemId(Integer.parseInt(entryFeatureValue[1]));
            entry.setRating(Double.parseDouble(entryFeatureValue[2]));
            entryList.add(entry);
        }
        return entryList;
    }
    /**
     * 获取最大用户Id
     * @param entryList 实体集列表
     * @return 最大用户Id
     */
    public static int getMaxUserId(List<Entry> entryList) {
        int maxUserId = 0;
        for (Entry entry : entryList) {
            if (entry.getUserId() > maxUserId) {
                maxUserId = entry.getUserId();
            }
        }
        return maxUserId;
    }

    /**
     * 获取最大用户Id
     * @param trainEntryList
     * @param testEntryList
     * @return
     */
    public static int getMaxUserId(List<Entry> trainEntryList, List<Entry> testEntryList) {
        int maxUserId = 0;
        for (Entry entry : trainEntryList) {
            if (entry.getUserId() > maxUserId) {
                maxUserId = entry.getUserId();
            }
        }
        for (Entry entry : testEntryList) {
            if (entry.getUserId() > maxUserId) {
                maxUserId = entry.getUserId();
            }
        }
        return maxUserId;
    }

    /**
     * 获取最大用户Id
     * @param trainEntryList
     * @param testEntryList
     * @return
     */
    public static int getMaxUserId(List<Entry> trainEntryList, List<Entry> testEntryList, List<Entry> validationEntryList) {
        int maxUserId = 0;
        for (Entry entry : trainEntryList) {
            if (entry.getUserId() > maxUserId) {
                maxUserId = entry.getUserId();
            }
        }
        for (Entry entry : testEntryList) {
            if (entry.getUserId() > maxUserId) {
                maxUserId = entry.getUserId();
            }
        }

        for (Entry entry : validationEntryList) {
            if (entry.getUserId() > maxUserId) {
                maxUserId = entry.getUserId();
            }
        }
        return maxUserId;
    }

    /**
     * 获取最大物品Id
     * @param entryList 实体集合列表
     * @return 最大物品Id
     */
    public static int getMaxItemId(List<Entry> entryList) {
        int maxItemId = 0;
        for (Entry entry : entryList) {
            if (entry.getItemId() > maxItemId) {
                maxItemId = entry.getItemId();
            }
        }
        return maxItemId;
    }

    /**
     * 获取最大用户Id
     * @param trainEntryList
     * @param testEntryList
     * @return
     */
    public static int getMaxItemId(List<Entry> trainEntryList, List<Entry> testEntryList) {
        int maxItemId = 0;
        for (Entry entry : trainEntryList) {
            if (entry.getItemId() > maxItemId) {
                maxItemId = entry.getItemId();
            }
        }

        for (Entry entry : testEntryList) {
            if (entry.getItemId() > maxItemId) {
                maxItemId = entry.getItemId();
            }
        }
        return maxItemId;
    }

    /**
     * 获取最大用户Id
     * @param trainEntryList
     * @param testEntryList
     * @return
     */
    public static int getMaxItemId(List<Entry> trainEntryList, List<Entry> testEntryList, List<Entry> validationEntryList) {
        int maxItemId = 0;
        for (Entry entry : trainEntryList) {
            if (entry.getItemId() > maxItemId) {
                maxItemId = entry.getItemId();
            }
        }

        for (Entry entry : testEntryList) {
            if (entry.getItemId() > maxItemId) {
                maxItemId = entry.getItemId();
            }
        }

        for (Entry entry : validationEntryList) {
            if (entry.getItemId() > maxItemId) {
                maxItemId = entry.getItemId();
            }
        }
        return maxItemId;
    }

    /**
     * 分割数据集，将数据集分成训练集和测试集合，采用5折交叉验证
     * @param fileName 数据集的文件名
     * @throws IOException
     */
    public static void spiltDataset(String fileName) throws IOException {
        int i = 1;
        String entryInfo = null;
        String filePath = Constant.PATH + fileName;
        String trainFileName = Constant.PATH + Constant.TRAIN_FILE_NAME;
        String testFileName = Constant.PATH + Constant.TEST_FILE_NAME;
        Path trainFilePath = Paths.get(trainFileName);
        Path testFilePath = Paths.get(testFileName);
        File trainFile = new File(trainFileName);
        File testFile = new File(testFileName);
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        if (!trainFile.exists()) {
            trainFile.createNewFile();
        }
        if (!testFile.exists()) {
            testFile.createNewFile();
        }

        while ((entryInfo = bufferedReader.readLine()) != null) {
            if ((i % 5) != 0) {
                try (BufferedWriter bufferedWriter =
                             Files.newBufferedWriter
                                     (trainFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                    bufferedWriter.write(entryInfo + "\n");
                }
            } else {
                try (BufferedWriter bufferedWriter =
                             Files.newBufferedWriter
                                     (testFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                    bufferedWriter.write(entryInfo + "\n");
                }
            }
            i += 1;
        }
    }

    /**
     * 获取用户集合
     * @param entryList 数据集
     * @return 用户集
     */
    public static Set<Integer> getUserSet(List<Entry> entryList) {
        Set<Integer> userSet = new HashSet<>();
        for (Entry entry : entryList) {
            userSet.add(entry.getUserId());
        }
        return userSet;
    }

    public static Set<Integer> getUserSet(List<Entry> trainEntryList, List<Entry> testEntryList) {
        Set<Integer> userSet = new HashSet<>();
        for (Entry entry : trainEntryList) {
            userSet.add(entry.getUserId());
        }
        for (Entry entry : testEntryList) {
            userSet.add(entry.getUserId());
        }
        return userSet;
    }

    public static Set<Integer> getUserSet(List<Entry> trainEntryList, List<Entry> testEntryList, List<Entry> validationEntryList) {
        Set<Integer> userSet = new HashSet<>();
        for (Entry entry : trainEntryList) {
            userSet.add(entry.getUserId());
        }
        for (Entry entry : testEntryList) {
            userSet.add(entry.getUserId());
        }
        for (Entry entry : validationEntryList) {
            userSet.add(entry.getUserId());
        }
        return userSet;
    }
    /**
     * 获取物品集
     * @param entryList 数据集
     * @return 物品集
     */
    public static Set<Integer> getItemSet(List<Entry> entryList) {
        Set<Integer> itemSet = new HashSet<>();
        for (Entry entry : entryList) {
            itemSet.add(entry.getItemId());
        }
        return itemSet;
    }


    public static Set<Integer> getItemSet(List<Entry> trainEntryList, List<Entry> testEntryList) {
        Set<Integer> itemSet = new HashSet<>();
        for (Entry entry : trainEntryList) {
            itemSet.add(entry.getItemId());
        }
        for (Entry entry : testEntryList) {
            itemSet.add(entry.getItemId());
        }
        return itemSet;
    }

    public static Set<Integer> getItemSet(List<Entry> trainEntryList, List<Entry> testEntryList, List<Entry> validationEntryList) {
        Set<Integer> itemSet = new HashSet<>();
        for (Entry entry : trainEntryList) {
            itemSet.add(entry.getItemId());
        }
        for (Entry entry : testEntryList) {
            itemSet.add(entry.getItemId());
        }

        for (Entry entry : validationEntryList) {
            itemSet.add(entry.getItemId());
        }
        return itemSet;
    }
    /**
     * 保存用户u的个数
     * @param entryList 数据集
     * @param size hashmap的初始长度
     * @return
     */
    public static Map<Integer, Integer> getUserMap(List<Entry> entryList, int size) {
        Map<Integer, Integer> userMap = new HashMap<>((int) (size / 0.75 + 1));
        for (Entry entry : entryList) {
            if (userMap.containsKey(entry.getUserId())) {
                int count = userMap.get(entry.getUserId()) + 1;
                userMap.put(entry.getUserId(), count);
            } else {
                userMap.put(entry.getUserId(), 1);
            }
        }
        return userMap;
    }

    /**
     * 保存物品i的个数
     * @param entryList 数据集
     * @param size hashmap的初始长度
     * @return
     */
    public static Map<Integer, Integer> getItemMap(List<Entry> entryList, int size) {
        Map<Integer, Integer> itemMap = new HashMap<>(size);
        for (Entry entry : entryList) {
            int itemId = entry.getItemId();
            itemMap.put(itemId, itemMap.getOrDefault(itemId, 0) + 1);
        }

        return itemMap;
    }

    public static void setCount(List<Entry> entryList, int[] userCount, int[] itemCount) {
        for (Entry entry : entryList) {
            userCount[entry.getUserId()] += 1;
            itemCount[entry.getItemId()] += 1;
        }
    }

    /**
     * 保存输出结果
     * @param resultList
     * @param modelName
     * @param resultType
     * @param filePath
     */
    public static void saveOutputResult(List<String> resultList, String modelName, String resultType, String filePath) {
        System.out.println("正在保存 " + modelName + " 模型 " + resultType + " 输出结果");
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String path = filePath + "//" + modelName;
            File resultFile = new File(path);
            if (!resultFile.exists()) {
                resultFile.mkdirs();
            }
            String realPath = path+ "//" + resultType + "_" + formatter.format(calendar.getTime()) + ".txt";
            resultFile = new File(realPath);
            resultFile.createNewFile();
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(resultFile, true), "utf-8"), 10240);
            for (int i = 0; i < resultList.size(); i++) {
                out.write(resultList.get(i) + "\r\n");
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("文件保存结束！！！！！！！！");
    }

    public static void resetEstimationHat(List<Entry> entryList) {
        for (Entry entry : entryList) {
            entry.setRatingHat(0);
        }
    }

    public static List<Entry> copyList(List<Entry> entryList) {
        List<Entry> tempEntryList = new ArrayList<>(entryList.size());
        for (Entry entry : entryList) {
            Entry tempEntry = new Entry();
            tempEntry.setUserId(entry.getUserId());
            tempEntry.setItemId(entry.getItemId());
            tempEntry.setRating(entry.getRating());
            tempEntryList.add(tempEntry);
        }
        return tempEntryList;
    }

    public static Map<Integer, List<Entry>> getUserListMap(List<Entry> entryList) {
        Map<Integer, List<Entry>> userListMap = new HashMap<>();

        for (Entry entry : entryList) {
            Integer u = entry.getUserId();
            userListMap.computeIfAbsent(u, k -> new ArrayList<>()).add(entry);
        }
        return userListMap;
    }

    public static Map<Integer, List<Entry>> getItemListMap(List<Entry> entryList) {
        Map<Integer, List<Entry>> itemListMap = new HashMap<>();

        for (Entry entry : entryList) {
            Integer i = entry.getItemId();
            itemListMap.computeIfAbsent(i, k -> new ArrayList<>()).add(entry);
        }
        return itemListMap;
    }
}
