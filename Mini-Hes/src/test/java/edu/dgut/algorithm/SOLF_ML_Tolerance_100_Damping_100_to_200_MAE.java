package edu.dgut.algorithm;

import edu.dgut.pojo.Entry;
import edu.dgut.util.FileUtil;
import edu.dgut.util.MatrixUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

public class SOLF_ML_Tolerance_100_Damping_100_to_200_MAE {
    public static void main(String[] args) throws IOException, BrokenBarrierException, InterruptedException {
        List<String> modelOutString = new ArrayList<>();
        List<Entry> trainValidationEntryList =
                FileUtil.loadDatasetFile(Constant.PATH, Constant.TRAIN_FILE_NAME, Constant.SEPARATOR);
        List<Entry> testEntryList = FileUtil.loadDatasetFile(Constant.PATH, Constant.TEST_FILE_NAME, Constant.SEPARATOR);
        int maxUserId = FileUtil.getMaxUserId(trainValidationEntryList, testEntryList);
        int maxItemId = FileUtil.getMaxItemId(trainValidationEntryList, testEntryList);
        int[] userCount = new int[maxUserId + 1];
        int[] itemCount = new int[maxItemId + 1];
        FileUtil.setCount(trainValidationEntryList, userCount, itemCount);
        double[][] userOriginMatrix = new double[maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        double[][] itemOriginMatrix = new double[maxItemId + 1][Constant.FEATURE_DIMENSION + 1];
        List<Double> bestMAEList = new ArrayList<>();
        double bestMAE = 999.0;
        double bestDamping = 0.0;
        double bestTolerance = 0.0;
        double bestRegularization = 0.0;
        int count = 1;
        double tolerance = 100;
        double[] dampingArray = {120, 140, 160, 180, 200};
        double gridSearchStart = System.currentTimeMillis();
        for (double damping : dampingArray) {
            for (double regularization : Constant.REGULARIZATION_LIST) {
                MatrixUtil.initializeMatrix(userOriginMatrix);
                MatrixUtil.initializeMatrix(itemOriginMatrix);
                System.out.println();
                String paraList = "Count = " + count + " Damping: " + damping + " tolerance: " + tolerance + " regularization: " + regularization;
                System.out.println(paraList);
                modelOutString.add(paraList);
                /** 实验：通过手工创建线程池替代Executors */
                // ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
                /** Common Thread Pool */
                //ExecutorService pool = new ThreadPoolExecutor(10, 10, )
                // SecondOrderLatentFactorModel aSOLF = new SecondOrderLatentFactorModel();
                double start = System.currentTimeMillis();
                SecondOrderLatentFactorModel_MAE aSOLF = new SecondOrderLatentFactorModel_MAE(damping, tolerance, regularization, modelOutString);
                aSOLF.setUserMatrix(userOriginMatrix);
                aSOLF.setItemMatrix(itemOriginMatrix);
                /**
                 *         results[0] = this.trainMAE;
                 *         results[1] = this.validationMAE;
                 *         results[2] = this.testMAE;
                 *         results[3] = this.validationBestMAE;
                 *         results[4] = this.testBestMAE;
                 *         results[5] = this.minTime;
                 *         results[6] = this.sumTime;
                 *         results[7] = this.minRound;
                 *         results[8] = this.sumRound;
                 */
                // 结果
                double[] results = aSOLF.run();
                double trainMAE = results[0];
                double validationMAE = results[1];
                double testMAE = results[2];
                double validationBestMAE = results[3];
                double testBestMAE = results[4];
                double minTime = results[5];
                double sumTime = results[6];
                double minRound = results[7];
                double sumRound = results[8];
                double end = System.currentTimeMillis();
                String validationBestMAEResult = "Finish validation MAE = " + validationBestMAE;
                String testBestMAEResult = "Finish test MAE = " + testBestMAE;
                String trainMinTimeResult = "Finish train min time = " + minTime;
                String trainSumTimeResult = "Finish train sum time = " + sumTime;
                String minRoundResult = "Finish min round = " + minRound;
                String sumRoundResult = "Finish sum round = " + sumRound;
                String currentModelTimeResult = "Finish time = " + (end - start);
                System.out.println();
                // System.out.println(currentModelTrainTimeResult);
                System.out.println(validationBestMAEResult);
                System.out.println(testBestMAEResult);
                System.out.println(trainMinTimeResult);
                System.out.println(trainSumTimeResult);
                System.out.println(minRoundResult);
                System.out.println(sumRoundResult);
                System.out.println(currentModelTimeResult);
                bestMAEList.add(validationBestMAE);
                modelOutString.add(validationBestMAEResult);
                modelOutString.add(testBestMAEResult);
                modelOutString.add(trainMinTimeResult);
                modelOutString.add(trainSumTimeResult);
                modelOutString.add(minRoundResult);
                modelOutString.add(sumRoundResult);
                modelOutString.add(currentModelTimeResult);
                modelOutString.add("\n");
                count += 1;
            }
        }
        double gridSearchEnd = System.currentTimeMillis();
        double sumTime = gridSearchEnd - gridSearchStart;
        bestMAE = Collections.min(bestMAEList);
        String bestMAEStr = "Best MAE of grid search = " + bestMAE;
        String sumTimeStr = "Grid Search time = " + sumTime;
        String typeStr = "full gn!!!";
        modelOutString.add(bestMAEStr);
        modelOutString.add(sumTimeStr);
        modelOutString.add(typeStr);
        FileUtil.saveOutputResult(modelOutString, "SLF_20M" + "_tolerance_100_damping_100_to_200_regularization", "MAE_Density_Large", Constant.SAVE_FILE_PATH);
    }
}
