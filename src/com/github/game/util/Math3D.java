package com.github.game.util;

/**
 * Generic 3D Math methods. These are fairly streamlined methods for efficiency.
 * 
 * @author David
 *
 */
public class Math3D {

	public static float[] createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz, float scale) {
		float x_angle = Utils.toRadians(rx);
		
		float x_c = Utils.cos(x_angle);
		float x_s = Utils.sin(x_angle);
	
		float y_angle = Utils.toRadians(ry);
	
		float y_c = Utils.cos(y_angle);
		float y_s = Utils.sin(y_angle);
	
		float z_angle = Utils.toRadians(rz);
		float z_c = Utils.cos(z_angle);
		float z_s = Utils.sin(z_angle);
	
		float temp1 = -x_s * -y_s;
		float temp2 = x_c * -y_s;
	
		float[] matrix = new float[16];
		matrix[0] = y_c * z_c * scale;
		matrix[1] = (temp1 * z_c + x_c * z_s) * scale;
		matrix[2] = (temp2 * z_c + x_s * z_s) * scale;
		matrix[4] = y_c * -z_s * scale;
		matrix[5] = (temp1 * -z_s + x_c * z_c) * scale;
		matrix[6] = (temp2 * -z_s + x_s * z_c) * scale;
		matrix[8] = y_s * scale;
		matrix[9] = -x_s * y_c * scale;
		matrix[10] = x_c * y_c * scale;
		matrix[12] = x;
		matrix[13] = y;
		matrix[14] = z;
		matrix[15] = 1.0f;				
		//zero: m03, m13, m23
		return matrix;
	}

}
