package ascii_art;

import image.Image;
import image.PrepareImage;
import image_char_matching.SubImgCharMatcher;


/**
 * The AsciiArtAlgorithm class is responsible for converting an image to ascii art.
 * It uses the brightness of the image to determine the character to be used.
 */
class AsciiArtAlgorithm {

    /**
     * The image snapshot and last image are used to avoid recalculating the brightness matrix
     */
    private static ImageSnapshot imgSnap = null;
    private static PrepareImage lastImage = null;

    /**
     * The image, resolution, charMatcher, and outputMethod are the settings of the algorithm.
     */
    private final PrepareImage image;
    private final int resolution;
    private final SubImgCharMatcher characterMatcher;

    /**
     * Constructor for the AsciiArtAlgorithm class. Initializes the image, resolution,
     * charMatcher, and outputMethod.
     * Sets the change flags to true.
     * @param img the image to be converted to ascii art.
     * @param resolution the resolution of the ascii art.
     * @param characterMatcher the character matcher to be used.
     */
    public AsciiArtAlgorithm(Image img, int resolution, SubImgCharMatcher characterMatcher){
        if (lastImage != null && lastImage.getOriginalImage() == img){
            image = lastImage;
        } else {
            image = new PrepareImage(img);
            lastImage = image;
            imgSnap = null;
        }
        this.resolution = resolution;
        this.characterMatcher = characterMatcher;
    }

    /**
     * The main method of the class, with the current settings, generates the ascii art and returns it.
     * @return char[][] defining the art.
     * @throws TooSmallSetException if the character set is too small.
     */
    public char[][] run() throws TooSmallSetException{
        if (characterMatcher.getCharSet().size() < 2){
            throw new TooSmallSetException();
        }
        double[][] brightness = getBrightnessMatrix();

        
        char[][] asciiArt = new char[brightness.length][brightness[0].length];
        for (int i = 0; i < brightness.length; i++) {
            for (int j = 0; j < brightness[0].length; j++) {
                asciiArt[i][j] = characterMatcher.getCharByImageBrightness(brightness[i][j]);
            }
        }
        return asciiArt;
    }

    /**
     * Returns the brightness matrix of the image.
     * Avoids recalculating the brightness matrix if the image has not changed.
     * @return the brightness matrix of the image.
     */
    private double[][] getBrightnessMatrix(){
        if (imgSnap == null || resolution != imgSnap.resolution()){
            imgSnap = new ImageSnapshot(resolution,image.getImageBrightness(resolution));
        }
        return imgSnap.brightness();
    }

    /**
     * Exception for when the character set is too small.
     */
    static class TooSmallSetException extends Exception {
        public TooSmallSetException() {
            super("Charset is too small.");
        }
    }

    /**
     * Private record to store the image, resolution, and brightness matrix.
     * Used to avoid recalculating the brightness matrix if the image and resolution have not changed.
     */
    private record ImageSnapshot(int resolution, double[][] brightness) {}
}
