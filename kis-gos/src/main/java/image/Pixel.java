package image;

import java.util.Objects;

/**
 * Pixel in image.
 */
public class Pixel {
    /**
     * EPS to approximate comparison of rgb values.
     */
    public static final int EPS = 10;
    private final int r;
    private final int g;
    private final int b;

    public Pixel()
    {
        this.r = 0;
        this.g = 0;
        this.b = 0;
    }

    public static Pixel black() {
        return new Pixel(0,0,0);
    }

    public Pixel(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Returns image.Pixel of differential of current and another.
     */
    public Pixel diff(Pixel another)
    {
        return new Pixel(r - another.r, g - another.g, b - another.b);
    }

    public boolean isBlack() {
        return r == 0 && g == 0 && b == 0;
    }

    /**
     * Condition of there is no any values in rgb colors bigger then EPS.
     */
    public boolean isBlackApproximately() {
        return Math.abs(r) <= EPS && Math.abs(g) <= EPS && Math.abs(b) <= EPS;
    }

    /**
     * Compare two gradient states with EPS to rgb values.
     */
    public boolean equalsApproximately(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pixel)) return false;
        Pixel pixel = (Pixel) o;

        return Math.abs(r - pixel.r) <= EPS && Math.abs(g - pixel.g) <= EPS && Math.abs(b - pixel.b) <= EPS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pixel)) return false;
        Pixel pixel = (Pixel) o;
        return r == pixel.r && g == pixel.g && b == pixel.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
