package ascii_art;

import java.io.IOException;
import java.util.Set;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;

class AsciiArtAlgorithm {
    private static final char[] DEFAULT_CHAR_LIST = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    private Image image;
    private int resolution;
    private final SubImgCharMatcher charMatcher;
    private boolean changeImage;
    private boolean changeCharSet;
    private double[][] brightness;

    /**
     * Constructor for the AsciiArtAlgorithm class. Initializes the image, resolution, charMatcher, and outputMethod.
     * Sets thee change flags to true.
     * @param pathImage the path to the image file.
     * @throws IOException on file open error.
     */
    public AsciiArtAlgorithm(String pathImage) throws IOException {
        loadImage(pathImage);
        this.resolution = 2;
        this.charMatcher= new SubImgCharMatcher(DEFAULT_CHAR_LIST);
        this.changeImage = true;
        this.changeCharSet = true;
    }

    /**
     * Changes the image to a new image.
     * @param pathImage the path to the new image file.
     * @throws IOException on file open error.
     */
    private void loadImage(String pathImage) throws IOException {
        this.image =Image.getBuffered( new Image(pathImage));
        this.changeImage = true;
    }

    /**
     * Changes the resolution of the image.
     * @param up true if the resolution should be increased, false if it should be decreased.
     * @return the new resolution.
     * @throws BadResolutionException if the resolution is out of bounds.
     */
    public int changeResolution(boolean up) throws BadResolutionException {
        if (up && resolution * 2 > image.getWidth() ||
         !up && resolution / 2 < Math.max(1,image.getWidth()/image.getHeight())){
            throw new BadResolutionException();
        }
        this.resolution = up ? resolution*2 : resolution/2;
        changeImage = true;      
        return resolution;
    }

    /**
     * Adds a character to the character set, if it is not already in the set.
     * Sets the changeCharSet flag to true.
     * @param charList the characters to be added.
     */
    public void addChars(char[] charList){
        int pre_size = charMatcher.getCharSet().size();
        for (char c : charList) charMatcher.addChar(c);
        if (pre_size !=  charMatcher.getCharSet().size()) changeCharSet = true;
    }

    /**
     * Removes a character from the character set, if it is in the set.
     * Sets the changeCharSet flag to true.
     * @param charList the characters to be removed.
     */
    public void removeChars(char[] charList){
        int pre_size = charMatcher.getCharSet().size();
        for (char c : charList) charMatcher.removeChar(c);
        if (pre_size != charMatcher.getCharSet().size()) changeCharSet = true;
    }



    /**
     * Changes the rounding method used to match the brightness of the image to the characters
     * @param bias an integer that represents the rounding method
     * 1: round up
     * -1: round down
     * 0: round to the closest
     */
    public void changeRoundingMethod(int bias){
        if (bias >0){
            this.charMatcher.setTypeOfRound(SubImgCharMatcher.ROUND_UP);
        }
        else if (bias < 0){
            this.charMatcher.setTypeOfRound(SubImgCharMatcher.ROUND_DOWN);
        }
        else{
            this.charMatcher.setTypeOfRound(SubImgCharMatcher.ROUND_ABS);
        }
    }

    /**
     * Gets the character list currently in use.
     * @return the character list.
     */
    public Set<Character> getCharList() {
        return charMatcher.getCharSet();
    }

    /**
     * The main method of the class, with the current settings, generates the ascii art and returns it.
     * Takes care to only recalculate the brightness if the image has changed, and to only normalize the character set if it has changed.
     * @return char[][] defining the art.
     * @throws TooSmallSetException if the character set is too small.
     */
    public char[][] run() throws TooSmallSetException{
        if (charMatcher.getCharSet().size() < 2){
            throw new TooSmallSetException();
        }
        if (changeImage){
            System.out.println("Calculating brightness");
            brightness = image.getImageBrightness(resolution);
            changeImage = false;
        }
        if (changeCharSet){
            System.out.println("Normalizing chars");
            charMatcher.normalizeBrightness();
            changeCharSet = false;
        }


        
        char[][] asciiArt = new char[brightness.length][brightness[0].length];
        for (int i = 0; i < brightness.length; i++) {
            for (int j = 0; j < brightness[0].length; j++) {
                asciiArt[i][j] = charMatcher.getCharByImageBrightness(brightness[i][j]);
            }
        }
        return asciiArt;
    }

    /**
     * Exception for when the resolution is out of bounds.
     */
    static class BadResolutionException extends Exception {
        public BadResolutionException() {
            super("exceeding boundaries");
        }
    }

    /**
     * Exception for when the character set is too small.
     */
    static class TooSmallSetException extends Exception {
        public TooSmallSetException() {
            super("Charset is too small.");
        }
    }
}
