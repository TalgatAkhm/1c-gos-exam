package image;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ImageMatrix<T> {
    private final int width;
    private final int height;
    private final List<List<T>> image;

    private final Constructor<? extends T> ctor;
//    private final T[][] image;


    public ImageMatrix(int width, int height, Class<? extends T> impl) {
        try {
            this.ctor = impl.getConstructor();
            image = new ArrayList<>(height);
            for (int i = 0; i < height; i++) {
                image.add(new ArrayList<>(width));
                for (int j = 0; j < width; j++)
                    image.get(i).add(ctor.newInstance());
            }
//        image = (T[][]) new Object[height][width];


            this.width = width;
            this.height = height;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public T getPixel(int x, int y) {
        assert x < width;
        assert y < height;

        return image.get(y).get(x);
//        return image[y][x];
    }

    public ImageMatrix<T> setPixel(int x, int y, T pixel) {
        assert x < width;
        assert y < height;

//        image[y][x] = pixel;
        image.get(y).set(x, pixel);
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
