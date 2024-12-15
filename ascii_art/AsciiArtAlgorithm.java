package ascii_art;

import java.io.IOException;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;

public class AsciiArtAlgorithm {
    private static final char[] DEFAULT_CHAR_LIST = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String ABS = "abs";


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

    public int changeResolution(String direction) throws IllegalArgumentException {
        if (direction.equals("up")){
            this.resolution *= 2;
        }
        else if (direction.equals("down")){
            this.resolution /= 2;
        }
        else {
            throw new IllegalArgumentException("incorrect direction"); // TODO: change to a more specific exception
        }
        changeImage = true;      
        return resolution;
    }

    // TODO: migrate to Shell
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
            if (!charMatcher.charInSet(charList[i])){
                this.charMatcher.addChar(charList[i]);
                changeCharSet = true;
            }
        }
    }

    public void removeChars(String charString){
        char[] charList = getCharList(charString);
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

    public void changeRoundingMethod(String method) throws IllegalArgumentException {
        if (method.equals(UP)){
            this.charMatcher.setTypeOfRound(charMatcher.ROUND_UP);
        }
        else if (method.equals(DOWN)){
            this.charMatcher.setTypeOfRound(charMatcher.ROUND_DOWN);
        }
        else if (method.equals(ABS)){
            this.charMatcher.setTypeOfRound(charMatcher.ROUND_ABS);
        }
        else {
            throw new IllegalArgumentException("incorrect format");// TODO: migrate to Shell
        }
    }

    public void doTheThing(){
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
}
