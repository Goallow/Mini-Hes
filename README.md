## Mini-Hes：A Parallelizable Second-order Latent Factor Analysis Model

![屏幕截图 2024-02-15 041013](https://github.com/Goallow/Mini-Hes/assets/23376459/7e909dbf-72f7-4ac5-b25b-17254fb461b3)

## Datasets
Link：https://pan.baidu.com/s/1J2GsTt7QCxCCDsKxf-d-4w?pwd=3nux 

Password：3nux 

## Installation
Step 1: Download the model and dataset.

Step 2: Modify the dataset path inside the Constant.java file.

Step 3: Run the Java file inside the 'test' package.

## Hyperparameter Optimization Guidelines

The damping term γ, regularization term coefficient λ, and tolerance coefficient τ of Mini-Hes are searched within the ranges {20, 40, 60, …, 100}, {0.0, 0.01, 0.02, …, 0.1}, and {0.1, 1.0}, respectively. In most cases, for Mini-Hes, the optimal values for γ, λ, and τ in Mini-Hes-based LFA are 60, 0.04, and 0.1, respectively.

The values of these hyperparameters can be modified by editing the Java file inside the 'test' package.
