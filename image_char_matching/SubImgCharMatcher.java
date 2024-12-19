package image_char_matching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is going to match an ASCII character to a sub image
 * with a given brightness(it is going to transform sub images into chars)
 */

public class SubImgCharMatcher {


    private final CharConverter charConverter ;
    private final Set<Character> charSet ; //vs treeset
    private final HashMap<Character,Double> brightnessMap ;
    private final TreeSet<Double> minMaxBrightness ;
    private final HashMap<Character,Double> normalizedBrightness;
    private static final int START_MIN = 1;
    private static final int START_MAX = 0;
    private static final int defaulLowerASCII = 32;
    private static final int defaultUpperASCII = 126;
    private static final boolean REMOVED = true;

    private String roundType ;
    private double[] sortedBrightness;
    private double minBrightness ;
    private double maxBrightness ;
    private String typeOfRound;


    /**
     * The constructor for this class
     */
    public SubImgCharMatcher(char[] charSet){
        buildSet(charSet);
        this.minBrightness = START_MIN;
        this.maxBrightness = START_MAX;
        this.charSet = new HashSet<Character>();
        this.brightnessMap = new HashMap<>();
        this.minMaxBrightness = new TreeSet<>();
        this.normalizedBrightness = new HashMap<>();
        this.charConverter = new CharConverter();
        initializeStorageMap();
    }

    /**
     * Given a value of brightness for the sub image, the method will
     * return the char from the set of chars, with the closest brightness
     * in the absolute value to the given brightness.
     * Given a few chars with the same brightness this method will return the one
     * with the lowest ASCII value.
     */
    public char getCharByImageBrightness(double brightness){
        char fittedChar = defaultUpperASCII+1 ;
        for (char c: charSet) {
            double rounded = round(normalizedBrightness.get(c));
            if (rounded == brightness && rounded<fittedChar) {
                fittedChar = c;
            }
        }
        return fittedChar;
    }


    /**
     * This method adds a char to our set
     */
    public void addChar(char c){
        charSet.add(c);
        updateMinMax(c,!REMOVED);
    }

    /**
     * This method removes a char to our set
     */
    public void removeChar(char c){
        updateMinMax(c,REMOVED);
        normalizedBrightness.remove(c);
        // do not remove from brightnessMap so we have it stored anyway cause it does not change only depends on the number of pixels

    }

    /**
     * Method that builds our char set from the array of chars that we
     * get in the constructor
     */
    private void buildSet(char[] charSet) {
        if(charSet!=null){
            for (char c : charSet) {
                this.charSet.add(c);
            }
        }
    }

    /**
     * Finds char brightness before linear normalization
     */
    //this does not change no matter what
    private void convertChar(char c) {
        double brightnessCounter = 0;
        double brightnessValue ;
        int defaultPixelNumber = charConverter.DEFAULT_PIXEL_RESOLUTION;
        boolean[][] charArr = charConverter.convertToBoolArray(c);
        for(int i = 0;i<defaultPixelNumber;i++){
            for(int j=0;j<defaultPixelNumber;j++){
                if (charArr[i][j]) {
                    brightnessCounter=brightnessCounter+1;
                }
            }
        }
        brightnessValue = brightnessCounter/(double)defaultPixelNumber;
        brightnessMap.put(c,brightnessValue); // brightness storage
//        updateMinMax(c,false); // brightness values hierarchy
//        normalizeBrightness();
    }


    /**
     * Allows ASCII_art to define the way of rounding the brightness for each char
     */
    public void setTypeOfRound(String typeOfRound){ // default abs
        this.roundType = typeOfRound;
    }


    /**
     * Allows ASCII_art to check whether a given char is already in our normalized set of chars
     */
    public boolean charInSet(char c){
        return normalizedBrightness.get(c)!= null;
    }

    /**
     * Returns reference to our set of chars
     */
    public Set<Character> getCharList() {
        return charSet;
    }


    /**
     * Min max brightness update
     */
    private void updateMinMax(char c,boolean remove){
        double brightness = brightnessMap.get(c);
        if(!remove){
            minMaxBrightness.add(brightnessMap.get(c));
        }
        else{
            minMaxBrightness.remove(brightness);
        }
        if(brightness< minBrightness || brightness> maxBrightness){
            this.minBrightness = minMaxBrightness.first();
            this.maxBrightness = minMaxBrightness.last();
        }
    }

    /**
     * Linear normalization of the brightness of the chars
     */
    public void normalizeBrightness(){
        double newBrightness;
        for(char c : charSet){ // sa nu treci peste new
            newBrightness = calculateLinearNormalization(c);
            normalizedBrightness.put(c,newBrightness);
        }
    }

    /**
     * Returns the result of applying the newCharBrightness formula on our
     * initial values
     */
    private double calculateLinearNormalization(char c){
        double numerator = brightnessMap.get(c) - this.minBrightness;
        double denominator = this.maxBrightness - this.minBrightness;
        return numerator/ denominator;
    }

    /**
     * Initializes the map with all the possible values of chars from ASCII
     */
    private void initializeStorageMap() {
        for(char i = defaulLowerASCII;i<defaultUpperASCII+1;i++){
            convertChar(i);
        }
    }


    /**
     * Rounds the brightness we look for by the chosen method
     */
    private double round(Double brightness) {
        double roundedBrightness;
        switch(typeOfRound){
            case "up":
                roundedBrightness = Math.ceil(brightness);
                break;
            case "down":
                roundedBrightness= Math.floor(brightness);
                break;
            default:
                roundedBrightness = Math.abs(brightness);
                break;
        }
        return roundedBrightness;
    }

}