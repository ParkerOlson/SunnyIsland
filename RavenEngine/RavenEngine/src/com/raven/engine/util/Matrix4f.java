package com.raven.engine.util;

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

    FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    private float m00, m01, m02, m03;
    private float m10, m11, m12, m13;
    private float m20, m21, m22, m23;
    private float m30, m31, m32, m33;
    
    private float n00, n01, n02, n03;
    private float n10, n11, n12, n13;
    private float n20, n21, n22, n23;
    private float n30, n31, n32, n33;


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
    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f ortho = new Matrix4f();

        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        ortho.m00 = 2f / (right - left);
        ortho.m11 = 2f / (top - bottom);
        ortho.m22 = -2f / (far - near);
        ortho.m03 = tx;
        ortho.m13 = ty;
        ortho.m23 = tz;

        return ortho;
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
    public static Matrix4f frustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f frustum = new Matrix4f();

        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float c = -(far + near) / (far - near);
        float d = -(2f * far * near) / (far - near);

        frustum.m00 = (2f * near) / (right - left);
        frustum.m11 = (2f * near) / (top - bottom);
        frustum.m02 = a;
        frustum.m12 = b;
        frustum.m22 = c;
        frustum.m32 = -1f;
        frustum.m23 = d;
        frustum.m33 = 0f;

        return frustum;
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
    public static Matrix4f perspective(float fovy, float aspect, float near, float far) {
        Matrix4f perspective = new Matrix4f();

        float f = (float) (1f / Math.tan(Math.toRadians(fovy) / 2f));

        perspective.m00 = f / aspect;
        perspective.m11 = f;
        perspective.m22 = (far + near) / (near - far);
        perspective.m32 = -1f;
        perspective.m23 = (2f * far * near) / (near - far);
        perspective.m33 = 0f;

        return perspective;
    }

    public static Matrix4f lookAt (
            float eyex, float eyey, float eyez,
            float atx, float aty, float atz,
            float upx, float upy, float upz)
    {
        Matrix4f mat = new Matrix4f();

        Vector3f at = new Vector3f(atx-eyex, aty-eyey, atz-eyez).normalize();

        // ?
        if (at.z > 0) {
            upy *= -1;
        }

        Vector3f up = new Vector3f(upx, upy, upz);
        Vector3f xaxis = at.cross(up).normalize();

        up = xaxis.cross(at);
        at.scale(1f);

        mat.m00 = xaxis.x; mat.m01 = xaxis.y; mat.m02 = xaxis.z; mat.m03 = 0f;
        mat.m10 = up.x; mat.m11 = up.y; mat.m12 = up.z; mat.m13 = 0f;
        mat.m20 = at.x; mat.m21 = at.y; mat.m22 = at.z; mat.m23 = 0f;
        mat.m30 = eyex; mat.m31 = eyey; mat.m32 = eyez; mat.m33 = 1f;

        return mat.transpose();
    }

    public static Matrix4f direction(Vector3f direction, Matrix4f mat) {

        if (mat == null) {
            mat = new Matrix4f();
        } else {
            mat.identity();
        }

        Vector3f zaxis = direction.normalize();
        Vector3f xaxis = new Vector3f(0, 1, 0).cross(direction).normalize();
        Vector3f yaxis = zaxis.cross(xaxis);     // The "up" vector.

        // Create a 4x4 orientation matrix from the right, up, and forward vectors
        // This is transposed which is equivalent to performing an inverse
        // if the matrix is orthonormalized (in this case, it is).
        mat.m00 = xaxis.x; mat.m01 = yaxis.x; mat.m02 = zaxis.x; mat.m03 = 0f;
        mat.m10 = xaxis.y; mat.m11 = yaxis.y; mat.m12 = zaxis.y; mat.m13 = 0f;
        mat.m20 = xaxis.z; mat.m21 = yaxis.z; mat.m22 = zaxis.z; mat.m23 = 0f;
        mat.m30 = 0f; mat.m31 = 0f; mat.m32 = 0f; mat.m33 = 0f;


        // the final view matrix
        Matrix4f cat = new Matrix4f();
        cat.translate(direction.normalize().scale(-30f));
        cat.invert();

        mat = mat.multiply(cat);

        return mat;
    }

    public Matrix4f direction(Vector3f direction) {
        return direction(direction, this);
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
    public Matrix4f translate(float x, float y, float z) {
        nIdentity();

        n03 = x;
        n13 = y;
        n23 = z;

        nMultiply();

        return this;
    }

    public Matrix4f translate(Vector3f t) {
        return translate(t.x, t.y, t.z);
    }

    private Vector3f vec = new Vector3f();
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
    public Matrix4f rotate(float angle, float x, float y, float z) {
        nIdentity();

        float c = (float) Math.cos(Math.toRadians(angle));
        float s = (float) Math.sin(Math.toRadians(angle));

        vec.x = x;
        vec.y = y;
        vec.z = z;

        float len = vec.length();

        if (len != 1f) {

            x = vec.x / len;
            y = vec.y / len;
            z = vec.z / len;
        }

        n00 = x * x * (1f - c) + c;
        n10 = y * x * (1f - c) + z * s;
        n20 = x * z * (1f - c) - y * s;
        n01 = x * y * (1f - c) - z * s;
        n11 = y * y * (1f - c) + c;
        n21 = y * z * (1f - c) + x * s;
        n02 = x * z * (1f - c) + y * s;
        n12 = y * z * (1f - c) - x * s;
        n22 = z * z * (1f - c) + c;

        nMultiply();
        
        return this;
    }

    /**
     * Creates a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     *
     * @param x Scale factor along the x coordinate
     * @param y Scale factor along the y coordinate
     * @param z Scale factor along the z coordinate
     * @return Scaling matrix
     */
    public Matrix4f scale(float x, float y, float z) {
        nIdentity();

        n00 = x;
        n11 = y;
        n22 = z;

        nMultiply();

        return this;
    }

    public static Matrix4f reflection(Plane p) {
        Matrix4f r = new Matrix4f();

        r.m00 = 1 - 2 * p.a * p.a;
        r.m01 = -2 * p.a * p.b;
        r.m02 = -2 * p.a * p.c;
        r.m03 = -2 * p.a * p.d;
        r.m10 = -2 * p.a * p.b;
        r.m11 = 1 - 2 * p.b * p.b;
        r.m12 = -2 * p.b * p.c;
        r.m13 = -2 * p.b * p.d;
        r.m20 = -2 * p.a * p.c;
        r.m21 = -2 * p.b * p.c;
        r.m22 = 1 - 2 * p.c * p.c;
        r.m23 = -2 * p.c * p.d;
        r.m30 = 0.0f;
        r.m31 = 0.0f;
        r.m32 = 0.0f;
        r.m33 = 1.0f;

        return r;
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

    private final void nIdentity() {
        n00 = 1f;
        n11 = 1f;
        n22 = 1f;
        n33 = 1f;

        n01 = 0f;
        n02 = 0f;
        n03 = 0f;
        n10 = 0f;
        n12 = 0f;
        n13 = 0f;
        n20 = 0f;
        n21 = 0f;
        n23 = 0f;
        n30 = 0f;
        n31 = 0f;
        n32 = 0f;
    }

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    public Matrix4f add(Matrix4f other) {
        Matrix4f result = new Matrix4f();

        result.m00 = this.m00 + other.m00;
        result.m10 = this.m10 + other.m10;
        result.m20 = this.m20 + other.m20;
        result.m30 = this.m30 + other.m30;

        result.m01 = this.m01 + other.m01;
        result.m11 = this.m11 + other.m11;
        result.m21 = this.m21 + other.m21;
        result.m31 = this.m31 + other.m31;

        result.m02 = this.m02 + other.m02;
        result.m12 = this.m12 + other.m12;
        result.m22 = this.m22 + other.m22;
        result.m32 = this.m32 + other.m32;

        result.m03 = this.m03 + other.m03;
        result.m13 = this.m13 + other.m13;
        result.m23 = this.m23 + other.m23;
        result.m33 = this.m33 + other.m33;

        return result;
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    public Matrix4f negate() {
        return multiply(-1f);
    }

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    public Matrix4f subtract(Matrix4f other) {
        return this.add(other.negate());
    }

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    public Matrix4f multiply(float scalar) {
        Matrix4f result = new Matrix4f();

        result.m00 = this.m00 * scalar;
        result.m10 = this.m10 * scalar;
        result.m20 = this.m20 * scalar;
        result.m30 = this.m30 * scalar;

        result.m01 = this.m01 * scalar;
        result.m11 = this.m11 * scalar;
        result.m21 = this.m21 * scalar;
        result.m31 = this.m31 * scalar;

        result.m02 = this.m02 * scalar;
        result.m12 = this.m12 * scalar;
        result.m22 = this.m22 * scalar;
        result.m32 = this.m32 * scalar;

        result.m03 = this.m03 * scalar;
        result.m13 = this.m13 * scalar;
        result.m23 = this.m23 * scalar;
        result.m33 = this.m33 * scalar;

        return result;
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    public Vector4f multiply(Vector4f vector) {
        float x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z + this.m03 * vector.w;
        float y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z + this.m13 * vector.w;
        float z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z + this.m23 * vector.w;
        float w = this.m30 * vector.x + this.m31 * vector.y + this.m32 * vector.z + this.m33 * vector.w;
        return new Vector4f(x, y, z, w);
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    public Matrix4f multiply(Matrix4f other) {
        Matrix4f result = new Matrix4f();

        result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
        result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
        result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
        result.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;

        result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
        result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
        result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
        result.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;

        result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
        result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
        result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
        result.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;

        result.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
        result.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
        result.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
        result.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;

        return result;
    }

    private void nMultiply() {

        float t00, t01, t02, t03;
        float t10, t11, t12, t13;
        float t20, t21, t22, t23;
        float t30, t31, t32, t33;
        
        t00 = m00 * n00 + m01 * n10 + m02 * n20 + m03 * n30;
        t10 = m10 * n00 + m11 * n10 + m12 * n20 + m13 * n30;
        t20 = m20 * n00 + m21 * n10 + m22 * n20 + m23 * n30;
        t30 = m30 * n00 + m31 * n10 + m32 * n20 + m33 * n30;

        t01 = m00 * n01 + m01 * n11 + m02 * n21 + m03 * n31;
        t11 = m10 * n01 + m11 * n11 + m12 * n21 + m13 * n31;
        t21 = m20 * n01 + m21 * n11 + m22 * n21 + m23 * n31;
        t31 = m30 * n01 + m31 * n11 + m32 * n21 + m33 * n31;

        t02 = m00 * n02 + m01 * n12 + m02 * n22 + m03 * n32;
        t12 = m10 * n02 + m11 * n12 + m12 * n22 + m13 * n32;
        t22 = m20 * n02 + m21 * n12 + m22 * n22 + m23 * n32;
        t32 = m30 * n02 + m31 * n12 + m32 * n22 + m33 * n32;

        t03 = m00 * n03 + m01 * n13 + m02 * n23 + m03 * n33;
        t13 = m10 * n03 + m11 * n13 + m12 * n23 + m13 * n33;
        t23 = m20 * n03 + m21 * n13 + m22 * n23 + m23 * n33;
        t33 = m30 * n03 + m31 * n13 + m32 * n23 + m33 * n33;

        m00 = t00;
        m01 = t01;
        m02 = t02;
        m03 = t03;

        m10 = t10;
        m11 = t11;
        m12 = t12;
        m13 = t13;

        m20 = t20;
        m21 = t21;
        m22 = t22;
        m23 = t23;
        
        m30 = t30;
        m31 = t31;
        m32 = t32;
        m33 = t33;
    }

    private void nToM() {
        this.m00 = n00;
        this.m01 = n01;
        this.m02 = n02;
        this.m03 = n03;

        this.m10 = n10;
        this.m11 = n11;
        this.m12 = n12;
        this.m13 = n13;

        this.m20 = n20;
        this.m21 = n21;
        this.m22 = n22;
        this.m23 = n23;

        this.m30 = n30;
        this.m31 = n31;
        this.m32 = n32;
        this.m33 = n33;
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    public Matrix4f transpose() {
        Matrix4f result = new Matrix4f();

        result.m00 = this.m00;
        result.m10 = this.m01;
        result.m20 = this.m02;
        result.m30 = this.m03;

        result.m01 = this.m10;
        result.m11 = this.m11;
        result.m21 = this.m12;
        result.m31 = this.m13;

        result.m02 = this.m20;
        result.m12 = this.m21;
        result.m22 = this.m22;
        result.m32 = this.m23;

        result.m03 = this.m30;
        result.m13 = this.m31;
        result.m23 = this.m32;
        result.m33 = this.m33;

        return result;
    }

    public Matrix4f inverse() {
        Matrix4f inv = new Matrix4f();

        inv.m00 = m11 * m22 * m33 -
                m11 * m23 * m32 -
                m21 * m12 * m33 +
                m21 * m13 * m32 +
                m31 * m12 * m23 -
                m31 * m13 * m22;

        inv.m10 = -m10 * m22 * m33 +
                m10 * m23 * m32 +
                m20 * m12 * m33 -
                m20 * m13 * m32 -
                m30 * m12 * m23 +
                m30 * m13 * m22;

        inv.m20 = m10 * m21 * m33 -
                m10 * m23 * m31 -
                m20 * m11 * m33 +
                m20 * m13 * m31 +
                m30 * m11 * m23 -
                m30 * m13 * m21;

        inv.m30 = -m10 * m21 * m32 +
                m10 * m22 * m31 +
                m20 * m11 * m32 -
                m20 * m12 * m31 -
                m30 * m11 * m22 +
                m30 * m12 * m21;

        inv.m01 = -m01 * m22 * m33 +
                m01 * m23 * m32 +
                m21 * m02 * m33 -
                m21 * m03 * m32 -
                m31 * m02 * m23 +
                m31 * m03 * m22;

        inv.m11 = m00 * m22 * m33 -
                m00 * m23 * m32 -
                m20 * m02 * m33 +
                m20 * m03 * m32 +
                m30 * m02 * m23 -
                m30 * m03 * m22;

        inv.m21 = -m00 * m21 * m33 +
                m00 * m23 * m31 +
                m20 * m01 * m33 -
                m20 * m03 * m31 -
                m30 * m01 * m23 +
                m30 * m03 * m21;

        inv.m31 = m00 * m21 * m32 -
                m00 * m22 * m31 -
                m20 * m01 * m32 +
                m20 * m02 * m31 +
                m30 * m01 * m22 -
                m30 * m02 * m21;

        inv.m02 = m01 * m12 * m33 -
                m01 * m13 * m32 -
                m11 * m02 * m33 +
                m11 * m03 * m32 +
                m31 * m02 * m13 -
                m31 * m03 * m12;

        inv.m12 = -m00 * m12 * m33 +
                m00 * m13 * m32 +
                m10 * m02 * m33 -
                m10 * m03 * m32 -
                m30 * m02 * m13 +
                m30 * m03 * m12;

        inv.m22 = m00 * m11 * m33 -
                m00 * m13 * m31 -
                m10 * m01 * m33 +
                m10 * m03 * m31 +
                m30 * m01 * m13 -
                m30 * m03 * m11;

        inv.m32 = -m00 * m11 * m32 +
                m00 * m12 * m31 +
                m10 * m01 * m32 -
                m10 * m02 * m31 -
                m30 * m01 * m12 +
                m30 * m02 * m11;

        inv.m03 = -m01 * m12 * m23 +
                m01 * m13 * m22 +
                m11 * m02 * m23 -
                m11 * m03 * m22 -
                m21 * m02 * m13 +
                m21 * m03 * m12;

        inv.m13 = m00 * m12 * m23 -
                m00 * m13 * m22 -
                m10 * m02 * m23 +
                m10 * m03 * m22 +
                m20 * m02 * m13 -
                m20 * m03 * m12;

        inv.m23 = -m00 * m11 * m23 +
                m00 * m13 * m21 +
                m10 * m01 * m23 -
                m10 * m03 * m21 -
                m20 * m01 * m13 +
                m20 * m03 * m11;

        inv.m33 = m00 * m11 * m22 -
                m00 * m12 * m21 -
                m10 * m01 * m22 +
                m10 * m02 * m21 +
                m20 * m01 * m12 -
                m20 * m02 * m11;

        float det = m00 * inv.m00 + m01 * inv.m10 + m02 * inv.m20 + m03 * inv.m30;

        det = 1.0f / det;

        inv = inv.multiply(det);

        return inv;
    }

    public Matrix4f invert() {
        nIdentity();

        n00 = m11 * m22 * m33 -
                m11 * m23 * m32 -
                m21 * m12 * m33 +
                m21 * m13 * m32 +
                m31 * m12 * m23 -
                m31 * m13 * m22;

        n10 = -m10 * m22 * m33 +
                m10 * m23 * m32 +
                m20 * m12 * m33 -
                m20 * m13 * m32 -
                m30 * m12 * m23 +
                m30 * m13 * m22;

        n20 = m10 * m21 * m33 -
                m10 * m23 * m31 -
                m20 * m11 * m33 +
                m20 * m13 * m31 +
                m30 * m11 * m23 -
                m30 * m13 * m21;

        n30 = -m10 * m21 * m32 +
                m10 * m22 * m31 +
                m20 * m11 * m32 -
                m20 * m12 * m31 -
                m30 * m11 * m22 +
                m30 * m12 * m21;

        n01 = -m01 * m22 * m33 +
                m01 * m23 * m32 +
                m21 * m02 * m33 -
                m21 * m03 * m32 -
                m31 * m02 * m23 +
                m31 * m03 * m22;

        n11 = m00 * m22 * m33 -
                m00 * m23 * m32 -
                m20 * m02 * m33 +
                m20 * m03 * m32 +
                m30 * m02 * m23 -
                m30 * m03 * m22;

        n21 = -m00 * m21 * m33 +
                m00 * m23 * m31 +
                m20 * m01 * m33 -
                m20 * m03 * m31 -
                m30 * m01 * m23 +
                m30 * m03 * m21;

        n31 = m00 * m21 * m32 -
                m00 * m22 * m31 -
                m20 * m01 * m32 +
                m20 * m02 * m31 +
                m30 * m01 * m22 -
                m30 * m02 * m21;

        n02 = m01 * m12 * m33 -
                m01 * m13 * m32 -
                m11 * m02 * m33 +
                m11 * m03 * m32 +
                m31 * m02 * m13 -
                m31 * m03 * m12;

        n12 = -m00 * m12 * m33 +
                m00 * m13 * m32 +
                m10 * m02 * m33 -
                m10 * m03 * m32 -
                m30 * m02 * m13 +
                m30 * m03 * m12;

        n22 = m00 * m11 * m33 -
                m00 * m13 * m31 -
                m10 * m01 * m33 +
                m10 * m03 * m31 +
                m30 * m01 * m13 -
                m30 * m03 * m11;

        n32 = -m00 * m11 * m32 +
                m00 * m12 * m31 +
                m10 * m01 * m32 -
                m10 * m02 * m31 -
                m30 * m01 * m12 +
                m30 * m02 * m11;

        n03 = -m01 * m12 * m23 +
                m01 * m13 * m22 +
                m11 * m02 * m23 -
                m11 * m03 * m22 -
                m21 * m02 * m13 +
                m21 * m03 * m12;

        n13 = m00 * m12 * m23 -
                m00 * m13 * m22 -
                m10 * m02 * m23 +
                m10 * m03 * m22 +
                m20 * m02 * m13 -
                m20 * m03 * m12;

        n23 = -m00 * m11 * m23 +
                m00 * m13 * m21 +
                m10 * m01 * m23 -
                m10 * m03 * m21 -
                m20 * m01 * m13 +
                m20 * m03 * m11;

        n33 = m00 * m11 * m22 -
                m00 * m12 * m21 -
                m10 * m01 * m22 +
                m10 * m02 * m21 +
                m20 * m01 * m12 -
                m20 * m02 * m11;

        float det = m00 * n00 + m01 * n10 + m02 * n20 + m03 * n30;

        det = 1.0f / det;

        nToM();

        multiply(det);

        return this;
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


    private static Vector3f staticVec = new Vector3f();

    public static float shadowSkewLength(Vector3f direction, float size, float height) {
        staticVec.x = direction.x;
        staticVec.y = 0f;
        staticVec.z = direction.z;

        float len = staticVec.length();

        return Math.abs(height * direction.y) + Math.abs(size * len);
    }

    public Matrix4f shadowSkew(Vector3f direction, Vector3f origin,  float size, float height) {
        return shadowSkew(direction, origin, size, height, this);
    }

    public static Matrix4f shadowSkew(Vector3f direction, Vector3f origin, float size, float height, Matrix4f mat) {

        staticVec.x = direction.x;
        staticVec.y = 0f;
        staticVec.z = direction.z;

        float len = staticVec.length();

        staticVec = staticVec.normalize();

        if (mat == null)
            mat = new Matrix4f();

        mat.m00 = 0;
        mat.m01 = 0;
        mat.m02 = -1 / size;
        mat.m03 = 0;

        mat.m10 = -direction.y / ((Math.abs(len) * height + Math.abs(direction.y) * size));
        mat.m11 = len / ((Math.abs(len) * height + Math.abs(direction.y) * size));
        mat.m12 = 0;
        mat.m13 = 0;

        mat.m20 = -len / (size + height);
        mat.m21 = -direction.y / (size + height);
        mat.m22 = 0;
        mat.m23 = 0;

        mat.m30 = 0;
        mat.m31 = 0;
        mat.m32 = 0;
        mat.m33 = 1;

        float rotation;
        if (staticVec.z < 0)
            rotation = (float)Math.toDegrees(-Math.acos(staticVec.x));
        else
            rotation = (float)Math.toDegrees(Math.acos(staticVec.x));

        mat.rotate(rotation, 0, 1, 0);
        mat.translate(origin.negate());

        return mat;
    }
}