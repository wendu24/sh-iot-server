package com.ruoyi.business.util;

import com.ruoyi.business.domain.StatHourDO;
import com.ruoyi.business.vo.scatter.Point2D;
import com.ruoyi.business.vo.scatter.ValveAndTemperatureVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 抽稀算法
 */
public class LTTBDownsampler {

    /**
     * 计算三点形成的三角形面积。
     * 使用向量叉积公式: Area = 0.5 * |x1(y2 - y3) + x2(y3 - y1) + x3(y1 - y2)|
     */
    private static double triangleArea(Point2D p1, Point2D p2, Point2D p3) {
        return Math.abs(
                p1.getX() * (p2.getY() - p3.getY()) +
                        p2.getX() * (p3.getY() - p1.getY()) +
                        p3.getX() * (p1.getY() - p2.getY())
        ) / 2.0;
    }

    /**
     * LTTB 抽稀算法主函数。
     *
     * @param dataList 原始数据列表 (必须是已按 X 轴排序的)
     * @param threshold 抽稀后目标数据量 K
     * @return 抽稀后的数据列表
     */
    public static List<ValveAndTemperatureVO> downsample(List<ValveAndTemperatureVO> dataList, int threshold) {
        int n = dataList.size();

        if (threshold >= n || threshold <= 2) {
            return new ArrayList<>(dataList); // 不抽稀或阈值太小
        }

        List<ValveAndTemperatureVO> sampled = new ArrayList<>(threshold);

        // 1. 始终保留第一个点
        sampled.add(dataList.get(0));

        // 2. 计算每个桶的大小 (浮点数)
        // K-2 是要采样的中间桶的数量
        double bucketSize = (double) (n - 2) / (threshold - 2);

        // 3. 迭代抽样，处理中间的 (K-2) 个点
        for (int i = 0; i < threshold - 2; i++) {
            // 计算当前桶的起始索引和结束索引 (包含)
            int bucketStart = (int) Math.floor(i * bucketSize) + 1;
            int bucketEnd = (int) Math.floor((i + 1) * bucketSize) + 1;

            // 确保最后一个桶正确覆盖到倒数第二个点
            if (i == threshold - 3) {
                bucketEnd = n - 2;
            }

            // 4. 计算下一个桶的平均点 (中心点)
            double avgX = 0;
            double avgY = 0;
            int nextBucketCount = 0;

            int nextBucketStart = bucketEnd;
            int nextBucketEnd = (int) Math.floor((i + 2) * bucketSize) + 1;
            if (nextBucketEnd > n - 1) {
                nextBucketEnd = n - 1;
            }

            // 如果是倒数第二个桶，下一个“平均点”就是最后一个点
            if (i == threshold - 3) {
                Point2D lastPoint = dataList.get(n - 1);
                avgX = lastPoint.getX();
                avgY = lastPoint.getY();
                nextBucketCount = 1; // 仅一个点
            } else {
                // 计算下一个桶的平均值
                for (int j = nextBucketStart; j < nextBucketEnd; j++) {
                    avgX += dataList.get(j).getX();
                    avgY += dataList.get(j).getY();
                    nextBucketCount++;
                }
                if (nextBucketCount > 0) {
                    avgX /= nextBucketCount;
                    avgY /= nextBucketCount;
                }
            }

            // 5. 在当前桶内，找到能形成最大三角形面积的点
            double maxArea = -1;
            int maxAreaIndex = -1;
            Point2D prevPoint = sampled.get(sampled.size() - 1); // 上一个保留点
            // 创建一个临时的 Point2D 对象作为平均点
            Point2D avgPoint = new ValveAndTemperatureVO(BigDecimal.valueOf(avgY), BigDecimal.valueOf(avgX)); // 注意这里 Y/X 的传入顺序

            for (int j = bucketStart; j <= bucketEnd; j++) {
                Point2D currentPoint = dataList.get(j);
                double area = triangleArea(prevPoint, currentPoint, avgPoint);

                if (area > maxArea) {
                    maxArea = area;
                    maxAreaIndex = j;
                }
            }

            // 6. 将面积最大的点保留下来
            if (maxAreaIndex != -1) {
                sampled.add(dataList.get(maxAreaIndex));
            }
        }

        // 7. 始终保留最后一个点
        sampled.add(dataList.get(n - 1));

        return sampled;
    }

    /**
     * 外部调用的主方法：负责排序和调用 LTTB 抽稀。
     */
    public static List<ValveAndTemperatureVO> downsampleData(List<ValveAndTemperatureVO> originalData, int threshold) {
        if (originalData == null || originalData.isEmpty()) {
            return new ArrayList<>();
        }

        // LTTB 算法在 X 轴（阀门开度）排序后效果最佳
        List<ValveAndTemperatureVO> sortedData = new ArrayList<>(originalData);
        sortedData.sort(Comparator.comparing(ValveAndTemperatureVO::getX));

        return downsample(sortedData, threshold);
    }

    // --- 示例用法 ---
    public static void main(String[] args) {
        // 1. 模拟原始数据 (假设有 10000 条)
        List<ValveAndTemperatureVO> data = new ArrayList<>();
        Random rand = new Random();
        int initialSize = 10000;

        // 模拟一个带趋势和随机波动的阀门-温度数据
        for (int i = 0; i < initialSize; i++) {
            // 阀门开度（X轴）从 10 线性增加到 90，并带随机波动
            double valvePos = 10 + (double)i / initialSize * 80 + rand.nextDouble() * 5;

            // 温度（Y轴）随阀门变化，并带较大随机性，模拟关键特征
            double temp = 20.0 + Math.sin(i / 100.0) * 3.0 + (valvePos / 100.0) * 5.0 + rand.nextDouble() * 1.0;

            data.add(new ValveAndTemperatureVO(BigDecimal.valueOf(temp), BigDecimal.valueOf(valvePos)));
        }

        System.out.println("原始数据量: " + data.size());

        // 2. 设置目标抽稀点数 (例如：只保留 500 个点)
        int targetThreshold = 500;
        List<ValveAndTemperatureVO> downsampledData = downsampleData(data, targetThreshold);

        // 3. 打印抽稀后的结果
        System.out.println("目标数据量: " + targetThreshold);
        System.out.println("抽稀后数据量: " + downsampledData.size());

        System.out.println("\n抽稀后的部分关键点 (X:阀门, Y:温度):");
        // 打印前 5 个和后 5 个点
        for (int i = 0; i < Math.min(5, downsampledData.size()); i++) {
            System.out.println("点 " + (i+1) + ": " + downsampledData.get(i));
        }
        System.out.println("...");
        for (int i = downsampledData.size() - 5; i < downsampledData.size(); i++) {
            System.out.println("点 " + (i+1) + ": " + downsampledData.get(i));
        }
    }

}
