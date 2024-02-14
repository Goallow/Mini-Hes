package edu.dgut.algorithm;

import edu.dgut.pojo.Entry;
import edu.dgut.util.EvaluationUtil;
import edu.dgut.util.FileUtil;
import edu.dgut.util.MatrixUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @Author Goallow
 * @Date 2021/11/29 15:27
 * @Version 1.0
 */
public class SecondOrderLatentFactorModel {

    private int maxUserId;
    private int maxItemId;

    private double[][] userMatrix;
    private double[][] itemMatrix;

    private double[][] userOriginMatrix;
    private double[][] itemOriginMatrix;

    /**
     * 用户隐特征矩阵梯度
     */
    private double[][] userGradientMatrix;
    /**
     * 物品隐特征矩阵梯度
     */
    private double[][] itemGradientMatrix;

    private double[][] userPreGradientMatrix;
    private double[][] itemPreGradientMatrix;

    private double[][] userDeltaGradientMatrix;
    private double[][] itemDeltaGradientMatrix;

    private double[][] userMomentumMatrix;
    private double[][] itemMomentumMatrix;
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
    private int featureDimension;

    private List<String> modelOutputString;

    private Map<Integer, List<Entry>> userListMap;

    private Map<Integer, List<Entry>> itemListMap;

    /**
     * 保存用户u在数据集中的出现次数
     */
    private Map<Integer, Integer> userMap;
    /** 保存物品i在数据集中的出现次数 */
    ;
    private Map<Integer, Integer> itemMap;

    private List<double[][]> hyperMatrix;

    private boolean converge;
    private double RMSE;
    private double validationRMSE;
    private double validationBestRMSE;

    private double testRMSE;

    private double testBestRMSE;

    private double minRound;

    private double sumRound;

    private double startTime;

    private double endTime;

    private double realEndTime;

    private double minTime;

    private double minEndTime;
    private double sumTime;

    private int delayCount;


    ThreadPoolExecutor userExecutor;
    ThreadPoolExecutor itemExecutor;



    CyclicBarrier userCyclicBarrier;

    CyclicBarrier itemCyclicBarrier;

    public SecondOrderLatentFactorModel() throws IOException {
        /** 初始化数据集合 */
        this.trainEntryList = FileUtil.loadDatasetFile(Constant.TRAIN_FILE_NAME, Constant.SEPARATOR);
        this.testEntryList = FileUtil.loadDatasetFile(Constant.TEST_FILE_NAME, Constant.SEPARATOR);
        this.validationEntryList = FileUtil.loadDatasetFile(Constant.VALIDATION_FILE_NAME, Constant.SEPARATOR);
        /** 获取最大用户和物品的Id */
        this.maxUserId = FileUtil.getMaxUserId(this.trainEntryList, this.testEntryList, this.validationEntryList);
        this.maxItemId = FileUtil.getMaxItemId(this.trainEntryList, this.testEntryList, this.validationEntryList);
        /** 初始化用户矩阵和物品矩阵 */
        this.userMatrix = new double[this.maxUserId + 1][this.featureDimension + 1];
        this.itemMatrix = new double[this.maxItemId + 1][this.featureDimension + 1];
        this.userMatrix = MatrixUtil.initializeMatrix(this.maxUserId, Constant.FEATURE_DIMENSION,
                Constant.RATING_LOWER_BOUND, Constant.RATING_UPPER_BOUND);
        this.itemMatrix = MatrixUtil.initializeMatrix(this.maxItemId, Constant.FEATURE_DIMENSION,
                Constant.RATING_LOWER_BOUND, Constant.RATING_UPPER_BOUND);

        /** 初始化用户梯度矩阵和物品梯度矩阵 */
        this.userGradientMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemGradientMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];

