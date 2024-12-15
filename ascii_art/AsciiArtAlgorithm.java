package ascii_art;

import java.io.IOException;

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

    public AsciiArtAlgorithm(String path_image) throws IOException {
        this.image = new Image(path_image);
        this.resolution = 2;
        this.charMatcher= new SubImgCharMatcher(DEFAULT_CHAR_LIST);
        this.outputMethod = new ConsoleAsciiOutput();
    }

    public void loadImage(String path_image) throws IOException {
        this.image = new Image(path_image);
    }

    public int changeResolution(boolean up){
        resolution = up ? resolution*2 : resolution/2;
        return resolution;
    }

    private char[] getCharList(String charString) throws IllegalArgumentException {
        if (charString.equals("all")){
            char[] charList = new char[126-32+1];
            for (int i = 0; i < charList.length; i++) {
                charList[i] = (char)(32+i);
            }
            return charList;
        }
        else if (charString.equals("space")){
            return new char[]{' '};
        }
        else if (charString.length() == 1){
            return new char[]{charString.charAt(0)};
        }
        else if(charString.length() == 3 && charString.charAt(1) == '-'){
            char start = charString.charAt(0);
            char end = charString.charAt(2);
            char[] charList = new char[Math.abs(start-end)+1];
            if (start > end){
                for (int i = end; i <= charList.length; i++) {
                    charList[i] = (char)(end+i);
                }
            }
            else {
                for (int i = start; i <= charList.length; i++) {
                    charList[i] = (char)(start+i);
                }
            }
            return charList;
        }
        else {
            throw new IllegalArgumentException("incorrect format");
        }
    }

    public void addChars(String charString){
        char[] charList = getCharList(charString);
        for (int i = 0; i < charList.length; i++) {
            this.charMatcher.addChar(charList[i]);
        }
    }

    public void removeChars(String charString){
        char[] charList = getCharList(charString);
        for (int i = 0; i < charList.length; i++) {
            this.charMatcher.removeChar(charList[i]);
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

    public void changeRoundingMethod(String method) throws IllegalArgumentException {
        this.charMatcher.changeRoundingMethod(method); // TODO: implement changeRoundingMethod
    }

    public void doTheThing(){
        double[][] brightness = image.getImageBrightness(resolution);
        char[][] asciiArt = new char[brightness.length][brightness[0].length];
        for (int i = 0; i < brightness.length; i++) {
            for (int j = 0; j < brightness[0].length; j++) {
                asciiArt[i][j] = charMatcher.getClosestChar(brightness[i][j]); // TODO: implement getClosestChar
            }
        }
        outputMethod.out(asciiArt);
    }
}
