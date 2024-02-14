package edu.dgut.util;

import edu.dgut.algorithm.Constant;

import java.util.Random;

/**
 * @Author Goallow
 * @Date 2021/11/15 10:15
 * @Version 1.0
 */
public class MatrixUtil {

    public static void initializeMatrix(double[][] matrix) {
        Random random = new Random(1639985730934L);
        // Random random = new Random(System.currentTimeMillis());
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                matrix[i][j] = 0.004 * random.nextDouble();
            }
        }
    }

    /**
     * 初始化矩阵
     * @param row 行
     * @param column 列
     * @return 矩阵
     */
    public static double[][] initializeMatrix(int row, int column, double lowerBound, double upperBound) {
        double[][] matrix = new double[row + 1][column + 1];
        /**
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= column; j++) {
               matrix[i][j] = lowerBound + Math.random() * (upperBound - lowerBound);
            }
        }*/
        Random random = new Random(1639985730934L);
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= column; j++) {
                matrix[i][j] = 0.004 * random.nextDouble();
            }
        }
        return matrix;
    }


    /**
     * 初始化向量
     * @param dimension 向量的维度
     * @return
     */
    public static double[] initializeVector(int dimension, double value) {
        double[] vector = new double[dimension + 1];
        for (int i = 1; i <= dimension; i++) {
            vector[i] = value;
        }
        return vector;
    }

    public static double innerProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 1; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public static double innerProduct(int userId, int itemId, double[][] userMatrix, double[][] itemMatrix) {
        double sum = 0.0;
        for (int i = 1; i < userMatrix[0].length; i++) {
            sum += userMatrix[userId][i] * itemMatrix[itemId][i];
        }
        return sum;
    }

    public static double innerProduct(double[][] firstMatrix, double[][] secondMatrix) {
        double sum = 0.0;
        for (int i = 1; i < firstMatrix.length; i++) {
            for (int j = 1; j < firstMatrix[0].length; j++) {
                sum += firstMatrix[i][j] * secondMatrix[i][j];
            }
        }
        return sum;
    }
    public static double[] getRowVector(double[][] matrix, int row) {
        double[] vector = new double[matrix[0].length];
        for (int i = 1; i < matrix[0].length; i++) {
            vector[i] = matrix[row][i];
        }
        return vector;
    }

    /**
     * 设置向量元素的值
     * @param vector 向量
     * @param value 设置的值
     */
    public static void setVectorElementValue(double[] vector, double value) {
        for (int i = 1; i < vector.length; i++) {
            vector[i] = value;
        }
    }

    /**
     * 将 X & Y 矩阵的第K维信息缓存进入 X_C & Y_C 向量中
     * @param matrix x 或 Y 矩阵
     * @param vector X_C 或 Y_C
     * @param dimension 第k维
     */
    public static void cacheInfo(double[][] matrix, double[] vector, int dimension) {
        for (int i = 1; i < matrix.length; i++) {
            vector[i] = matrix[i][dimension];
        }
    }


    public static double getMaxValue(double[][] matrix) {
        double maxValue = matrix[1][1];
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                if (maxValue < matrix[i][j]) {
                    maxValue = matrix[i][j];
                }
            }
        }
        return maxValue;
    }

    public static void reset(double[] vector) {
        for (int i = 1; i < vector.length; i++) {
            vector[i] = 0;
        }
    }

    public static void reset(double[][] matrix) {
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    public static void copy(double[] originVector, double[] destVector) {
        System.arraycopy(originVector, 1, destVector, 1, Math.min(originVector.length - 1, destVector.length - 1));
    }

    public static void copy(double[][] originMatrix, double[][] destMatrix) {
        for (int i = 1; i < destMatrix.length; i++) {
            for (int j = 1; j < destMatrix[0].length; j++) {
                destMatrix[i][j] = originMatrix[i][j];
            }
        }
    }

    public static double getFrobeniusNorm(double[] vector) {
        double sum = 0.0;
        for (int i = 1; i < vector.length; i++) {
                sum += Math.pow(vector[i], 2);
        }
        return sum;
    }

    public static double getFrobeniusNorm(double[][] matrix) {
        double sum = 0.0;
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                sum += Math.pow(matrix[i][j], 2);
            }
        }
        return sum;
    }

    public static void update(double[][] originMatrix, double[][] destMatrix, double originValue, double destValue) {
        for (int i = 1; i < destMatrix.length; i++) {
            for (int j = 1; j < destMatrix[0].length; j++) {
                destMatrix[i][j] = (originValue * originMatrix[i][j]) + (destValue * destMatrix[i][j]);
            }
        }
    }

}
