package com.countgandi.engine.utils;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.countgandi.engine.Display;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.objects.Entity;
import com.countgandi.engine.objects.Light;

public class Maths {

	public static final int FRUSTUM_NEAR = 0;
	public static final int FRUSTUM_FAR = 1;
	public static final int FRUSTUM_LEFT = 2;
	public static final int FRUSTUM_RIGHT = 3;
	public static final int FRUSTUM_UP = 4;
	public static final int FRUSTUM_DOWN = 5;

	public static Matrix4f createTransformationMatrix(Vector2f translation, float rx, float ry, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		Matrix4f.rotate(rx, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(ry, new Vector3f(0, 1, 0), matrix, matrix);
		return matrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static float distance(Vector3f v1, Vector3f v2) {
		return (float) Math.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) + (v1.z - v2.z) * (v1.z - v2.z));
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(rx, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(ry, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rz, new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createProjectionMatrix(float near, float far, float fov, Display window) {
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
		float windowY = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))));
		float windowX = windowY / aspectRatio;
		float frustumLength = far - near;

		projectionMatrix.m00 = windowX;
		projectionMatrix.m11 = windowY;
		projectionMatrix.m22 = -((far + near) / frustumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near * far) / frustumLength);
		projectionMatrix.m33 = 0;

		return projectionMatrix;
	}

	public static Matrix4f createOrthographicMatrix(float near, float far, Display window) {
		Matrix4f orthographicMatrix = new Matrix4f();
		float windowY = window.getHeight(), windowX = window.getWidth();
		float frustumLength = far - near;

		orthographicMatrix.m00 = 2 / windowX;
		orthographicMatrix.m11 = 2 / windowY;

		orthographicMatrix.m22 = -2 / frustumLength;
		orthographicMatrix.m33 = 1;

		return orthographicMatrix;
	}

	public static Vector4f[] createFrustumPlanes(Vector4f[] p, Matrix4f projectionViewMatrix) {

		p[FRUSTUM_RIGHT].x = projectionViewMatrix.m03 - projectionViewMatrix.m00;
		p[FRUSTUM_RIGHT].y = projectionViewMatrix.m13 - projectionViewMatrix.m10;
		p[FRUSTUM_RIGHT].z = projectionViewMatrix.m23 - projectionViewMatrix.m20;
		p[FRUSTUM_RIGHT].w = projectionViewMatrix.m33 - projectionViewMatrix.m30;

		p[FRUSTUM_LEFT].x = projectionViewMatrix.m03 + projectionViewMatrix.m00;
		p[FRUSTUM_LEFT].y = projectionViewMatrix.m13 + projectionViewMatrix.m10;
		p[FRUSTUM_LEFT].z = projectionViewMatrix.m23 + projectionViewMatrix.m20;
		p[FRUSTUM_LEFT].w = projectionViewMatrix.m33 + projectionViewMatrix.m30;

		p[FRUSTUM_DOWN].x = projectionViewMatrix.m03 + projectionViewMatrix.m01;
		p[FRUSTUM_DOWN].y = projectionViewMatrix.m13 + projectionViewMatrix.m11;
		p[FRUSTUM_DOWN].z = projectionViewMatrix.m23 + projectionViewMatrix.m21;
		p[FRUSTUM_DOWN].w = projectionViewMatrix.m33 + projectionViewMatrix.m31;

		p[FRUSTUM_UP].x = projectionViewMatrix.m03 - projectionViewMatrix.m01;
		p[FRUSTUM_UP].y = projectionViewMatrix.m13 - projectionViewMatrix.m11;
		p[FRUSTUM_UP].z = projectionViewMatrix.m23 - projectionViewMatrix.m21;
		p[FRUSTUM_UP].w = projectionViewMatrix.m33 - projectionViewMatrix.m31;

		p[FRUSTUM_FAR].x = projectionViewMatrix.m03 - projectionViewMatrix.m02;
		p[FRUSTUM_FAR].y = projectionViewMatrix.m13 - projectionViewMatrix.m12;
		p[FRUSTUM_FAR].z = projectionViewMatrix.m23 - projectionViewMatrix.m22;
		p[FRUSTUM_FAR].w = projectionViewMatrix.m33 - projectionViewMatrix.m32;

		p[FRUSTUM_NEAR].x = projectionViewMatrix.m03 + projectionViewMatrix.m02;
		p[FRUSTUM_NEAR].y = projectionViewMatrix.m13 + projectionViewMatrix.m12;
		p[FRUSTUM_NEAR].z = projectionViewMatrix.m23 + projectionViewMatrix.m22;
		p[FRUSTUM_NEAR].w = projectionViewMatrix.m33 + projectionViewMatrix.m32;

		// Normalize all plane normals
		for (int i = 0; i < 6; i++)
			p[i].normalise();

		return p;
	}

	public static Vector3f pointFromThreePlaneIntersection(Vector4f p1, Vector4f p2, Vector4f p3) {
		Matrix3f m = new Matrix3f();
		m.m00 = p1.x;
		m.m10 = p1.y;
		m.m20 = p1.z;
		m.m01 = p2.x;
		m.m11 = p2.y;
		m.m21 = p2.z;
		m.m02 = p3.x;
		m.m12 = p3.y;
		m.m22 = p3.z;
		Vector3f rightSide = new Vector3f(p1.w, p2.w, p3.w);
		return Matrix3f.transform(m, rightSide, null);
	}

	public static Vector3f mul(Matrix3f m, Vector3f v) {
		Vector3f a = new Vector3f();
		a.x = m.m00 * v.x + m.m10 * v.x + m.m20 * v.x;
		a.y = m.m01 * v.y + m.m11 * v.y + m.m21 * v.y;
		a.z = m.m02 * v.z + m.m12 * v.z + m.m22 * v.z;
		return a;
	}

	public static boolean sphereFrustumIntersection(Entity e, Model m, Vector4f[] frustum) {
		for (int i = 0; i < 6; i++) {
			if (Vector3f.dot(Vector3f.add(e.getPosition(), new Vector3f(m.getCenterPoint().x * e.getScale(), m.getCenterPoint().y * e.getScale(), m.getCenterPoint().z * e.getScale()), null), new Vector3f(frustum[i].x, frustum[i].y, frustum[i].z)) + frustum[i].w + m.getRadius() * e.getScale() <= 0) {
				return false;
			}
		}

		return true;
	}

	public static boolean sphereFrustumIntersection(Light l, Vector4f[] frustum) {
		for (int i = 0; i < 6; i++) {
			if (Vector3f.dot(l.getPosition(), new Vector3f(frustum[i].x, frustum[i].y, frustum[i].z)) + 
					frustum[i].w - l.getRadius() <= 0) {
				return false;
			}
		}

		return true;
	}

	public static float map(float value, float min1, float max1, float min2, float max2) {
		return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
	}

	public static float clamp(double value, double min, double max) {
		if (value < min) {
			value = min;
		} else if (value > max) {
			value = max;
		}
		return (float) value;
	}

}
