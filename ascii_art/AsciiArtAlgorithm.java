package ascii_art;

import java.io.IOException;
import java.util.MissingFormatWidthException;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;

public class AsciiArtAlgorithm {
    private static final char[] DEFAULT_CHAR_LIST = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    private Image image;
    private int resolution;
    private SubImgCharMatcher charMatcher;
    private AsciiOutput outputMethod;
    private boolean changeImage;
    private boolean changeCharSet;
    private double[][] brightness;

    public AsciiArtAlgorithm(String path_image) throws IOException {
        this.image = new Image(path_image);
        this.resolution = 2;
        this.charMatcher= new SubImgCharMatcher(DEFAULT_CHAR_LIST);
        this.outputMethod = new ConsoleAsciiOutput();
        this.changeImage = true;
        this.changeCharSet = true;
    }

    public void loadImage(String path_image) throws IOException {
        this.image = new Image(path_image);
        this.changeImage = true;
    }

    public int changeResolution(boolean up) throws BadResolutionException {
        if (up && resolution * 2 > image.getWidth() ||
         !up && resolution / 2 < Math.max(1,image.getWidth()/image.getHeight())){
            throw new BadResolutionException("exceeding boundaries");
        }
        this.resolution = up ? resolution*2 : resolution/2;
        changeImage = true;      
        return resolution;
    }

    public void addChars(char[] charList){
        for (int i = 0; i < charList.length; i++) {
            if (!charMatcher.charInSet(charList[i])){
                this.charMatcher.addChar(charList[i]);
                changeCharSet = true;
            }
        }
    }

    public void removeChars(char[] charList){
        for (int i = 0; i < charList.length; i++) {
            if (charMatcher.charInSet(charList[i])){
                this.charMatcher.removeChar(charList[i]);
                changeCharSet = true;
            }
        }
    }

    public void htmlOutput(String filename, String fontName){
        this.outputMethod = new HtmlAsciiOutput(filename, fontName);
    }

    public void consoleOutput(){
        if (!(outputMethod instanceof ConsoleAsciiOutput)){
            this.outputMethod = new ConsoleAsciiOutput();
        }
    }

    /**
     * Changes the rounding method used to match the brightness of the image to the characters
     * @param method an integer that represents the rounding method
     * 1: round up
     * -1: round down
     * 0: round to the closest
     */
    public void changeRoundingMethod(int method){
        if (method >0){
            this.charMatcher.setTypeOfRound(charMatcher.ROUND_UP);
        }
        else if (method < 0){
            this.charMatcher.setTypeOfRound(charMatcher.ROUND_DOWN);
        }
        else{
            this.charMatcher.setTypeOfRound(charMatcher.ROUND_ABS);
        }
    }

    public char[] getCharList() {
        return charMatcher.getCharList();
    }

    public void doTheThing() throws TooSmallSetException{
        if (charMatcher.getCharList().length < 2){
            throw new TooSmallSetException();
        }
        if (changeImage){
            brightness = image.getImageBrightness(resolution);
            changeImage = false;
        }
        if (changeCharSet){
            charMatcher.normalize();
            changeCharSet = false;
        }
        
        char[][] asciiArt = new char[brightness.length][brightness[0].length];
        for (int i = 0; i < brightness.length; i++) {
            for (int j = 0; j < brightness[0].length; j++) {
                asciiArt[i][j] = charMatcher.getCharByImageBrightness(brightness[i][j]);
            }
        }
        outputMethod.out(asciiArt);
    }

    class BadResolutionException extends Exception {
        public BadResolutionException(String message) {
            super(message);
        }
    }

    class TooSmallSetException extends Exception {
        public TooSmallSetException() {
            super("Charset is too small.");
        }
    }
}
