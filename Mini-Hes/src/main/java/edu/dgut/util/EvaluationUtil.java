package edu.dgut.util;

import edu.dgut.algorithm.Constant;
import edu.dgut.pojo.Entry;

import java.io.IOException;
import java.util.List;

/**
 * @Author Goallow
 * @Date 2021/11/15 16:10
 * @Version 1.0
 */
public class EvaluationUtil {
    static int count = 0;
    public static double rootMeanSquaredError(List<Entry> entryList) {
        double sumOfDeltaRatingSquared = 0.0;
        for (Entry entry : entryList) {
            sumOfDeltaRatingSquared += Math.pow(entry.getRating() - entry.getRatingHat(), 2);
        }
        return Math.sqrt(sumOfDeltaRatingSquared / entryList.size());
    }

    public static double meanAbsoluteError(List<Entry> entryList) {
        double sumOfDeltaRatingAbsolute = 0.0;
        for (Entry entry : entryList) {
            sumOfDeltaRatingAbsolute += Math.abs(entry.getRating() - entry.getRatingHat());
        }
        return (sumOfDeltaRatingAbsolute / entryList.size());
    }

    public static double meanAbsoluteError(List<Entry> entryList, double[][] matrixP, double[][] matrixQ) {
        double sumOfDeltaRatingAbsolute = 0.0;
        for (Entry entry : entryList) {
            double estimatedRating = MatrixUtil.innerProduct(matrixP[entry.getUserId()], matrixQ[entry.getItemId()]);
            sumOfDeltaRatingAbsolute += Math.abs(entry.getRating() - estimatedRating);
        }
        return (sumOfDeltaRatingAbsolute / entryList.size());
    }
    public static double rootMeanSquaredError(List<Entry> entryList, double[][] matrixP, double[][] matrixQ) {
        double sumOfDeltaRatingSquared = 0.0;
        for (Entry entry : entryList) {
            double estimatedRating = MatrixUtil.innerProduct(matrixP[entry.getUserId()], matrixQ[entry.getItemId()]);
            sumOfDeltaRatingSquared += Math.pow((entry.getRating() - estimatedRating), 2);
        }
        return Math.sqrt(sumOfDeltaRatingSquared / entryList.size());
    }


    public static boolean compare(double a, double b) {
        if (a < b) {
            return true;
        }
        return false;
    }

}
