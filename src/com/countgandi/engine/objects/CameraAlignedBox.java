package com.countgandi.engine.objects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.countgandi.engine.utils.Maths;

public class CameraAlignedBox {
	
	private Matrix4f viewMatrix;
	
	private float minX, maxX;
	private float minY, maxY;
	private float minZ, maxZ;
	
	public void update(Vector4f[] frustum) {
		Vector3f[] points = this.getVerticesFromFrustum(frustum);
		
		
		boolean first = true;
		for (Vector3f point : points) {
			if (first) {
				minX = point.x;
				maxX = point.x;
				minY = point.y;
				maxY = point.y;
				minZ = point.z;
				maxZ = point.z;
				first = false;
				continue;
			}
			if (point.x > maxX) {
				maxX = point.x;
			} else if (point.x < minX) {
				minX = point.x;
			}
			if (point.y > maxY) {
				maxY = point.y;
			} else if (point.y < minY) {
				minY = point.y;
			}
			if (point.z > maxZ) {
				maxZ = point.z;
			} else if (point.z < minZ) {
				minZ = point.z;
			}
		}
	}
	
	private Vector3f[] getVerticesFromFrustum(Vector4f[] frustum) {
		Vector3f[] points = new Vector3f[8];

		// Near Bottom Left
		points[0] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_NEAR], frustum[Maths.FRUSTUM_LEFT], frustum[Maths.FRUSTUM_DOWN]);
		// Near Bottom Right
		points[1] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_NEAR], frustum[Maths.FRUSTUM_RIGHT], frustum[Maths.FRUSTUM_DOWN]);

		// Near Top Right
		points[2] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_NEAR], frustum[Maths.FRUSTUM_RIGHT], frustum[Maths.FRUSTUM_UP]);
		// Near Top Left
		points[3] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_NEAR], frustum[Maths.FRUSTUM_LEFT], frustum[Maths.FRUSTUM_UP]);

		// Far Away

		// Far Bottom Left
		points[4] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_FAR], frustum[Maths.FRUSTUM_LEFT], frustum[Maths.FRUSTUM_DOWN]);
		// Far Bottom Right
		points[5] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_FAR], frustum[Maths.FRUSTUM_RIGHT], frustum[Maths.FRUSTUM_DOWN]);

		// Far Top Right
		points[6] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_FAR], frustum[Maths.FRUSTUM_RIGHT], frustum[Maths.FRUSTUM_UP]);
		// Far Top Left
		points[7] = Maths.pointFromThreePlaneIntersection(frustum[Maths.FRUSTUM_FAR], frustum[Maths.FRUSTUM_LEFT], frustum[Maths.FRUSTUM_UP]);

		return points;
	}

}
