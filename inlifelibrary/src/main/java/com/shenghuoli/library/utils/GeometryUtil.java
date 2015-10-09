package com.shenghuoli.library.utils;

public class GeometryUtil {

    /**
     * 计算两个点之间的距离
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float pointDistance(float x1, float y1, float x2, float y2) {
		float deltaX = x1 - x2;
		float deltaY = y1 - y2;
		return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * 计算一条线段的倾斜角
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return 角度
     */
    public static float calculateDegree(float x1, float y1, float x2, float y2) {
		double deltaX = x2 - x1;
		double deltaY = y2 - y1;
		double a = Math.atan2(deltaY, deltaX);
		return (float) Math.toDegrees(a);
    }

    /**
     * 计算向量与纵轴正方向的逆时针夹角
     * 
     * @param vectorX
     * @param vectorY
     * @return 弧度
     */
    public static double vectorDegree(float vectorX, float vectorY) {
		double angle = Math.asin(vectorX / pointDistance(vectorX, vectorY, 0, 0));
		if (vectorY < 0) {
			if (vectorX > 0) {
			angle = Math.PI - angle;
			} else {
			angle = -Math.PI - angle;
			}
		}
		return angle;
    }
}
