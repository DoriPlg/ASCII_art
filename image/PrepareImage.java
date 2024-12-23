package image;

import java.awt.Color;

/**
 * Tasked with preparing the image for the ascii art algorithm
 */
public class PrepareImage {
    
    /**
     * The factors used to calculate the gray code of a color.
     */
    private static final double RED_FACTOR = 0.2126;
    private static final double GREEN_FACTOR = 0.7152;
    private static final double BLUE_FACTOR = 0.0722;
    private static final Color WHITE = new Color(255, 255, 255);
    private static final double GRAY_WHITE = 255;

    /**
     * Instance variables for the PrepareImage class.
     * The buffered image is the image that has been buffered to the nearest power of 2.
     * The original image is the image that was passed to the constructor.
     */
    private Image bufferedImage;
    private Image originalImage;


    /**
     * Constructor for the PrepareImage class.
     * @param image The image to be prepared.
     */
    public PrepareImage(Image image) {
        this.bufferedImage = getBuffered(image);
        this.originalImage = image;
    }

    /**
     * Returns the buffered image.
     * @return The buffered image.
     */
    public Image getOriginalImage() {
        return originalImage;
    }
  
    /**
     * Calculates the width the image should be buffered to.
     * @return The width the image should be buffered to.
     */
    private int bufferedWidth(Image image) {
        int newWidth = 2;
        while (newWidth < image.getWidth()) {
            newWidth *= 2;
        }
        return newWidth;
    }

    /**
     * Calculates the height the image should be buffered to.
     * @return The height the image should be buffered to.
     */
    private static int bufferedHeight(Image image) {
        int newHeight = 2;
        while (newHeight < image.getHeight()) {
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
        if (color.equals(WHITE)) {
            // To pre-handle the case of white pixels, avoid incorrect rounding.
            return GRAY_WHITE;
        }
        return color.getRed() * RED_FACTOR +
                color.getGreen() * GREEN_FACTOR +
                color.getBlue() * BLUE_FACTOR;
    }
    
    /**
     * Calculates the brightness of the image.
     * @return The brightness of the image.
     */
    private double getPixelBrightness(Image image) {
        double brightness = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                brightness += grayCode(image.getPixel(i,j));
            }
        }
        return brightness/(255*image.getWidth()*image.getHeight());
    }

    /**
     * Buffers the image to the nearest power of 2.
     * @param image The image to be buffered.
     * @return The buffered image.
     */
    private Image getBuffered(Image image) {
        int buffH = bufferedHeight(image);
        int buffW = bufferedWidth(image);

        int topBuffer = (buffH-image.getHeight())/2;
        int sideBuffer = (buffW-image.getWidth())/2;

        Color[][] newPixelArray = new Color[buffH][buffW];
        for (int y = 0; y < buffH ;y++) {
            for (int x = 0; x < buffW; x++) {
                if (x < sideBuffer || x >= sideBuffer + image.getWidth() ||
                        y < topBuffer || y >= topBuffer + image.getHeight()) {
                    newPixelArray[y][x]= WHITE;
                }
                else {
                    newPixelArray[y][x] = image.getPixel(x - sideBuffer, y - topBuffer);
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
        int pixelDim = bufferedImage.getWidth()/resolution;
        int pixelPrHeight = bufferedImage.getHeight()/pixelDim;

        Image[][] subImages = new Image[pixelPrHeight][resolution];
        for (int i = 0; i < resolution ; i++) {
            for (int j = 0; j < pixelPrHeight; j++) {
                Color[][] subImage = new Color[pixelDim][pixelDim];
                for (int x = 0; x < pixelDim; x++) {
                    for (int y = 0; y < pixelDim; y++) {
                        subImage[y][x] = bufferedImage.getPixel(x + pixelDim*i, y + pixelDim*j);
                    }
                }
                subImages[j][i]  = new Image(subImage,pixelDim,pixelDim);
            }
        }
        return subImages;
    }
    
    /**
     * Calculates the brightnessof each sub-image (pixel) of the image.
     * @param resolution The resolution of the sub-images.
     * @return A 2D array of doubles, representing the brightness of each sub-image. An array of rows!
     */
    public double[][] getImageBrightness(int resolution){
        Image[][] subImages = getSubImages(resolution);
        double[][] brightness = 
        new double[resolution][bufferedImage.getHeight()*resolution/bufferedImage.getWidth()];
        for (int x = 0; x < resolution; x++) {
            for (int y = 0; 
                y < (bufferedImage.getHeight() * resolution / bufferedImage.getWidth());
                y++) {
                brightness[y][x] = getPixelBrightness(subImages[y][x]);
            }
        }
        return brightness;
    }
}
