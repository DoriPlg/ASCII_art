package image_char_matching;

import java.util.*;

/**
 * This class is going to match an ASCII character to a sub image
 * with a given brightness(it is going to transform sub images into chars)
 */
public class SubImgCharMatcher {


    private final Set<Character> charSet ;
    private final HashMap<Character,Double> brightnessMap ;
    private final Set<Character> addedChars;
    private final Set<Character> removedChars;
    private final SortedMap<Double,Character> normalizedBrightness; //tree set
    private static final int START_MIN = 1;
    private static final int START_MAX = 0;
    private static final double MAX_BRIGHTNESS = 255;
    private static final double MIN_BRIGHTNESS = 0;
    private static final String ASCII_OUT_OF_BOUNDS = "the char is not in the ASCII range";

    public static final int LOWER_ASCII = 32;
    public static final int UPPER_ASCII = 126;

    private double minBrightness ;
    private double maxBrightness ;
    private String typeOfRound;

    public static final String ROUND_UP = "up";
    public static final String ROUND_DOWN = "down";
    public static final String ROUND_ABS = "abs";


    /**
     * The constructor for this class, it initializes the set of chars
     * and normalizes the brightness of the chars
     * @param charArray the array of chars that we want to use
     */
    public SubImgCharMatcher(char[] charArray) throws IllegalArgumentException{
        this.minBrightness = START_MIN;
        this.maxBrightness = START_MAX;
        this.charSet = new HashSet<>();
        this.addedChars = new HashSet<>();
        this.removedChars = new HashSet<>();
        this.brightnessMap = new HashMap<>();
        this.normalizedBrightness = new TreeMap<>();
        this.typeOfRound = ROUND_ABS;
        buildSet(charArray);
        initializeStorageMap();
        normalizeBrightness();
    }

    /**
     * Given a value of brightness for the sub image, the method will
     * return the char from the set of chars, with the closest rounded brightness
     * Given a few chars with the same brightness this method will return the one
     * with the lowest ASCII value.
     * @param brightness the brightness of the sub image
     * @return the char that is the closest to the brightness
     */
    public char getCharByImageBrightness(double brightness){
        if(!(addedChars.isEmpty() && removedChars.isEmpty())){
            normalizeBrightness();
            addedChars.clear();
            removedChars.clear();
        }
        double rounded =  round(brightness);
        return normalizedBrightness.get(rounded);
    }


    /**
     * This method adds a char to our set
     * @param c the char that we want to add
     * @throws IllegalArgumentException if the char is not in the ASCII range [32,126]
     */
    public void addChar(char c) throws IllegalArgumentException{
        if (c<LOWER_ASCII || c>UPPER_ASCII){
            throw new IllegalArgumentException(ASCII_OUT_OF_BOUNDS);
        }
        if(charSet.add(c)){
            if(!removedChars.remove(c)){
                addedChars.add(c);
            }
        }
    }


    /**
     * This method removes a char to our set
     * @param c the char that we want to remove
     * @throws IllegalArgumentException if the char is not in the ASCII range [32,126]
     */
    public void removeChar(char c) throws IllegalArgumentException{
        if (c<LOWER_ASCII || c>UPPER_ASCII){
            throw new IllegalArgumentException(ASCII_OUT_OF_BOUNDS);
        }
        if(charSet.remove(c)){
            if(!addedChars.remove(c)){
                removedChars.add(c);
            }
        }
    }


    /**
     * Returns reference to the set of chars
     * @return the set of chars
     */
    public Set<Character> getCharSet() {
        return charSet;
    }

    /**
     * Allows ASCII_art to define the way of rounding the brightness for each char
     * @param typeOfRound the type of rounding that we want to use, it can be "up", "down" or "abs"
     */
    public void setTypeOfRound(String typeOfRound){ // default abs
        this.typeOfRound = typeOfRound;
    }


    /**
     * Method that builds our char set from the array of chars that we get in the constructor
     * @param charArray the array of chars that we want to use
     * @throws IllegalArgumentException if the char is not in the ASCII range [32,126]
     */
    private void buildSet(char[] charArray) throws IllegalArgumentException{
        if(charArray!=null){
            for (char c : charArray) {
                addChar(c);
            }
        }
    }

    /**
     * Finds char brightness before linear normalization
     * @param c the char that we want to convert
     */
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
    }


    /**
     * Min max brightness update, called before linear normalization
     */
    private void updateMinMax(){
        this.minBrightness = MAX_BRIGHTNESS;
        this.maxBrightness = MIN_BRIGHTNESS;
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
     * Linear normalization of the brightness of the chars such that if there are
     * two chars with the same brightness we only enter the char which has
     * a lower value accroding to the ASCII table
     */
    private void normalizeBrightness(){
        updateMinMax();
        normalizedBrightness.clear();
        double newBrightness;
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
     * @param c the char that we want to convert
     * @return the brightness of the char
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
        for(char i = LOWER_ASCII;i<UPPER_ASCII+1;i++){
            convertChar(i);
        }
    }


    /**
     * Rounds the brightness we look for by the chosen method
     * @param brightness the brightness that we want to round
     * @return the rounded brightness
     */
    private double round(Double brightness) {
        double roundedBrightness;
        double upperEstimation ;
        if (normalizedBrightness.containsKey(brightness)){
            return brightness;
        }
        // tailMap returns a value larger or equal to the key so there is no problem in the maximum case
        double lowerEstimation = normalizedBrightness.tailMap(brightness).firstKey();
        if(!normalizedBrightness.headMap(brightness).isEmpty()){
            upperEstimation =  normalizedBrightness.headMap(brightness).lastKey();
        }
        else {
            // it means the value we want to round is lower than the minimum or it is the minimum
            upperEstimation = normalizedBrightness.firstKey();
        }
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