        this.userDirectionMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemDirectionMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];
        /** 初始化过往梯度矩阵 */
        this.userPreGradientMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemPreGradientMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];
        /** 初始化动量梯度矩阵*/
        this.userMomentumMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemMomentumMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];
        /** 初始化增量矩阵*/
        this.userIncrementMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemIncrementMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];
        /** 初始化高斯牛顿矩阵*/
        this.userGaussNewtonMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemGaussNewtonMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];

        /** 初始化任意向量 */
        this.userVectorMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemVectorMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];

        this.userDeltaGradientMatrix = new double[this.maxUserId + 1][Constant.FEATURE_DIMENSION + 1];
        this.itemDeltaGradientMatrix = new double[this.maxItemId + 1][Constant.FEATURE_DIMENSION + 1];
        /** 初始化相关数 */
        this.userCount = new int[this.maxUserId + 1];
        this.itemCount = new int[this.maxItemId + 1];
        this.userSet = new HashSet<>();
        this.itemSet = new HashSet<>();
        // this.userSet = FileUtil.getUserSet(this.trainEntryList, this.testEntryList, this.validationEntryList);
        // this.itemSet = FileUtil.getItemSet(this.trainEntryList, this.testEntryList, this.validationEntryList);
        for (Entry entry : this.trainEntryList) {
            this.userSet.add(entry.getUserId());
            this.itemSet.add(entry.getItemId());
            this.userCount[entry.getUserId()] += 1;
            this.itemCount[entry.getItemId()] += 1;
        }

        this.userListMap = FileUtil.getUserListMap(this.trainEntryList);
        this.itemListMap = FileUtil.getItemListMap(this.trainEntryList);
        this.converge = false;
        this.gamma = Constant.GAMMA;
        this.tolerance = Constant.TOLERANCE;
        this.lambda = Constant.LAMBDA;
        this.learningRate = Constant.LEARNING_RATE;
        this.modelOutputString = new ArrayList<>();
        this.RMSE = Math.pow(10, 10);
        this.validationRMSE = Math.pow(10, 10);
        this.testRMSE = Math.pow(10, 10);
        this.validationBestRMSE = Math.pow(10, 10);
        this.testBestRMSE = Math.pow(10, 10);
        this.delayCount = 1;
        this.minRound = 0;
        this.sumRound = 0;
    }

    public SecondOrderLatentFactorModel(double gamma, double tolerance, double lambda, List<String> modelOutputString) throws IOException {
        this();
        this.gamma = gamma;
        this.tolerance = tolerance;
        this.lambda = lambda;
        this.modelOutputString = modelOutputString;
    }

    public SecondOrderLatentFactorModel(double gamma, double tolerance, double lambda, double learningRate, List<String> modelOutputString, double[][] userOriginMatrix, double[][] itemOriginMatrix)
            throws IOException {
        this();
        this.gamma = gamma;
        this.tolerance = tolerance;
        this.lambda = lambda;
        this.learningRate = learningRate;
        this.modelOutputString = modelOutputString;
        this.userOriginMatrix = userOriginMatrix;
        this.itemOriginMatrix = itemOriginMatrix;
        MatrixUtil.copy(this.userOriginMatrix, this.userMatrix);
        MatrixUtil.copy(this.itemOriginMatrix, this.itemMatrix);
    }

    public double[] run() throws InterruptedException {
        int t = 1;
        this.startTime = System.currentTimeMillis();
        this.realEndTime = 0.0;
        this.minTime = 0.0;
        this.endTime = 0.0;
        this.sumTime = 0.0;
        double[] results = new double[9];
        while ((t <= Constant.MAX_TRAINING_ROUND) && (!converge)) {
            this.startTime = System.currentTimeMillis();
            this.computeGradient();
            this.hessianFreeConjugateGradientDescent();
            this.update();
            this.endTime = System.currentTimeMillis();
            this.sumTime += (this.endTime - this.startTime) / 1000;
            this.sumRound = t;
            this.convergenceAnalysis(t);
            t += 1;
        }

        results[1] = this.validationRMSE;
        results[2] = this.testRMSE;
        results[3] = this.validationBestRMSE;
        results[4] = this.testBestRMSE;
        results[5] = this.minTime;
        results[6] = this.sumTime;
        results[7] = this.minRound;
        results[8] = this.sumRound;
        System.out.println("运行时间(s) = " + sumTime);
        return results;
    }

    public void computeGradient() {
        MatrixUtil.reset(this.userGradientMatrix);
        MatrixUtil.reset(this.itemGradientMatrix);
        for (Entry entry : this.trainEntryList) {
            int u = entry.getUserId();
            int i = entry.getItemId();
            double ratingHat = MatrixUtil.innerProduct(entry.getUserId(), entry.getItemId(),
                    this.userMatrix, this.itemMatrix);
            entry.setRatingHat(ratingHat);
            double residual = entry.getRating() - ratingHat;
            for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                this.userGradientMatrix[u][d] += residual * this.itemMatrix[i][d] - this.lambda * this.userMatrix[u][d];
                this.itemGradientMatrix[i][d] += residual * this.userMatrix[u][d] - this.lambda * this.itemMatrix[i][d];
            }
        }
    }


    public void hessianFreeConjugateGradientDescent() {

        MatrixUtil.reset(this.userIncrementMatrix);
        MatrixUtil.reset(this.itemIncrementMatrix);
        MatrixUtil.reset(this.userDirectionMatrix);
        MatrixUtil.reset(this.itemDirectionMatrix);
        MatrixUtil.copy(this.userGradientMatrix, this.userDirectionMatrix);
        MatrixUtil.copy(this.itemGradientMatrix, this.itemDirectionMatrix);

        double[] rau = new double[Constant.K + 1];
        rau[0] = MatrixUtil.getFrobeniusNorm(this.userDirectionMatrix)
                + MatrixUtil.getFrobeniusNorm(this.itemDirectionMatrix);
        for (int t = 1; t <= Constant.K; t++) {
            if (Math.sqrt(rau[t - 1]) < this.tolerance) {
                break;
            }
            if (t == 1) {
                MatrixUtil.copy(this.userDirectionMatrix, this.userVectorMatrix);
                MatrixUtil.copy(this.itemDirectionMatrix, this.itemVectorMatrix);
            } else {
                double coefficient = rau[t - 1] / rau[t - 2];
                for (Integer u : this.userSet) {
                    for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                        this.userVectorMatrix[u][d] = this.userDirectionMatrix[u][d]
                                + coefficient * this.userVectorMatrix[u][d];
                    }
                }
                for (Integer i : this.itemSet) {
                    for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                        this.itemVectorMatrix[i][d] = this.itemDirectionMatrix[i][d]
                                + coefficient * this.itemVectorMatrix[i][d];
                    }
                }
            }
            hessianVectorProduct();
            double alpha = 0.0;
            double alphaUp = rau[t - 1];
            double alphaDown = MatrixUtil.innerProduct(this.userVectorMatrix, this.userGaussNewtonMatrix)
                    + MatrixUtil.innerProduct(this.itemVectorMatrix, this.itemGaussNewtonMatrix);
            if (alphaDown != 0) {
                alpha = alphaUp / alphaDown;
            } else {
                alpha = 0.00001;
            }

            for (Integer u : this.userSet) {
                for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                    this.userIncrementMatrix[u][d] += alpha * this.userVectorMatrix[u][d];
                    this.userDirectionMatrix[u][d] -= alpha * this.userGaussNewtonMatrix[u][d];
                }
            }

            for (Integer i : this.itemSet) {
                for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                    this.itemIncrementMatrix[i][d] += alpha * this.itemVectorMatrix[i][d];
                    this.itemDirectionMatrix[i][d] -= alpha * this.itemGaussNewtonMatrix[i][d];
                }
            }

            rau[t] = MatrixUtil.getFrobeniusNorm(this.userDirectionMatrix)
                    + MatrixUtil.getFrobeniusNorm(this.itemDirectionMatrix);
        }
    }


    public void hessianVectorProduct() {

        MatrixUtil.reset(this.userGaussNewtonMatrix);
        MatrixUtil.reset(this.itemGaussNewtonMatrix);
        for (Entry entry : this.trainEntryList) {
            int u = entry.getUserId();
            int i = entry.getItemId();
            double jacobianVectorProduct = 0.0;
            for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                jacobianVectorProduct += (this.userVectorMatrix[u][d] * this.itemMatrix[i][d])
                        + (this.userMatrix[u][d] * this.itemVectorMatrix[i][d]);
            }
            for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                this.userGaussNewtonMatrix[u][d] += this.itemMatrix[i][d] * jacobianVectorProduct;
                this.itemGaussNewtonMatrix[i][d] += this.userMatrix[u][d] * jacobianVectorProduct;
            }
        }

        for (Integer u : this.userSet) {
            for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                this.userGaussNewtonMatrix[u][d] += this.gamma * this.userVectorMatrix[u][d]
                        + this.lambda * this.userVectorMatrix[u][d] * this.userCount[u];
            }
        }

        for (Integer i : this.itemSet) {
            for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                this.itemGaussNewtonMatrix[i][d] += this.gamma * this.itemVectorMatrix[i][d]
                        + this.lambda * this.itemVectorMatrix[i][d] * this.itemCount[i];
            }
        }
    }


    public void update() {
        for (Integer u : this.userSet) {
            for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                this.userMatrix[u][d] += this.learningRate * this.userIncrementMatrix[u][d];
            }
        }
        for (Integer i : itemSet) {
            for (int d = 1; d <= Constant.FEATURE_DIMENSION; d++) {
                this.itemMatrix[i][d] += this.learningRate * this.itemIncrementMatrix[i][d];
            }
        }
    }

    public void convergenceAnalysis(int epoch) {
        for (Entry entry : this.testEntryList) {
            int u = entry.getUserId();
            int i = entry.getItemId();
            double[] pRow = MatrixUtil.getRowVector(this.userMatrix, u);
            double[] qRow = MatrixUtil.getRowVector(this.itemMatrix, i);
            double estimation = MatrixUtil.innerProduct(pRow, qRow);
            entry.setRatingHat(estimation);
        }

        for (Entry entry : this.validationEntryList) {
            int u = entry.getUserId();
            int i = entry.getItemId();
            double[] pRow = MatrixUtil.getRowVector(this.userMatrix, u);
            double[] qRow = MatrixUtil.getRowVector(this.itemMatrix, i);
            double estimation = MatrixUtil.innerProduct(pRow, qRow);
            entry.setRatingHat(estimation);
        }
        double testRMSE = EvaluationUtil.rootMeanSquaredError(this.testEntryList);
        double validationRMSE = EvaluationUtil.rootMeanSquaredError(this.validationEntryList);
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


    public void setUserMatrix(double[][] userOriginMatrix) {
        MatrixUtil.copy(userOriginMatrix, this.userMatrix);
    }

    public void setItemMatrix(double[][] itemOriginMatrix) {
        MatrixUtil.copy(itemOriginMatrix, this.itemMatrix);
    }
}
