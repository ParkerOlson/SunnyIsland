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
 * This class represents a (x,y,z)-Vector. GLSL equivalent to vec3.
 *
 * @author Heiko Brumme
 */
public class Vector3f {

    private static Vector3f tempVec = new Vector3f();
    private static Vector3f tempVec2 = new Vector3f();

    public float x;
    public float y;
    public float z;

    private FloatBuffer buffer = BufferUtils.createFloatBuffer(3);

    /**
     * Creates a default 3-tuple vector with all values set to 0.
     */
    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }

    /**
     * Creates a 3-tuple vector with specified values.
     *
     * @param x x value
     * @param y y value
     * @param z z value
     */
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Calculates the squared length of the vector.
     *
     * @return Squared length of this vector
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Calculates the length of the vector.
     *
     * @return Length of this vector
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector.
     *
     * @return Normalized vector
     */
    public Vector3f normalize(Vector3f out) {
        float length = length();
        return divide(length, out);
    }

    /**
     * Adds this vector to another vector.
     *
     * @param other The other vector
     *
     * @return Sum of this + other
     */
    public Vector3f add(Vector3f other, Vector3f out) {
        out.x = this.x + other.x;
        out.y = this.y + other.y;
        out.z = this.z + other.z;
        return out;
    }

    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    public Vector3f negate(Vector3f out) {
        return scale(-1f, out);
    }

    /**
     * Subtracts this vector from another vector.
     *
     * @param other The other vector
     *
     * @return Difference of this - other
     */
    public Vector3f subtract(Vector3f other, Vector3f out) {
        return this.add(other.negate(tempVec), out);
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     *
     * @return Scalar product of this * scalar
     */
    public Vector3f scale(float scalar, Vector3f out) {
        out.x = this.x * scalar;
        out.y = this.y * scalar;
        out.z = this.z * scalar;
        return out;
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     *
     * @return Scalar quotient of this / scalar
     */
    public Vector3f divide(float scalar, Vector3f out) {
        return scale(1f / scalar, out);
    }

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     *
     * @return Dot product of this * other
     */
    public float dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     *
     * @return Cross product of this x other
     */
    public Vector3f cross(Vector3f other, Vector3f out) {
        out.x = this.y * other.z - this.z * other.y;
        out.y = this.z * other.x - this.x * other.z;
        out.z = this.x * other.y - this.y * other.x;
        return out;
    }

    /**
     * Calculates a linear interpolation between two vectors.
     *
     * @param a The a vector
     * @param b The b vector
     * @param alpha The alpha value, should be between 0.0 and 1.0
     *
     * @return Linear interpolated vector
     */
    public static Vector3f lerp(Vector3f a, Vector3f b, float alpha, Vector3f out) {
        out.x = a.x * (1f - alpha) + b.x * alpha;
        out.y = a.y * (1f - alpha) + b.y * alpha;
        out.z = a.z * (1f - alpha) + b.z * alpha;

        return out;
    }

    /**
     * Stores the vector in a given Buffer.
     *
     * @param buffer The buffer to store the vector data
     */
    public void toBuffer(FloatBuffer buffer) {
        buffer.put(x).put(y).put(z);
    }

    public FloatBuffer toBuffer() {
        toBuffer(buffer);
        buffer.flip();
        return buffer;
    }

    @Deprecated
    public Float[] toArray() {
        return new Float[] { x, y, z };
    }

    public String toString() {
        return "" + x + ", " + y + ", " + z;
    }
}