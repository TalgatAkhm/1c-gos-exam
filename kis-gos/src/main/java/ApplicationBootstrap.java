import image.Image;
import image.Pixel;
import imagegradient.GradientSearcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class ApplicationBootstrap {
    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        new ApplicationBootstrap().saveGradPicture(args[0], null);
    }

    public void saveGradPicture(String inputPicture, String toFile) throws IOException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {

        int[] pixel;
        BufferedImage im = ImageIO.read(new File(inputPicture));
        var raster = im.getRaster();

        Image curIm = new image.Image(raster.getWidth(), raster.getHeight());

        System.out.println("Picture size: " + raster.getWidth() + " x " + raster.getHeight());
        for (int i = 0; i < raster.getHeight(); i++) {
            for (int j = 0; j < raster.getWidth(); j++) {
                pixel = raster.getPixel(j, i, new int[3]);
                curIm.setPixel(j, i, new Pixel(pixel[0], pixel[1], pixel[2]));
            }
        }

        GradientSearcher searcher = new GradientSearcher(curIm);
        searcher.findGradient();
        List<Rectangle> rectangles = searcher.getRectanglesHistory();

        Rectangle r = searcher.getBiggest();

        System.out.println("Max rectangle with gradient: " + r);
        for (int i = 0; i < r.width; i++) {
            raster.setPixel(r.x + i, r.y, new int[]{0, 0, 0});
            raster.setPixel(r.x + i, r.y + r.height - 1, new int[]{0, 0, 0});

        }
        for (int j = 0; j < r.height; j++) {
            raster.setPixel(r.x, r.y + j, new int[]{0, 0, 0});
            raster.setPixel(r.x + r.width - 1, r.y + j, new int[]{0, 0, 0});
        }


        BufferedImage res = new BufferedImage(raster.getWidth(), raster.getHeight(), im.getType());
        res.setData(raster);
        if (toFile == null)
            toFile = "out.png";
        ImageIO.write(res, "png", new File(toFile));
    }
}
