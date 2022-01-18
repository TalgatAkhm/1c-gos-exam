package image;

/**
 * Image for gradient rectangle search.
 */
public class Image extends ImageMatrix<Pixel> {
    public Image(int width, int height) {
        super(width, height, Pixel.class);
    }
}
