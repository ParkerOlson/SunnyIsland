package com.raven.engine.util.math;

public class Quaternion {

    public float w, x, y, z;

    public static Quaternion tempQuat = new Quaternion();

    @Deprecated
    public static Quaternion lerp(Quaternion a, Quaternion b, float alpah, Quaternion out) {
        return lerp(a, b, alpah, true, out);
    }

    @Deprecated
    public static Quaternion lerp(Quaternion a, Quaternion b, float alpah, boolean normalize, Quaternion out) {

        if (normalize) {
            tempQuat.w = a.w * (1f - alpah) + b.w * alpah;
            tempQuat.x = a.x * (1f - alpah) + b.x * alpah;
            tempQuat.y = a.y * (1f - alpah) + b.y * alpah;
            tempQuat.z = a.z * (1f - alpah) + b.z * alpah;

            tempQuat.normalize(out);
        } else {
            out.w = a.w * (1f - alpah) + b.w * alpah;
            out.x = a.x * (1f - alpah) + b.x * alpah;
            out.y = a.y * (1f - alpah) + b.y * alpah;
            out.z = a.z * (1f - alpah) + b.z * alpah;
        }

        return out;
    }

    public static Quaternion slerp(Quaternion a, Quaternion b, float alpha, Quaternion out) {
		final float d = a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
		float absDot = d < 0.f ? -d : d;

		// Set the first and second scale for the interpolation
		float scale0 = 1f - alpha;
		float scale1 = alpha;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if ((1 - absDot) > 0.1) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			final float angle = (float)Math.acos(absDot);
			final float invSinTheta = 1f / (float)Math.sin(angle);

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = ((float)Math.sin((1f - alpha) * angle) * invSinTheta);
			scale1 = ((float)Math.sin((alpha * angle)) * invSinTheta);
		}

		if (d < 0.f) scale1 = -scale1;

		// Calculate the x, y, z and w values for the quaternion by using a
		// special form of linear interpolation for quaternions.
        out.w = (scale0 * a.w) + (scale1 * b.w);
		out.x = (scale0 * a.x) + (scale1 * b.x);
		out.y = (scale0 * a.y) + (scale1 * b.y);
		out.z = (scale0 * a.z) + (scale1 * b.z);

		// Return the interpolated quaternion
        return out;
    }

    public Quaternion() {
        this(1,0,0,0);
    }

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        normalize();
    }

    public Matrix4f toMatrix(Matrix4f out) {
        out.m00 = 1f - 2f*y*y - 2f*z*z;
        out.m01 = 2f*x*y - 2f*z*w;
        out.m02 = 2f*x*z + 2f*y*w;
        out.m03 = 0f;

        out.m10 = 2f*x*y + 2f*z*w;
        out.m11 = 1f - 2f*x*x - 2f*z*z;
        out.m12 = 2f*y*z - 2f*x*w;
        out.m13 = 0f;

        out.m20 = 2f*x*z - 2f*y*w;
        out.m21 = 2f*y*z + 2f*x*w;
        out.m22 = 1f - 2f*x*x - 2f*y*y;
        out.m23 = 0f;

        out.m30 = 0f;
        out.m31 = 0f;
        out.m32 = 0f;
        out.m33 = 1f;

        return out;
    }

    public Quaternion scale(float s, Quaternion out) {
        out.w = this.w * s;
        out.x = this.x * s;
        out.y = this.y * s;
        out.z = this.z * s;

        return out;
    }

    public Quaternion normalize(Quaternion out) {
        this.scale(1 / this.length(), out);

        return out;
    }

    public Quaternion normalize() {
        return normalize(this);
    }

    public float length2() {
        return w*w + x*x + y*y + z*z;
    }

    public float length() {
        return (float)Math.sqrt(length2());
    }

    @Override
    public String toString() {
        return "" + w + " " + x + " " + y + " " + z;
    }
}
