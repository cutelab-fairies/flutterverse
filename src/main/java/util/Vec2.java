package util;

public class Vec2 {
    public float x = 0;
    public float y = 0;

    public float[] toArray() {
        return new float[] {x,y};
    }
    public String toString() {
        return x+","+y;
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
