package com.raven.engine.util.math;

/*
 * The MIT License (MIT)
 *
 * Copyright © 2015-2017, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * This class represents a 4x4-Matrix. GLSL equivalent to mat4.
 *
 * @author Heiko Brumme
 */
public class Matrix4f {


    private static Vector3f tempVec = new Vector3f();
    private static Vector3f tempVec2 = new Vector3f();
    private static Vector3f tempVec3 = new Vector3f();
    private static Matrix4f tempMat = new Matrix4f();
    private static Matrix4f tempMat2 = new Matrix4f();

    FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    protected float m00, m01, m02, m03;
    protected float m10, m11, m12, m13;
    protected float m20, m21, m22, m23;
    protected float m30, m31, m32, m33;

    /**
     * Creates a 4x4 identity matrix.
     */
    public Matrix4f() {
        identity();
    }

    /**
     * Creates a 4x4 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     * @param col3 Vector with values of the third column
     * @param col4 Vector with values of the fourth column
     */
    public Matrix4f(Vector4f col1, Vector4f col2, Vector4f col3, Vector4f col4) {
        m00 = col1.x;
        m10 = col1.y;
        m20 = col1.z;
        m30 = col1.w;

        m01 = col2.x;
        m11 = col2.y;
        m21 = col2.z;
        m31 = col2.w;

        m02 = col3.x;
        m12 = col3.y;
        m22 = col3.z;
        m32 = col3.w;

        m03 = col4.x;
        m13 = col4.y;
        m23 = col4.z;
        m33 = col4.w;
    }

