package util;

public class Vec3 {
    public float x = 0;
    public float y = 0;
    public float z = 0;

    public float[] toArray() {
        return new float[] {x,y,z};
    }
    public String toString() {
        return x+","+y+","+z;
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
