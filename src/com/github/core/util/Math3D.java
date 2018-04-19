package com.github.core.util;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Generic 3D Math methods. These are fairly streamlined methods for efficiency.
 * 
 * @author David
 *
 */
public class Math3D {

	/*
	 * Renderer Constants
	 */
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private static final float FOV_X_SCALE = (float) (1f / Utils.tan(Utils.toRadians(Math3D.FOV / 2f)));
	
	
	public static float[] createViewMatrix(Camera camera) {

		//Store vars on stack, rather than recalculating.
		float pitch = Utils.toRadians(camera.pitch);
		float yaw = Utils.toRadians(camera.yaw);
		

		float cos_pitch = Utils.cos(pitch);
		float sin_pitch = Utils.sin(pitch);

		float cos_yaw = Utils.cos(yaw);
		float sin_yaw = Utils.sin(yaw);
		
		//setup the viewing matrix for the camera.
		float[] matrix = new float[16];

		matrix[0] = cos_yaw;
		matrix[1] = -sin_pitch * -sin_yaw;
		matrix[2] = cos_pitch * -sin_yaw;
		matrix[5] = cos_pitch;
		matrix[6] = sin_pitch;
		matrix[8] = sin_yaw;
		matrix[9] = -sin_pitch * cos_yaw;
		matrix[10] = cos_pitch * cos_yaw;
		matrix[15] = 1.0f;

		
		float cameraX = camera.x;
		float cameraY = camera.y;
		float cameraZ = camera.z;

		matrix[12] += matrix[0] * -cameraX + matrix[8] * -cameraZ;
		matrix[13] += matrix[1] * -cameraX + matrix[5] * -cameraY + matrix[9] * -cameraZ;
		matrix[14] += matrix[2] * -cameraX + matrix[6] * -cameraY + matrix[10] * -cameraZ;
		
		return matrix;
	}
		
	public static float[] createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = FOV_X_SCALE * aspectRatio;
		float frustum_length = Math3D.FAR_PLANE - Math3D.NEAR_PLANE;
	
		
		float[] matrix = new float[16];
		matrix[0] = FOV_X_SCALE;
		matrix[5] = y_scale;
		matrix[10] = -((Math3D.FAR_PLANE + Math3D.NEAR_PLANE) / frustum_length);
		matrix[11] = -1;
		matrix[14] = -((2 * Math3D.NEAR_PLANE * Math3D.FAR_PLANE) / frustum_length);
		matrix[15] = 0;
		
		return matrix;
	}
		
	/**
	 * Calculates the mouse ray for the camera. Primary uses are in mouse picking and click detection.
	 * 
	 * @param camera The camera.
	 * @return The calculated mouse ray for the current position.
	 */
	public static Vector3f calculateMouseRay(Camera camera, float mouse_x, float mouse_y) {
		
		//normalize device coordinates and get the clip coords
		float normalized_clip_coord_x = (2.0f * mouse_x) / Display.getWidth() - 1f;
		float normalized_clip_coord_y = (2.0f * mouse_y) / Display.getHeight() - 1f;
		
		/*
		 * Convert clip coords to eye coords
		 */
		//Start with the generic calculations. These are identical to the calculations in the getViewProjection method.
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = Math3D.FOV_X_SCALE * aspectRatio;
		float frustum_length = Math3D.FAR_PLANE - Math3D.NEAR_PLANE;
	
		float temp = ((2 * Math3D.NEAR_PLANE * Math3D.FAR_PLANE) / frustum_length);
		
		//usually, we check if the determinant is zero, but that is mathematically impossible. 
		float determinant_inv = 1f / (Math3D.FOV_X_SCALE * y_scale * temp);
	
		float eye_coord_x = y_scale * temp * determinant_inv * normalized_clip_coord_x;
		float eye_coord_y = Math3D.FOV_X_SCALE * temp * determinant_inv * normalized_clip_coord_y;
	
		
		/*
		 * Create view matrix, to convert eye coords to world coords.
		 * We create the view matrix, then invert it and later transform it with the eye coords.
		 * We get the x/y/z into a vector and then normalize it.
		 */
		float pitch = Utils.toRadians(camera.pitch);
		float yaw = Utils.toRadians(camera.yaw);
	
		float cos_pitch = Utils.cos(pitch);
		float sin_pitch = Utils.sin(pitch);
	
		float cos_yaw = Utils.cos(yaw);
		float sin_yaw = Utils.sin(yaw);
	
		//precalculated square values to improve readability.
		float cos_pitch_sq = cos_pitch * cos_pitch;
		float sin_pitch_sq = sin_pitch * sin_pitch;
	
		float cos_yaw_sq = cos_yaw * cos_yaw;
		float sin_yaw_sq = sin_yaw * sin_yaw;
	
		/*
		 * invert and transform the matrix using the x and y values of the eyecoords. Due to the fact that a single ray is desired, a 16 value matrix is not used. An 8 value one is sufficient.
		 * This is subsequently added to a vector3f and normalized to cast the ray.
		 *  
		 */
		//usually, we check if the determinant is zero, but that is mathematically impossible. 
		float determinant_inv_ = 1f / (cos_yaw_sq * (cos_pitch_sq + sin_pitch_sq)
				+ sin_yaw_sq * (sin_pitch_sq + cos_pitch_sq));
	
		float x = ((sin_pitch_sq + cos_pitch_sq) * eye_coord_x * cos_yaw + sin_pitch * eye_coord_y * sin_yaw + cos_pitch * sin_yaw) * determinant_inv_;
		float y = (cos_pitch * eye_coord_y - sin_pitch) * determinant_inv_ * (cos_yaw_sq + sin_yaw_sq);
		float z = ((sin_pitch_sq + cos_pitch_sq) * eye_coord_x * sin_yaw - sin_pitch * eye_coord_y * cos_yaw - cos_pitch * cos_yaw) * determinant_inv_;
		
		float l = Utils.sqrt(x * x + y * y + z * z);
	
		return new Vector3f(x / l, y / l, z / l);
	}
	
	/**
	 * Creates a transformation matrix for a given x/y/z and corresponding radial angles rx/ry/rz.
	 * First a translation is performed for the x/y/z, followed by independent matrix rotations on
	 * each axis. Lastly, the matrix is scaled.
	 * @param x
	 * @param y
	 * @param z
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param scale Scale of the artifact. Usually 1.
	 * @return
	 */
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