    /**
     * Creates a orthographic projection matrix. Similar to
     * <code>glOrtho(left, right, bottom, top, near, far)</code>.
     *
     * @param left   Coordinate for the left vertical clipping pane
     * @param right  Coordinate for the right vertical clipping pane
     * @param bottom Coordinate for the bottom horizontal clipping pane
     * @param top    Coordinate for the bottom horizontal clipping pane
     * @param near   Coordinate for the near depth clipping pane
     * @param far    Coordinate for the far depth clipping pane
     * @return Orthographic matrix
     */
    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far, Matrix4f out) {
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        out.m00 = 2f / (right - left);
        out.m11 = 2f / (top - bottom);
        out.m22 = -2f / (far - near);
        out.m03 = tx;
        out.m13 = ty;
        out.m23 = tz;

        return out;
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>glFrustum(left, right, bottom, top, near, far)</code>.
     *
     * @param left   Coordinate for the left vertical clipping pane
     * @param right  Coordinate for the right vertical clipping pane
     * @param bottom Coordinate for the bottom horizontal clipping pane
     * @param top    Coordinate for the bottom horizontal clipping pane
     * @param near   Coordinate for the near depth clipping pane, must be
     *               positive
     * @param far    Coordinate for the far depth clipping pane, must be
     *               positive
     * @return Perspective matrix
     */
    public static Matrix4f frustum(float left, float right, float bottom, float top, float near, float far, Matrix4f out) {
        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float c = -(far + near) / (far - near);
        float d = -(2f * far * near) / (far - near);

        out.m00 = (2f * near) / (right - left);
        out.m11 = (2f * near) / (top - bottom);
        out.m02 = a;
        out.m12 = b;
        out.m22 = c;
        out.m32 = -1f;
        out.m23 = d;
        out.m33 = 0f;

        return out;
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>gluPerspective(fovy, aspec, zNear, zFar)</code>.
     *
     * @param fovy   Field of view angle in degrees
     * @param aspect The aspect ratio is the ratio of width to height
     * @param near   Distance from the viewer to the near clipping plane, must
     *               be positive
     * @param far    Distance from the viewer to the far clipping plane, must be
     *               positive
     * @return Perspective matrix
     */
    public static Matrix4f perspective(float fovy, float aspect, float near, float far, Matrix4f out) {
        float f = (float) (1f / Math.tan(Math.toRadians(fovy) / 2f));

        out.m00 = f / aspect;
        out.m11 = f;
        out.m22 = (far + near) / (near - far);
        out.m32 = -1f;
        out.m23 = (2f * far * near) / (near - far);
        out.m33 = 0f;

        return out;
    }

    // TODO
    public static Matrix4f direction(Vector3f direction, Matrix4f out) {

        Vector3f zaxis = direction.normalize(tempVec2);
        tempVec.x = 0;
        tempVec.y = 1;
        tempVec.z = 0;
        Vector3f xaxis = tempVec.cross(direction, tempVec3).normalize(tempVec);
        Vector3f yaxis = zaxis.cross(xaxis, tempVec3);     // The "up" vector.

        // Create a 4x4 orientation matrix from the right, up, and forward vectors
        // This is transposed which is equivalent to performing an inverse
        // if the matrix is orthonormalized (in this case, it is).
        tempMat2.m00 = xaxis.x; tempMat2.m01 = yaxis.x; tempMat2.m02 = zaxis.x; tempMat2.m03 = 0f;
        tempMat2.m10 = xaxis.y; tempMat2.m11 = yaxis.y; tempMat2.m12 = zaxis.y; tempMat2.m13 = 0f;
        tempMat2.m20 = xaxis.z; tempMat2.m21 = yaxis.z; tempMat2.m22 = zaxis.z; tempMat2.m23 = 0f;
        tempMat2.m30 = 0f; tempMat2.m31 = 0f; tempMat2.m32 = 0f; tempMat2.m33 = 0f;


        // the final view matrix
        tempMat.identity();
        tempMat.translate(direction.normalize(tempVec).scale(-30f, tempVec2), out);
        out.invert(tempMat);

        tempMat2.multiply(tempMat, out);

        return out;
    }

    /**
     * Creates a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     *
     * @param x x coordinate of translation vector
     * @param y y coordinate of translation vector
     * @param z z coordinate of translation vector
     * @return Translation matrix
     */
    public Matrix4f translate(float x, float y, float z, Matrix4f out) {
        tempMat.identity();

        tempMat.m03 = x;
        tempMat.m13 = y;
        tempMat.m23 = z;

        this.multiply(tempMat, out);

        return out;
    }

    public Matrix4f translate(Vector3f t, Matrix4f out) {
        return translate(t.x, t.y, t.z, out);
    }

    /**
     * Creates a rotation matrix. Similar to
     * <code>glRotate(angle, x, y, z)</code>.
     *
     * @param angle Angle of rotation in degrees
     * @param x     x coordinate of the rotation vector
     * @param y     y coordinate of the rotation vector
     * @param z     z coordinate of the rotation vector
     * @return Rotation matrix
     */
    public Matrix4f rotate(float angle, float x, float y, float z, Matrix4f out) {
        tempMat.identity();

        float c = (float) Math.cos(Math.toRadians(angle));
        float s = (float) Math.sin(Math.toRadians(angle));

        tempVec.x = x;
        tempVec.y = y;
        tempVec.z = z;

        float len = tempVec.length();

        if (len != 1f) {

            x = tempVec.x / len;
            y = tempVec.y / len;
            z = tempVec.z / len;
        }

        tempMat.m00 = x * x * (1f - c) + c;
        tempMat.m10 = y * x * (1f - c) + z * s;
        tempMat.m20 = x * z * (1f - c) - y * s;
        tempMat.m01 = x * y * (1f - c) - z * s;
        tempMat.m11 = y * y * (1f - c) + c;
        tempMat.m21 = y * z * (1f - c) + x * s;
        tempMat.m02 = x * z * (1f - c) + y * s;
        tempMat.m12 = y * z * (1f - c) - x * s;
        tempMat.m22 = z * z * (1f - c) + c;

        this.multiply(tempMat, out);
        
        return out;
    }

    /**
     * Creates a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param x Scale factor along the x coordinate
     * @param y Scale factor along the y coordinate
     * @param z Scale factor along the z coordinate
     * @return Scaling matrix
     */
    public Matrix4f scale(float x, float y, float z, Matrix4f out) {

        tempMat.identity();

        tempMat.m00 = x;
        tempMat.m11 = y;
        tempMat.m22 = z;

        this.multiply(tempMat, out);

        return out;
    }

    public static Matrix4f reflection(Plane p, Matrix4f out) {
        out.m00 = 1 - 2 * p.a * p.a;
        out.m01 = -2 * p.a * p.b;
        out.m02 = -2 * p.a * p.c;
        out.m03 = -2 * p.a * p.d;
        out.m10 = -2 * p.a * p.b;
        out.m11 = 1 - 2 * p.b * p.b;
        out.m12 = -2 * p.b * p.c;
        out.m13 = -2 * p.b * p.d;
        out.m20 = -2 * p.a * p.c;
        out.m21 = -2 * p.b * p.c;
        out.m22 = 1 - 2 * p.c * p.c;
        out.m23 = -2 * p.c * p.d;
        out.m30 = 0.0f;
        out.m31 = 0.0f;
        out.m32 = 0.0f;
        out.m33 = 1.0f;

        return out;
    }

    /**
     * Sets this matrix to the identity matrix.
     */
    public final Matrix4f identity() {
        m00 = 1f;
        m11 = 1f;
        m22 = 1f;
        m33 = 1f;

        m01 = 0f;
        m02 = 0f;
        m03 = 0f;
        m10 = 0f;
        m12 = 0f;
        m13 = 0f;
        m20 = 0f;
        m21 = 0f;
        m23 = 0f;
        m30 = 0f;
        m31 = 0f;
        m32 = 0f;

        return this;
    }

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    public Matrix4f add(Matrix4f other, Matrix4f out) {
        out.m00 = this.m00 + other.m00;
        out.m10 = this.m10 + other.m10;
        out.m20 = this.m20 + other.m20;
        out.m30 = this.m30 + other.m30;

        out.m01 = this.m01 + other.m01;
        out.m11 = this.m11 + other.m11;
        out.m21 = this.m21 + other.m21;
        out.m31 = this.m31 + other.m31;

        out.m02 = this.m02 + other.m02;
        out.m12 = this.m12 + other.m12;
        out.m22 = this.m22 + other.m22;
        out.m32 = this.m32 + other.m32;

        out.m03 = this.m03 + other.m03;
        out.m13 = this.m13 + other.m13;
        out.m23 = this.m23 + other.m23;
        out.m33 = this.m33 + other.m33;

        return out;
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    public Matrix4f negate(Matrix4f out) {
        return multiply(-1f, out);
    }

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    public Matrix4f subtract(Matrix4f other, Matrix4f out) {
        return this.add(other.negate(tempMat), out);
    }

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    public Matrix4f multiply(float scalar, Matrix4f out) {
        out.m00 = this.m00 * scalar;
        out.m10 = this.m10 * scalar;
        out.m20 = this.m20 * scalar;
        out.m30 = this.m30 * scalar;

        out.m01 = this.m01 * scalar;
        out.m11 = this.m11 * scalar;
        out.m21 = this.m21 * scalar;
        out.m31 = this.m31 * scalar;

        out.m02 = this.m02 * scalar;
        out.m12 = this.m12 * scalar;
        out.m22 = this.m22 * scalar;
        out.m32 = this.m32 * scalar;

        out.m03 = this.m03 * scalar;
        out.m13 = this.m13 * scalar;
        out.m23 = this.m23 * scalar;
        out.m33 = this.m33 * scalar;

        return out;
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    public Vector4f multiply(Vector4f vector, Vector4f out) {
        out.x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z + this.m03 * vector.w;
        out.y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z + this.m13 * vector.w;
        out.z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z + this.m23 * vector.w;
        out.w = this.m30 * vector.x + this.m31 * vector.y + this.m32 * vector.z + this.m33 * vector.w;
        return out;
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    public Matrix4f multiply(Matrix4f other, Matrix4f out) {
        out.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
        out.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
        out.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
        out.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;

        out.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
        out.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
        out.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
        out.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;

        out.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
        out.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
        out.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
        out.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;

        out.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
        out.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
        out.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
        out.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;

        return out;
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    public Matrix4f transpose(Matrix4f out) {
        out.m00 = this.m00;
        out.m10 = this.m01;
        out.m20 = this.m02;
        out.m30 = this.m03;

        out.m01 = this.m10;
        out.m11 = this.m11;
        out.m21 = this.m12;
        out.m31 = this.m13;

        out.m02 = this.m20;
        out.m12 = this.m21;
        out.m22 = this.m22;
        out.m32 = this.m23;

        out.m03 = this.m30;
        out.m13 = this.m31;
        out.m23 = this.m32;
        out.m33 = this.m33;

        return out;
    }

    public Matrix4f inverse(Matrix4f out) {
        tempMat.m00 = m11 * m22 * m33 -
                m11 * m23 * m32 -
                m21 * m12 * m33 +
                m21 * m13 * m32 +
                m31 * m12 * m23 -
                m31 * m13 * m22;

        tempMat.m10 = -m10 * m22 * m33 +
                m10 * m23 * m32 +
                m20 * m12 * m33 -
                m20 * m13 * m32 -
                m30 * m12 * m23 +
                m30 * m13 * m22;

        tempMat.m20 = m10 * m21 * m33 -
                m10 * m23 * m31 -
                m20 * m11 * m33 +
                m20 * m13 * m31 +
                m30 * m11 * m23 -
                m30 * m13 * m21;

        tempMat.m30 = -m10 * m21 * m32 +
                m10 * m22 * m31 +
                m20 * m11 * m32 -
                m20 * m12 * m31 -
                m30 * m11 * m22 +
                m30 * m12 * m21;

        tempMat.m01 = -m01 * m22 * m33 +
                m01 * m23 * m32 +
                m21 * m02 * m33 -
                m21 * m03 * m32 -
                m31 * m02 * m23 +
                m31 * m03 * m22;

        tempMat.m11 = m00 * m22 * m33 -
                m00 * m23 * m32 -
                m20 * m02 * m33 +
                m20 * m03 * m32 +
                m30 * m02 * m23 -
                m30 * m03 * m22;

        tempMat.m21 = -m00 * m21 * m33 +
                m00 * m23 * m31 +
                m20 * m01 * m33 -
                m20 * m03 * m31 -
                m30 * m01 * m23 +
                m30 * m03 * m21;

        tempMat.m31 = m00 * m21 * m32 -
                m00 * m22 * m31 -
                m20 * m01 * m32 +
                m20 * m02 * m31 +
                m30 * m01 * m22 -
                m30 * m02 * m21;

        tempMat.m02 = m01 * m12 * m33 -
                m01 * m13 * m32 -
                m11 * m02 * m33 +
                m11 * m03 * m32 +
                m31 * m02 * m13 -
                m31 * m03 * m12;

        tempMat.m12 = -m00 * m12 * m33 +
                m00 * m13 * m32 +
                m10 * m02 * m33 -
                m10 * m03 * m32 -
                m30 * m02 * m13 +
                m30 * m03 * m12;

        tempMat.m22 = m00 * m11 * m33 -
                m00 * m13 * m31 -
                m10 * m01 * m33 +
                m10 * m03 * m31 +
                m30 * m01 * m13 -
                m30 * m03 * m11;

        tempMat.m32 = -m00 * m11 * m32 +
                m00 * m12 * m31 +
                m10 * m01 * m32 -
                m10 * m02 * m31 -
                m30 * m01 * m12 +
                m30 * m02 * m11;

        tempMat.m03 = -m01 * m12 * m23 +
                m01 * m13 * m22 +
                m11 * m02 * m23 -
                m11 * m03 * m22 -
                m21 * m02 * m13 +
                m21 * m03 * m12;

        tempMat.m13 = m00 * m12 * m23 -
                m00 * m13 * m22 -
                m10 * m02 * m23 +
                m10 * m03 * m22 +
                m20 * m02 * m13 -
                m20 * m03 * m12;

        tempMat.m23 = -m00 * m11 * m23 +
                m00 * m13 * m21 +
                m10 * m01 * m23 -
                m10 * m03 * m21 -
                m20 * m01 * m13 +
                m20 * m03 * m11;

        tempMat.m33 = m00 * m11 * m22 -
                m00 * m12 * m21 -
                m10 * m01 * m22 +
                m10 * m02 * m21 +
                m20 * m01 * m12 -
                m20 * m02 * m11;

        float det = m00 * tempMat.m00 + m01 * tempMat.m10 + m02 * tempMat.m20 + m03 * tempMat.m30;

        det = 1.0f / det;

        tempMat.multiply(det, out);

        return out;
    }

    public Matrix4f invert(Matrix4f out) {
        tempMat.identity();

        tempMat.m00 = m11 * m22 * m33 -
                m11 * m23 * m32 -
                m21 * m12 * m33 +
                m21 * m13 * m32 +
                m31 * m12 * m23 -
                m31 * m13 * m22;

        tempMat.m10 = -m10 * m22 * m33 +
                m10 * m23 * m32 +
                m20 * m12 * m33 -
                m20 * m13 * m32 -
                m30 * m12 * m23 +
                m30 * m13 * m22;

        tempMat.m20 = m10 * m21 * m33 -
                m10 * m23 * m31 -
                m20 * m11 * m33 +
                m20 * m13 * m31 +
                m30 * m11 * m23 -
                m30 * m13 * m21;

        tempMat.m30 = -m10 * m21 * m32 +
                m10 * m22 * m31 +
                m20 * m11 * m32 -
                m20 * m12 * m31 -
                m30 * m11 * m22 +
                m30 * m12 * m21;

        tempMat.m01 = -m01 * m22 * m33 +
                m01 * m23 * m32 +
                m21 * m02 * m33 -
                m21 * m03 * m32 -
                m31 * m02 * m23 +
                m31 * m03 * m22;

        tempMat.m11 = m00 * m22 * m33 -
                m00 * m23 * m32 -
                m20 * m02 * m33 +
                m20 * m03 * m32 +
                m30 * m02 * m23 -
                m30 * m03 * m22;

        tempMat.m21 = -m00 * m21 * m33 +
                m00 * m23 * m31 +
                m20 * m01 * m33 -
                m20 * m03 * m31 -
                m30 * m01 * m23 +
                m30 * m03 * m21;

        tempMat.m31 = m00 * m21 * m32 -
                m00 * m22 * m31 -
                m20 * m01 * m32 +
                m20 * m02 * m31 +
                m30 * m01 * m22 -
                m30 * m02 * m21;

        tempMat.m02 = m01 * m12 * m33 -
                m01 * m13 * m32 -
                m11 * m02 * m33 +
                m11 * m03 * m32 +
                m31 * m02 * m13 -
                m31 * m03 * m12;

        tempMat.m12 = -m00 * m12 * m33 +
                m00 * m13 * m32 +
                m10 * m02 * m33 -
                m10 * m03 * m32 -
                m30 * m02 * m13 +
                m30 * m03 * m12;

        tempMat.m22 = m00 * m11 * m33 -
                m00 * m13 * m31 -
                m10 * m01 * m33 +
                m10 * m03 * m31 +
                m30 * m01 * m13 -
                m30 * m03 * m11;

        tempMat.m32 = -m00 * m11 * m32 +
                m00 * m12 * m31 +
                m10 * m01 * m32 -
                m10 * m02 * m31 -
                m30 * m01 * m12 +
                m30 * m02 * m11;

        tempMat.m03 = -m01 * m12 * m23 +
                m01 * m13 * m22 +
                m11 * m02 * m23 -
                m11 * m03 * m22 -
                m21 * m02 * m13 +
                m21 * m03 * m12;

        tempMat.m13 = m00 * m12 * m23 -
                m00 * m13 * m22 -
                m10 * m02 * m23 +
                m10 * m03 * m22 +
                m20 * m02 * m13 -
                m20 * m03 * m12;

        tempMat.m23 = -m00 * m11 * m23 +
                m00 * m13 * m21 +
                m10 * m01 * m23 -
                m10 * m03 * m21 -
                m20 * m01 * m13 +
                m20 * m03 * m11;

        tempMat.m33 = m00 * m11 * m22 -
                m00 * m12 * m21 -
                m10 * m01 * m22 +
                m10 * m02 * m21 +
                m20 * m01 * m12 -
                m20 * m02 * m11;

        float det = m00 * tempMat.m00 + m01 * tempMat.m10 + m02 * tempMat.m20 + m03 * tempMat.m30;

        det = 1.0f / det;

        tempMat.multiply(det, out);

        return out;
    }

    public void toBuffer(FloatBuffer buffer) {
        buffer.put(m00).put(m10).put(m20).put(m30);
        buffer.put(m01).put(m11).put(m21).put(m31);
        buffer.put(m02).put(m12).put(m22).put(m32);
        buffer.put(m03).put(m13).put(m23).put(m33);
    }

    public FloatBuffer toBuffer() {
        toBuffer(buffer);
        buffer.flip();

        return buffer;
    }

    public String toString() {
        return String.join(", ",
                "\n" + Float.toString(m00), Float.toString(m01), Float.toString(m02), Float.toString(m03),
                "\n" + Float.toString(m10), Float.toString(m11), Float.toString(m12), Float.toString(m13),
                "\n" + Float.toString(m20), Float.toString(m21), Float.toString(m22), Float.toString(m23),
                "\n" + Float.toString(m30), Float.toString(m31), Float.toString(m32), Float.toString(m33));
    }



    public static float shadowSkewLength(Vector3f direction, float size, float height) {
        tempVec.x = direction.x;
        tempVec.y = 0f;
        tempVec.z = direction.z;

        float len = tempVec.length();

        return Math.abs(height * direction.y) + Math.abs(size * len);
    }

    public static Matrix4f shadowSkew(Vector3f direction, Vector3f origin, float size, float height, Matrix4f out) {

        tempVec.x = direction.x;
        tempVec.y = 0f;
        tempVec.z = direction.z;

        float len = tempVec.length();

        tempVec.normalize(tempVec2);

        out.m00 = 0;
        out.m01 = 0;
        out.m02 = -1 / size;
        out.m03 = 0;

        out.m10 = -direction.y / ((Math.abs(len) * height + Math.abs(direction.y) * size));
        out.m11 = len / ((Math.abs(len) * height + Math.abs(direction.y) * size));
        out.m12 = 0;
        out.m13 = 0;

        out.m20 = -len / (size + height);
        out.m21 = -direction.y / (size + height);
        out.m22 = 0;
        out.m23 = 0;

        out.m30 = 0;
        out.m31 = 0;
        out.m32 = 0;
        out.m33 = 1;

        float rotation;
        if (tempVec2.z < 0)
            rotation = (float)Math.toDegrees(-Math.acos(tempVec2.x));
        else
            rotation = (float)Math.toDegrees(Math.acos(tempVec2.x));

        out.rotate(rotation, 0, 1, 0, tempMat2);
        tempMat2.translate(origin.negate(tempVec2), out);

        return out;
    }
}