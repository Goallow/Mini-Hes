package edu.dgut.algorithm;

import edu.dgut.pojo.Entry;
import edu.dgut.util.EvaluationUtil;
import edu.dgut.util.FileUtil;
import edu.dgut.util.MatrixUtil;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class BlockDiagonalSecondOrderLatentFactorModel {

    private int maxUserId;
    private int maxItemId;

    private double[][] userMatrix;
    private double[][] itemMatrix;


    /**
     * 用户隐特征矩阵梯度
     */
    private double[][] userGradientMatrix;
    /**
     * 物品隐特征矩阵梯度
     */
    private double[][] itemGradientMatrix;

    private double[][] userIncrementMatrix;
    private double[][] itemIncrementMatrix;

    /**
     * 更新的方向矩阵
     */
    private double[][] userDirectionMatrix;
    private double[][] itemDirectionMatrix;

    private double[][] userGaussNewtonMatrix;
    private double[][] itemGaussNewtonMatrix;

    /**
     * C.T Kelley Iterative Methods for Optimization 书里面是用 p
     */
    private double[][] userVectorMatrix;
    private double[][] itemVectorMatrix;

    private List<Entry> trainEntryList;
    private List<Entry> testEntryList;
    private List<Entry> validationEntryList;


    private Set<Integer> userSet;
    private Set<Integer> itemSet;

    private int[] userCount;
    private int[] itemCount;

    /** 参数列表 */
    private double gamma;
    private double tolerance;
    private double lambda;
    private double learningRate;

    private int threadNum;
    private int featureDimension;

    private int innerLoop;

    private int maxEpoch;
    private List<String> modelOutputString;

    private Map<Integer, List<Entry>> userListMap;

    private Map<Integer, List<Entry>> itemListMap;

    private Future<?>[] userFutures;
    private Future<?>[] itemFutures;


    private boolean converge;
    private double RMSE;
    private double validationRMSE;
    private double validationBestRMSE;

    private double testRMSE;

    private double testBestRMSE;

    private double startTime;

    private double endTime;

    private double realEndTime;

    private double minEndTime;
    private double minTime;
    private double sumTime;

    private int delayCount;
    private double minRound;
    private double sumRound;


    private ExecutorService executorService;


    public BlockDiagonalSecondOrderLatentFactorModel() throws IOException {
        /** 初始化数据集合 */
        this.trainEntryList = FileUtil.loadDatasetFile(Constant.TRAIN_FILE_NAME, Constant.SEPARATOR);
        this.testEntryList = FileUtil.loadDatasetFile(Constant.TEST_FILE_NAME, Constant.SEPARATOR);
        this.validationEntryList = FileUtil.loadDatasetFile(Constant.VALIDATION_FILE_NAME, Constant.SEPARATOR);
        /** 获取最大用户和物品的Id */
        this.maxUserId = FileUtil.getMaxUserId(this.trainEntryList, this.testEntryList, this.validationEntryList);
        this.maxItemId = FileUtil.getMaxItemId(this.trainEntryList, this.testEntryList, this.validationEntryList);
        /** 初始化用户矩阵和物品矩阵 */
        this.userMatrix = MatrixUtil.initializeMatrix(this.maxUserId, Constant.FEATURE_DIMENSION,
                Constant.RATING_LOWER_BOUND, Constant.RATING_UPPER_BOUND);
        this.itemMatrix = MatrixUtil.initializeMatrix(this.maxItemId, Constant.FEATURE_DIMENSION,
                Constant.RATING_LOWER_BOUND, Constant.RATING_UPPER_BOUND);

        /** 初始化用户梯度矩阵和物品梯度矩阵 */
        this.userGradientMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemGradientMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];

        this.userDirectionMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemDirectionMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];

        /** 初始化增量矩阵*/
        this.userIncrementMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemIncrementMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];
        /** 初始化高斯牛顿矩阵*/
        this.userGaussNewtonMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemGaussNewtonMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];

        /** 初始化任意向量 */
        this.userVectorMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemVectorMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];

        /** 初始化相关数 */
        this.userCount = new int[this.maxUserId + 1];
        this.itemCount = new int[this.maxItemId + 1];

        this.userSet = new HashSet<>();
        this.itemSet = new HashSet<>();
        for (Entry entry : this.trainEntryList) {
            this.userSet.add(entry.getUserId());
            this.itemSet.add(entry.getItemId());
            this.userCount[entry.getUserId()] += 1;
            this.itemCount[entry.getItemId()] += 1;
        }

        this.userFutures = new Future<?>[this.userSet.size()];
        this.itemFutures = new Future<?>[this.itemSet.size()];

        this.userListMap = FileUtil.getUserListMap(this.trainEntryList);
        this.itemListMap = FileUtil.getItemListMap(this.trainEntryList);

        this.converge = false;
        this.gamma = Constant.GAMMA;
        this.tolerance = Constant.TOLERANCE;
        this.lambda = Constant.LAMBDA;
        this.learningRate = Constant.LEARNING_RATE;
        this.featureDimension = Constant.FEATURE_DIMENSION;
        this.threadNum = Runtime.getRuntime().availableProcessors() / 2;

        this.executorService = newFixedThreadPool(threadNum);

        this.innerLoop = 20;
        this.maxEpoch = Constant.MAX_TRAINING_ROUND;
        this.modelOutputString = new ArrayList<>();
        this.RMSE = Math.pow(10, 10);
        this.validationRMSE = Math.pow(10, 10);
        this.validationBestRMSE = Math.pow(10, 10);
        this.testRMSE = Math.pow(10, 10);
        this.testBestRMSE = Math.pow(10, 10);
    }

    public BlockDiagonalSecondOrderLatentFactorModel(double gamma, double tolerance, double lambda, int threadNum, List<String> modelOutputString) throws IOException {
        this();
        this.gamma = gamma;
        this.tolerance = tolerance;
        this.lambda = lambda;
        this.threadNum = threadNum;
        this.executorService = newFixedThreadPool(threadNum);
        this.modelOutputString = modelOutputString;
    }


    public double[] run() throws InterruptedException, ExecutionException, BrokenBarrierException {
        int t = 1;
        this.startTime = 0.0;
        this.realEndTime = 0.0;
        this.minTime = 0.0;
        this.endTime = 0.0;
        this.sumTime = 0.0;
        double[] results = new double[9];
        while ((t <= this.maxEpoch) && (!converge)) {

            this.startTime = System.currentTimeMillis();

            this.computeUserGradient();
            this.computeItemGradient();

            this.singleUserHessianFreeConjugateGradientDescent();
            this.singleItemHessianFreeConjugateGradientDescent();

            this.updateUserMatrix();
            this.updateItemMatrix();

            this.endTime = System.currentTimeMillis();
            this.sumTime += (this.endTime - this.startTime);
            this.sumRound = t;
            this.convergenceAnalysis(t);

            t += 1;
        }
        results[1] = this.validationRMSE;
        results[2] = this.testRMSE;
        results[3] = this.validationBestRMSE;
        results[4] = this.testBestRMSE;
        results[5] = this.minTime / 1000;
        results[6] = this.sumTime / 1000;
        results[7] = this.minRound;
        results[8] = this.sumRound;
        System.out.println("运行时间(s) = " + sumTime);

        // Shut down the thread pool
        this.executorService.shutdown();
        return results;
    }

    public void computeUserGradient() throws ExecutionException, InterruptedException {
        // List<Future<?>> futures = new ArrayList<>();
        int index = 0;
        for (Integer u : this.userSet) {
            Future<?> future = executorService.submit(() -> {
                MatrixUtil.reset(this.userGradientMatrix[u]);
                List<Entry> entries = this.userListMap.get(u);
                for (Entry entry : entries) {
                    int i = entry.getItemId();
                    double ratingHat = MatrixUtil.innerProduct(this.userMatrix[u], this.itemMatrix[i]);
                    double residual = entry.getRating() - ratingHat;
                    for (int d = 1; d <= this.featureDimension; d++) {
                        this.userGradientMatrix[u][d] +=
                                residual * this.itemMatrix[i][d] - this.lambda * this.userMatrix[u][d];
                    }
                }
            });
            this.userFutures[index] = future;
            index += 1;
        }
        // 等待所有线程执行完成
        for (Future<?> future : this.userFutures) {
            future.get(); // 等待线程执行完成
        }
    }
    public void computeItemGradient() throws ExecutionException, InterruptedException {
        // List<Future<?>> futures = new ArrayList<>();
        int index = 0;
        for (Integer i : this.itemSet) {
            Future<?> future = executorService.submit(() -> {
                MatrixUtil.reset(this.itemGradientMatrix[i]);
                List<Entry> entries = this.itemListMap.get(i);
                for (Entry entry : entries) {
                    int u = entry.getUserId();
                    double ratingHat = MatrixUtil.innerProduct(this.userMatrix[u], this.itemMatrix[i]);
                    double residual = entry.getRating() - ratingHat;
                    for (int d = 1; d <= this.featureDimension; d++) {
                        this.itemGradientMatrix[i][d] +=
                                residual * this.userMatrix[u][d] - this.lambda * this.itemMatrix[i][d];
                    }
                }
            });
            // futures.add(future);
            this.itemFutures[index] = future;
            index += 1;
        }

        // 等待所有线程执行完成
        for (Future<?> future : this.itemFutures) {
            future.get(); // 等待线程执行完成
        }
    }

    public void singleUserHessianFreeConjugateGradientDescent() throws InterruptedException, BrokenBarrierException, ExecutionException {
        // List<Future<?>> futures = new ArrayList<>();
        int index = 0;
        for (Integer u : this.userSet) {
            Future<?> future = executorService.submit(() -> {
                MatrixUtil.reset(this.userIncrementMatrix[u]);
                MatrixUtil.copy(this.userGradientMatrix[u], this.userDirectionMatrix[u]);
                MatrixUtil.copy(this.userGradientMatrix[u], this.userVectorMatrix[u]);
                double preRau = 0.0D;
                double rau = MatrixUtil.getFrobeniusNorm(this.userDirectionMatrix[u]);
                for (int t = 1; t <= this.innerLoop; t++) {

                    if (Math.sqrt(rau) < this.tolerance) {
                        break;
                    }

                    singleUserHessianVectorProduct(u);

                    double alphaUp = rau;
                    double alphaDown = MatrixUtil.innerProduct(this.userVectorMatrix[u], this.userGaussNewtonMatrix[u]);
                    double alpha = alphaUp / alphaDown;

                    for (int d = 1; d <= this.featureDimension; d++) {
                        this.userIncrementMatrix[u][d] += alpha * this.userVectorMatrix[u][d];
                        this.userDirectionMatrix[u][d] -= alpha * this.userGaussNewtonMatrix[u][d];
                    }
                    preRau = rau;
                    rau = MatrixUtil.getFrobeniusNorm(this.userDirectionMatrix[u]);
                    double coefficient = rau / preRau;
                    for (int d = 1; d <= this.featureDimension; d++) {
                        this.userVectorMatrix[u][d] = this.userDirectionMatrix[u][d]
                                + coefficient * this.userVectorMatrix[u][d];
                    }
                }
            });
            // futures.add(future);
            this.userFutures[index] = future;
            index += 1;
        }

        // 等待所有线程执行完成
        for (Future<?> future : this.userFutures) {
            future.get(); // 等待线程执行完成
        }
    }
    public void singleItemHessianFreeConjugateGradientDescent() throws InterruptedException, ExecutionException {
        // List<Future<?>> futures = new ArrayList<>();
        int index = 0;
        for (Integer i : this.itemSet) {
            Future<?> future = executorService.submit(() -> {

                MatrixUtil.reset(this.itemIncrementMatrix[i]);
                MatrixUtil.copy(this.itemGradientMatrix[i], this.itemDirectionMatrix[i]);
                MatrixUtil.copy(this.itemGradientMatrix[i], this.itemVectorMatrix[i]);
                double preRau = 0.0D;
                double rau = MatrixUtil.getFrobeniusNorm(this.itemDirectionMatrix[i]);
                for (int t = 1; t <= this.innerLoop; t++) {

                    if (Math.sqrt(rau) < this.tolerance) {
                        break;
                    }

                    singleItemHessianVectorProduct(i);

                    double alphaUp = rau;
                    double alphaDown = MatrixUtil.innerProduct(this.itemVectorMatrix[i], this.itemGaussNewtonMatrix[i]);
                    double alpha = alphaUp / alphaDown;

                    for (int d = 1; d <= this.featureDimension; d++) {
                        this.itemIncrementMatrix[i][d] += alpha * this.itemVectorMatrix[i][d];
                        this.itemDirectionMatrix[i][d] -= alpha * this.itemGaussNewtonMatrix[i][d];
                    }

                    preRau = rau;
                    rau = MatrixUtil.getFrobeniusNorm(this.itemDirectionMatrix[i]);

                    double coefficient = rau / preRau;
                    for (int d = 1; d <= this.featureDimension; d++) {
                        this.itemVectorMatrix[i][d] = this.itemDirectionMatrix[i][d]
                                + coefficient * this.itemVectorMatrix[i][d];
                    }
                }
            });
            // futures.add(future);
            this.itemFutures[index] = future;
            index += 1;
        }

        // 等待所有线程执行完成
        for (Future<?> future : this.itemFutures) {
            future.get(); // 等待线程执行完成
        }
    }
    public void singleUserHessianVectorProduct(Integer u) {
        MatrixUtil.reset(this.userGaussNewtonMatrix[u]);
        List<Entry> entries = this.userListMap.get(u);
        for (Entry entry : entries) {
            int i = entry.getItemId();
            double jacobianVectorProduct = 0.0;
            for (int d = 1; d <= this.featureDimension; d++) {
                jacobianVectorProduct += (this.userVectorMatrix[u][d] * this.itemMatrix[i][d]);
            }
            for (int d = 1; d <= this.featureDimension; d++) {
                this.userGaussNewtonMatrix[u][d] += this.itemMatrix[i][d] * jacobianVectorProduct;
            }
        }
        for (int d = 1; d <= this.featureDimension; d++) {
            this.userGaussNewtonMatrix[u][d] += this.gamma * this.userVectorMatrix[u][d]
                    + this.lambda * this.userVectorMatrix[u][d] * this.userCount[u];
        }
    }

    public void singleItemHessianVectorProduct(Integer i) {
        MatrixUtil.reset(this.itemGaussNewtonMatrix[i]);
        List<Entry> entries = this.itemListMap.get(i);
        for (Entry entry : entries) {
            int u = entry.getUserId();
            double jacobianVectorProduct = 0.0;
            for (int d = 1; d <= this.featureDimension; d++) {
                jacobianVectorProduct += (this.userMatrix[u][d] * this.itemVectorMatrix[i][d]);
            }
            for (int d = 1; d <= this.featureDimension; d++) {
                this.itemGaussNewtonMatrix[i][d] += this.userMatrix[u][d] * jacobianVectorProduct;
            }
        }
        for (int d = 1; d <= this.featureDimension; d++) {
            this.itemGaussNewtonMatrix[i][d] += this.gamma * this.itemVectorMatrix[i][d]
                    + this.lambda * this.itemVectorMatrix[i][d] * this.itemCount[i];
        }
    }
    public void updateUserMatrix() throws ExecutionException, InterruptedException {
        // List<Future<?>> futures = new ArrayList<>();
        int index = 0;
        for (Integer u : this.userSet) {
            Future<?> future = executorService.submit(() -> {
                for (int d = 1; d <= this.featureDimension; d++) {
                    this.userMatrix[u][d] += this.learningRate * this.userIncrementMatrix[u][d];
                }
            });
            // futures.add(future);
            this.userFutures[index] = future;
            index += 1;
        }
        // 等待所有线程执行完成
        for (Future<?> future : this.userFutures) {
            future.get(); // 等待线程执行完成
        }
    }

    public void updateItemMatrix() throws ExecutionException, InterruptedException {
        // List<Future<?>> futures = new ArrayList<>();
        int index = 0;
        for (Integer i : this.itemSet) {
            Future<?> future = executorService.submit(() -> {
                for (int d = 1; d <= this.featureDimension; d++) {
                    this.itemMatrix[i][d] += this.learningRate * this.itemIncrementMatrix[i][d];
                }
            });
            // futures.add(future);
            this.itemFutures[index] = future;
            index += 1;
        }

        // 等待所有线程执行完成
        for (Future<?> future : this.itemFutures) {
            future.get(); // 等待线程执行完成
        }
    }


    public void convergenceAnalysis(int epoch) {
        double testRMSE = EvaluationUtil.rootMeanSquaredError(this.testEntryList, this.userMatrix, this.itemMatrix);
        double validationRMSE = EvaluationUtil.rootMeanSquaredError(this.validationEntryList, this.userMatrix, this.itemMatrix);
        String str = "The " + epoch + " epoch : " + "test-RMSE = " + testRMSE + " validation-RMSE = " + validationRMSE;
        this.modelOutputString.add(str);

        if ((this.validationBestRMSE - validationRMSE) <= Math.pow(10, -5)) {
            if (this.delayCount >= 10) {
                this.converge = true;
            } else {
                this.delayCount += 1;
            }
            this.testRMSE = testRMSE;
            this.validationRMSE = validationRMSE;
        } else {
            this.delayCount = 0;
            this.testRMSE = testRMSE;
            this.validationRMSE = validationRMSE;
            if (this.validationBestRMSE >= this.validationRMSE) {
                this.validationBestRMSE = validationRMSE;
                this.testBestRMSE = testRMSE;
                this.minRound = epoch;
                this.minTime = this.sumTime;
            }
        }

        System.out.println(str);
    }
}
