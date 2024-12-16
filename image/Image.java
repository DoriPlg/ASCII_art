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
    private static final double RED_FACTOR = 0.2126;
    private static final double GREEN_FACTOR = 0.7152;
    private static final double BLUE_FACTOR = 0.0722;
    private static final Color WHITE = new Color(255, 255, 255);

    private final Color[][] pixelArray;
    private final int width;
    private final int height;

    /**
     * Constructor for the Image class.
     * @param filename The name of the file to be read.
     * @throws IOException If the file is not found.
     */
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

    /**
     * Constructor for the Image class.
     * @param pixelArray A 2D array of Color objects.
     * @param width The width of the image.
     * @param height The height of the image.
     */
    public Image(Color[][] pixelArray, int width, int height) {
        this.pixelArray = pixelArray;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width of the image.
     * @return The width of the image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image.
     * @return The height of the image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the pixel at the given coordinates.
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return The Color object at the given coordinates.
     */
    public Color getPixel(int x, int y) {
        return pixelArray[y][x];
    }

    /**
     * Sets the pixel at the given coordinates to the given color.
     * @param fileName The name of the file to be saved.
     */
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

    /**
     * Calculates the width the image should be buffered to.
     * @return The width the image should be buffered to.
     */
    private int bufferedWidth() {
        int newWidth = 2;
        while (newWidth < width) {
            newWidth *= 2;
        }
        return newWidth;
    }

    /**
     * Calculates the height the image should be buffered to.
     * @return The height the image should be buffered to.
     */
    private int bufferedHeight() {
        int newHeight = 2;
        while (newHeight < height) {
            newHeight *= 2;
        }
        return newHeight;
    }

    /**
     * Calculates the gray code of a given color.
     * @param color The color to calculate the gray code of.
     * @return The gray code of the given color.
     */
    private static double grayCode(Color color) {
        return color.getRed() * RED_FACTOR +
                color.getGreen() * GREEN_FACTOR +
                color.getBlue() * BLUE_FACTOR;
    }

    /**
     * Buffers the image to the nearest power of 2.
     * @return The buffered image.
     */
    public Image getBuffered(){
        int buffH = bufferedHeight();
        int buffW = bufferedWidth();

        int topBuffer = (buffH-height)/2;
        int sideBuffer = (buffW-width)/2;

        Color[][] newPixelArray = new Color[buffH][buffW];
        for (int y = 0; y < bufferedHeight(); y++) {
            for (int x = 0; x < bufferedWidth(); x++) {
                if (x < sideBuffer || x > sideBuffer + width ||
                        y < topBuffer || y > topBuffer + height) {
                    newPixelArray[y][x]= WHITE;
            }
            else {
                newPixelArray[y][x] = getPixel(x + sideBuffer, y + topBuffer);
            }
        }
    }
    return new Image(newPixelArray, buffW, buffH);
}

    /**
     * Splits the image into sub-images of a given resolution.
     * @param resolution The of parts to split the image width into.
     * @return A 2D array of Image objects. An array of rows!
     */
    private Image[][] getSubImages(int resolution) {
        int pixelDim = width/resolution;
        int pixelPrHeight = height/pixelDim;

        Image[][] subImages = new Image[pixelPrHeight][resolution];
        for (int i = 0; i < resolution ; i++) {
            for (int j = 0; j < pixelPrHeight; j++) {
                Color[][] subImage = new Color[pixelDim][pixelDim];
                for (int x = 0; x < pixelDim; x++) {
                    for (int y = 0; y < pixelDim; y++) {
                        subImage[y][x] = getPixel(x + resolution*i, y + resolution*j);
                    }
                }
                subImages[j][i]  = new Image(subImage,pixelDim,pixelDim);
            }
        }
        return subImages;
    }

    /**
     * Calculates the brightness of the image.
     * @return The brightness of the image.
     */
    private double getPixelBrightness(){
        double brightness = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                brightness += grayCode(getPixel(i,j));
            }
        }
        return brightness/(255*width*height);
    }

    /**
     * Calculates the brightnessof each sub-image (pixel) of the image.
     * @param resolution The resolution of the sub-images.
     * @return A 2D array of doubles, representing the brightness of each sub-image. An array of rows!
     */
    public double[][] getImageBrightness(int resolution){
        Image[][] subImages = getSubImages(resolution);
        double[][] brightness = new double[resolution][height*resolution/width];
        for (int x = 0; x < resolution; x++) {
            for (int y = 0; y < (height * resolution / width); y++) {
                brightness[y][x] = subImages[y][x].getPixelBrightness();
            }
        }
        return brightness;
    }
}
