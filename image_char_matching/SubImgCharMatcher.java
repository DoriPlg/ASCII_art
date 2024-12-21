package image_char_matching;

import java.util.*;

/**
 * This class is going to match an ASCII character to a sub image
 * with a given brightness(it is going to transform sub images into chars)
 */

public class SubImgCharMatcher {


    private final Set<Character> charSet ; //vs treeset
    private final HashMap<Character,Double> brightnessMap ;
    private final TreeSet<Double> minMaxBrightness ;
    private final SortedMap<Double,Character> normalizedBrightness;
    private static final int START_MIN = 1;
    private static final int START_MAX = 0;
    private static final int defaulLowerASCII = 32;
    private static final int defaultUpperASCII = 126;
    private static final boolean REMOVED = true;

    private double minBrightness ;
    private double maxBrightness ;
    private String typeOfRound;

    public static final String ROUND_UP = "up";
    public static final String ROUND_DOWN = "down";
    public static final String ROUND_ABS = "abs";


    /**
     * The constructor for this class
     */
    public SubImgCharMatcher(char[] charArray){
        this.charSet = new HashSet<Character>();
        buildSet(charArray);
        this.minBrightness = START_MIN;
        this.maxBrightness = START_MAX;
        this.brightnessMap = new HashMap<>();
        this.minMaxBrightness = new TreeSet<>();
        this.normalizedBrightness = new TreeMap<>();
        this.typeOfRound = ROUND_ABS;
        initializeStorageMap();
    }

    /**
     * Given a value of brightness for the sub image, the method will
     * return the char from the set of chars, with the closest rounded brightness
     * Given a few chars with the same brightness this method will return the one
     * with the lowest ASCII value.
     */
    public char getCharByImageBrightness(double brightness){
        return normalizedBrightness.get(round(brightness));
    }


    /**
     * This method adds a char to our set
     */
    public void addChar(char c){
        charSet.add(c);
        // updateMinMax(c,!REMOVED);
    }

    /**
     * This method removes a char to our set
     */
    public void removeChar(char c){
        // updateMinMax(c,REMOVED);
        charSet.remove(c);
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
        int defaultPixelNumber = CharConverter.DEFAULT_PIXEL_RESOLUTION;
        boolean[][] charArr = CharConverter.convertToBoolArray(c);
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
        this.typeOfRound = typeOfRound;
    }


    /**
     * Returns reference to our set of chars
     */
    public Set<Character> getCharSet() {
        return charSet;
    }


    // /**
    //  * Min max brightness update
    //  */
    // private void updateMinMax(char c,boolean remove){
    //     double brightness = brightnessMap.get(c);
    //     if(!remove){
    //         minMaxBrightness.add(brightnessMap.get(c));
    //     }
    //     else{
    //         minMaxBrightness.remove(brightness);
    //     }
    //     if(brightness< minBrightness || brightness> maxBrightness){
    //         this.minBrightness = minMaxBrightness.first();
    //         this.maxBrightness = minMaxBrightness.last();
    //     }
    // }

    private void updateMinMax(){
        this.minBrightness = 255;
        this.maxBrightness = 0;
        for(char c: charSet){
            if (brightnessMap.get(c)< minBrightness){
                this.minBrightness = brightnessMap.get(c);
            }
            if (brightnessMap.get(c)> maxBrightness){
                this.maxBrightness = brightnessMap.get(c);
            }
        }
    }

    /**
     * Linear normalization of the brightness of the chars
     */
    public void normalizeBrightness(){
        double newBrightness;
        normalizedBrightness.clear();
        updateMinMax();
        for(char c : charSet){ // sa nu treci peste new
            newBrightness = calculateLinearNormalization(c);
            if(normalizedBrightness.containsKey(newBrightness)){
                if(normalizedBrightness.get(newBrightness)>c){
                    normalizedBrightness.put(newBrightness, c);
                }
            }
            else {
                normalizedBrightness.put(newBrightness, c);
            }
        }
    }

    /**
     * Returns the result of applying the newCharBrightness formula on our
     * initial values
     * @param c the char we want to calculate the new brightness for
     * @return the new brightness
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
     * @param brightness the brightness we want to round
     * @return the rounded brightness
     */
    private double round(Double brightness) {
        if (normalizedBrightness.containsKey(brightness)){
            return brightness;
        }
        double roundedBrightness;
        double upperEstimation =  normalizedBrightness.headMap(brightness).lastKey();
        double lowerEstimation = normalizedBrightness.tailMap(brightness).firstKey();
        switch(this.typeOfRound){
            case ROUND_UP:
                roundedBrightness = upperEstimation;
                break;
            case ROUND_DOWN:
                roundedBrightness= lowerEstimation;
                break;
            default:
                double upperDelta = Math.abs(brightness- upperEstimation);
                double lowerDelta = Math.abs(brightness-lowerEstimation);
                roundedBrightness = (upperDelta<lowerDelta)? upperEstimation:lowerEstimation;
                break;
        }
        return roundedBrightness;
    }

}