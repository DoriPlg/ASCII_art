package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
public class Image {

    private final Color[][] pixelArray;
    private final int width;
    private final int height;

    public Image(String filename) throws IOException {
        BufferedImage im = ImageIO.read(new File(filename));
        width = im.getWidth();
        height = im.getHeight();


        pixelArray = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelArray[i][j]=new Color(im.getRGB(j, i));
            }
        }
    }

    public Image(Color[][] pixelArray, int width, int height) {
        this.pixelArray = pixelArray;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getPixel(int x, int y) {
        return pixelArray[x][y];
    }

    public void saveImage(String fileName){
        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(pixelArray[0].length, pixelArray.length,
                BufferedImage.TYPE_INT_RGB);
        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < pixelArray.length; x++) {
            for (int y = 0; y < pixelArray[x].length; y++) {
                bufferedImage.setRGB(y, x, pixelArray[x][y].getRGB());
            }
        }
        File outputfile = new File(fileName+".jpeg");
        try {
            ImageIO.write(bufferedImage, "jpeg", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int bufferedWidth() {
        int newWidth = 2;
        while (newWidth < width) {
            newWidth *= 2;
        }
        return newWidth;
    }

    private int bufferedHeight() {
        int newHeight = 2;
        while (newHeight < height) {
            newHeight *= 2;
        }
        return newHeight;
    }

    private static double grayCode(Color color) {
        return color.getRed() * 0.2126 +
                color.getGreen() * 0.7152 +
                color.getBlue() * 0.0722;
    }


    private int[] buffer(){
        int buffH = bufferedHeight();
        int buffW = bufferedWidth();

        int topBuffer = (buffH-height)/2;
        int sideBuffer = (buffW-width)/2;

        Color[][] newPixelArray = new Color[buffH][buffW];
        for (int y = 0; y < bufferedHeight(); y++) {
            for (int x = 0; x < bufferedWidth(); x++) {
                if (x < sideBuffer || x > sideBuffer + width ||
                        y < topBuffer || y > topBuffer + height) {
                    newPixelArray[y][x]=new Color(255,255,255);
                }
                else {
                    newPixelArray[y][x] = getPixel(x+sideBuffer, y+sideBuffer);
                    // TODO: by value?
                }
            }
        }
        height = buffH;
        width = buffW;
        pixelArray = newPixelArray;
    }

    public Image[][] getSubImages(int resolution) {
        int pixelDim = width/resolution;
        int pixelPrHeight = height/pixelDim;

        Image[][] subImages = new Image[pixelPrHeight][resolution];
        for (int i = 0; i < resolution ; i++) {
            for (int j = 0; j < pixelPrHeight; j++) {
                Color[][] subImage = new Color[pixelDim][pixelDim];
                for (int x = 0; k < pixelDim; k++) {
                    for (int y = 0; y < pixelDim; y++) {
                        // TODO: by value?
                        subImage[y][x] = getPixel(x+resolution*j, y+resolution*i);
                    }
                }
                subImages[j][i]  = new Image(subImage,pixelDim,pixelDim);
            }
        }
        return subImages;
    }

    private double getbrightness(){
        double brightness = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                brightness += grayCode(getPixel(i,j));
            }
        }
        return brightness/(255*width*height);
    }

    public double[][] getbrightness(int resolution){
        Image[][] subImages = getSubImages(resolution);
        double[][] brightness = new double[resolution][height*resolution/width];
        for (int x = 0; x < resolution; x++) {
            for (int y = 0; y < (height * resolution / width); y++) {
                brightness[y][x] = subImages[y][x].getbrightness();
            }
        }
    }
}
