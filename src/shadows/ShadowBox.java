package shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entities.Camera;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;

public class ShadowBox {

    private static final float OFFSET = 15;
    private static final Vector4f UP = new Vector4f(0, 1, 0, 0);
    private static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
    public static final float SHADOW_DISTANCE = 100;

    private float minX, maxX;
    private float minY, maxY;
    private float minZ, maxZ;
    private Matrix4f lightViewMatrix;
    private Camera cam;

    private float farHeight, farWidth, nearHeight, nearWidth;

    protected ShadowBox(Matrix4f lightViewMatrix, Camera camera) {
        this.lightViewMatrix = lightViewMatrix;
        this.cam = camera;
        calculateWidthsAndHeights();
    }

    protected void update() {
        Matrix4f rotation = calculateCameraRotationMatrix();
        Vector4f forwardVector4 = rotation.transform(FORWARD, new Vector4f());
        Vector3f forwardVector = new Vector3f(forwardVector4.x, forwardVector4.y, forwardVector4.z);

        Vector3f toFar = new Vector3f(forwardVector).mul(SHADOW_DISTANCE);
        Vector3f toNear = new Vector3f(forwardVector).mul(MasterRenderer.NEAR_PLANE);
        Vector3f centerNear = toNear.add(cam.getPosition());
        Vector3f centerFar = toFar.add(cam.getPosition());

        Vector4f[] points = calculateFrustumVertices(rotation, forwardVector, centerNear, centerFar);

        boolean first = true;
        for (Vector4f point : points) {
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
        maxZ += OFFSET;
    }

    protected Vector3f getCenter() {
        float x = (minX + maxX) / 2f;
        float y = (minY + maxY) / 2f;
        float z = (minZ + maxZ) / 2f;
        Vector4f cen = new Vector4f(x, y, z, 1);
        Matrix4f invertedLight = lightViewMatrix.invert(new Matrix4f());
        Vector4f result = invertedLight.transform(cen, new Vector4f());
        return new Vector3f(result.x, result.y, result.z);
    }

    protected float getWidth() {
        return maxX - minX;
    }

    protected float getHeight() {
        return maxY - minY;
    }

    protected float getLength() {
        return maxZ - minZ;
    }

    private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector, Vector3f centerNear, Vector3f centerFar) {
        Vector4f upVector4 = rotation.transform(UP, new Vector4f());
        Vector3f upVector = new Vector3f(upVector4.x, upVector4.y, upVector4.z);
        Vector3f rightVector = forwardVector.cross(upVector, new Vector3f());
        Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
        Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);
        Vector3f farTop = centerFar.add(upVector.mul(farHeight, new Vector3f()));
        Vector3f farBottom = centerFar.add(downVector.mul(farHeight, new Vector3f()));
        Vector3f nearTop = centerNear.add(upVector.mul(nearHeight, new Vector3f()));
        Vector3f nearBottom = centerNear.add(downVector.mul(nearHeight, new Vector3f()));
        Vector4f[] points = new Vector4f[8];
        points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
        points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
        points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
        points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
        points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
        points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);
        return points;
    }

    private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction, float width) {
        Vector3f point = startPoint.add(direction.mul(width, new Vector3f()));
        Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
        return lightViewMatrix.transform(point4f);
    }

    private Matrix4f calculateCameraRotationMatrix() {
        Matrix4f rotation = new Matrix4f();
        rotation.rotateY((float) Math.toRadians(-cam.getYaw()));
        rotation.rotateX((float) Math.toRadians(-cam.getPitch()));
        return rotation;
    }

    private void calculateWidthsAndHeights() {
        farWidth = (float) (SHADOW_DISTANCE * Math.tan(Math.toRadians(MasterRenderer.FOV)));
        nearWidth = (float) (MasterRenderer.NEAR_PLANE * Math.tan(Math.toRadians(MasterRenderer.FOV)));
        farHeight = farWidth / getAspectRatio();
        nearHeight = nearWidth / getAspectRatio();
    }

    private float getAspectRatio() {
        return (float) DisplayManager.getWindowWidth() / (float) DisplayManager.getWindowHeight();
    }

}