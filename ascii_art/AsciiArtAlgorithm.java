package ascii_art;

import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.util.Set;

class AsciiArtAlgorithm {

    static ImageSnapshot imgSnap = null;
    static CharSetSnapshot charSetSnap = null;


    private final Image image;
    private final int resolution;
    private final Set<Character> characterSet;
    private final String rounding;

    /**
     * Constructor for the AsciiArtAlgorithm class. Initializes the image, resolution, charMatcher, and outputMethod.
     * Sets the change flags to true.
     *
     * @param img            is the image to be split
     * @param resolution     the resolution to split the image into
     * @param roundingMethod the desired rounding method by the user
     */
    public AsciiArtAlgorithm(Image img, int resolution, Set<Character> characterSet, String roundingMethod){
        this.image = img;
        this.resolution = resolution;
        this.characterSet = characterSet;
        this.rounding = roundingMethod;
    }

    /**
     * The main method of the class, with the current settings, generates the ascii art and returns it.
     * Takes care to only recalculate the brightness if the image has changed, and to only normalize the character set if it has changed.
     * @return char[][] defining the art.
     * @throws TooSmallSetException if the character set is too small.
     */
    public char[][] run() throws TooSmallSetException{
        if (characterSet.size() < 2){
            throw new TooSmallSetException();
        }
        SubImgCharMatcher charMatcher = getCharMatcher(characterSet);
        double[][] brightness = getBrightnessMatrix();
        charMatcher.setTypeOfRound(rounding);

        
        char[][] asciiArt = new char[brightness.length][brightness[0].length];
        for (int i = 0; i < brightness.length; i++) {
            for (int j = 0; j < brightness[0].length; j++) {
                asciiArt[i][j] = charMatcher.getCharByImageBrightness(brightness[i][j]);
            }
        }
        return asciiArt;
    }

    private double[][] getBrightnessMatrix(){
        if (imgSnap == null || !(image == imgSnap.image() && resolution == imgSnap.resolution())){
            System.out.println("Calculating brightness");
            imgSnap = new ImageSnapshot(image,resolution,image.getImageBrightness(resolution));
        }
        return imgSnap.brightness();
    }

    private SubImgCharMatcher getCharMatcher(Set<Character> characterSet){

        if (charSetSnap == null || !charSetSnap.sameSet(characterSet)){
            System.out.println("Normalizing chars");
            char[] chars = new char[characterSet.size()];
            char i =0;
            for ( char c : characterSet){
                chars[i++] = c;
            }
            charSetSnap = new CharSetSnapshot(new SubImgCharMatcher(chars));
        }
        return charSetSnap.charMatcher();

    }

    /**
     * Exception for when the character set is too small.
     */
    static class TooSmallSetException extends Exception {
        public TooSmallSetException() {
            super("Charset is too small.");
        }
    }

    private record ImageSnapshot(Image image, int resolution, double[][] brightness) {}

    private record CharSetSnapshot(SubImgCharMatcher charMatcher) {
        boolean sameSet(Set<Character> chars){
            for(char c: chars){
                if (!this.charMatcher().getCharSet().contains(c)) return false;
            }
            return chars.size() == this.charMatcher.getCharSet().size();
        }
    }
}
