package edu.dgut.algorithm;

/**
 * @Author Goallow
 * @Date 2021/11/15 9:35
 * @Version 1.0
 */
public interface Constant {

    String PATH = "/home/dgutbigdata/Jialiang/datasets/20m_v1/";
    String EM_FILE_PATH = "D:\\Documents\\IdeaProjects\\Data\\EM\\";
    String JESTER_FILE_PATH = "D:\\Documents\\IdeaProjects\\Data\\JESTER\\";
    String WSDREAM_FILE_PATH = "D:\\Documents\\IdeaProjects\\Data\\WSDREAM\\";

    String SAVE_FILE_PATH = "/home/dgutbigdata/Jialiang/results/20m_v1/Momentum/";
    String SAVE_EM_FILE_PATH = "D:\\Documents\\IdeaProjects\\Data\\Result\\ASOLF\\2022_3\\EM\\";
    String SAVE_JESTER_FILE_PATH = "D:\\Documents\\IdeaProjects\\Data\\Result\\ASOLF\\2022_3\\JESTER\\";
    String SAVE_WSDREAM_FILE_PATH = "D:\\Documents\\IdeaProjects\\Data\\Result\\ASOLF\\2022_3\\WSDREAM\\";

    String TRAIN_FILE_NAME = "20m_v1_train_8tv2t_6t2v.txt";
    String TRAIN_EM_FILE_NAME = "EM_train_8tv2t_6t2v.txt";
    String TRAIN_JESTER_FILE_NAME = "Jester_train_8tv2t_6t2v.txt";
    String TRAIN_WSDREAM_FILE_NAME = "WSDREAM_train_8tv2t_6t2v.txt";


    String VALIDATION_FILE_NAME = "20m_v1_validation_8tv2t_6t2v.txt";
    String VALIDATION_EM_FILE_NAME = "EM_validation_8tv2t_6t2v.txt";
    String VALIDATION_JESTER_FILE_NAME = "Jester_validation_8tv2t_6t2v.txt";
    String VALIDATION_WSDREAM_FILE_NAME = "WSDREAM_validation_8tv2t_6t2v.txt";

    String TEST_FILE_NAME = "20m_v1_test_8tv2t_6t2v.txt";
    String TEST_EM_FILE_NAME = "EM_test_8tv2t_6t2v.txt";
    String TEST_JESTER_FILE_NAME = "Jester_test_8tv2t_6t2v.txt";
    String TEST_WSDREAM_FILE_NAME = "WSDREAM_test_8tv2t_6t2v.txt";

    String TRAIN_VALIDATION_FILE_NAME = "10m_v2_train_validation_8tv2t_6t2v.txt";
    String TRAIN_VALIDATION_EM_FILE_NAME = "EM_train_validation_8tv2t_6t2v.txt";
    String TRAIN_VALIDATION_JESTER_FILE_NAME = "Jester_train_validation_8tv2t_6t2v.txt";
    String TRAIN_VALIDATION_WSDREAM_FILE_NAME = "WSDREAM_train_validation_8tv2t_6t2v.txt";

    String DATASET_FILE_NAME = "ratings.txt";

    String SEPARATOR = ",";

    double RATING_LOWER_BOUND = 0.0;

    double RATING_UPPER_BOUND = 0.004;

    int FEATURE_DIMENSION = 20;

    int MAX_TRAINING_ROUND = 500;

    int K = 20;

    double GAMMA = 104;

    double TOLERANCE = 275;

    double LAMBDA = 0.03;

    double LEARNING_RATE = Math.pow(2, 0);

    // double GAMMA_MAX = 500;
    double GAMMA_MAX = 100.0;
    double GAMMA_MIN = 100.0;
    //double GAMMA_MIN = Math.pow(10, -5);

    double TOLERANCE_MAX = 100.0;
    //double TOLERANCE_MAX = 1000;
    double TOLERANCE_MIN = 100.0;
    //double TOLERANCE_MIN = Math.pow(10, -5);

    /** 新增 */
    double LEARNING_RATE_MAX = Math.pow(2, -8);
    // double LEARNING_RATE_MIN = Math.pow(2, -4);
    double LEARNING_RATE_MIN = 1.0;

    double FEATURE_DIMENSION_MAX = 50;
    double FEATURE_DIMENSION_MIN = 10;

    double LAMBDA_MAX = 1.0;
    double LAMBDA_MIN = 0.0;

    int THREAD_NUM = 10;

    int SEARCH_DIMENSION = 4;

    double DELTA_POSITION_VALUE = Math.pow(10, 0);

    double DELTA_FITNESS_VALUE = Math.pow(10, -5);

    int DELAY_COUNT = 10;

    double[] DAMPING_LIST = {20.0, 40.0, 60.0, 80.0, 100.0};

    double[] TOLERANCE_LIST = {20.0, 40.0, 60.0, 80.0, 100.0};

    double[] REGULARIZATION_LIST = {0.04};

    double PROPORTION = 0.5;

    double INTEGRATION = 0.45;

    double DIFFERENTIATION = 0.00001D;
}
