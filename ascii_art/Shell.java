package ascii_art;

import java.io.IOException;

import ascii_art.AsciiArtAlgorithm.TooSmallSetException;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;


/**
 * The Shell class is responsible for running the ascii art program.
 * It takes user input and executes commands to generate ascii art.
 */
public class Shell{
    /**
     * Constants for the shell.
     */
    private static final String HTML = "html";
    private static final String CONSOLE = "console";
    private static final String RUN = "asciiArt";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String RESOLUTION = "res";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String EXIT = "exit";
    private static final String CHARS = "chars";
    private static final String ROUND = "round";
    private static final String ABS = "abs";
    private static final String OUTPUT = "output";
    private static final String INCORRECT_FORMAT = "incorrect format.";
    private static final String ALL_CHARS = "all";
    private static final String SPACE_KEY = "space";
    private static final String USER_INPUT_PROMPT = ">>> ";
    private static final String HTML_OUTPUT_FILE = "out.html";
    private static final String HTML_FONT = "Courier New";
    private static final String CHG_RUND_MTD = "change rounding method";
    private static final String CHG_OPUT_MTD = "change output method";
    private static final char[] DEFAULT_CHAR_LIST = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    /**
     * Instance variables for the shell.
     */
    private Image image;
    private int resolution;
    private AsciiOutput outputMethod;
    private final SubImgCharMatcher charMatcher;

    /**
     * Constructor for the Shell class.
     * @throws IOException if the file cannot be opened.
     */
    public Shell() throws IllegalArgumentException {
        this.charMatcher = new SubImgCharMatcher(DEFAULT_CHAR_LIST);
        this.resolution = 2;
        this.outputMethod = new ConsoleAsciiOutput();
        this.image = null;
    }

    /**
     * Writes the standard error message for the shell.
     * @param attempt the attempted action.
     * @param reason the reason the action could not be completed.
     * @return the error message.
     */
    private String errWriter(String attempt, String reason){
        return "Did not "+attempt+" due to "+reason;
    }

    /**
     * parses the resolution command.
     * @param commandString the command string given by the user.
     * @return the new resolution.
     * @throws IllegalArgumentException if the command is not formatted correctly.
     * @throws BadResolutionException if the resolution is out of bounds.
     */
    private int parseResolution(String commandString) throws
                        IllegalArgumentException, BadResolutionException {
        boolean up;
        if (commandString.equals(UP)){
            up = true;
        }
        else if (commandString.equals(DOWN)){
            up = false;
        }
        else {
            throw new IllegalArgumentException("incorrect direction.");
        }
        if (up && resolution * 2 > image.getWidth() ||
                !up && resolution / 2 < Math.max(1,image.getWidth()/image.getHeight())){
            throw new BadResolutionException();
        }
        this.resolution = up ? resolution*2 : resolution/2;
        return resolution;
    }

    /**
     * Makes a char array from a string, in the format of the shell.
     * @param charString the string to be converted.
     * @return the char array.
     * @throws IllegalArgumentException if the string is not formatted correctly.
     */
    private char[] makeCharArray(String charString) throws
                                    IllegalArgumentException {
        if (charString.equals(ALL_CHARS)){
            char[] charList = new char[SubImgCharMatcher.UPPER_ASCII-SubImgCharMatcher.LOWER_ASCII+1];
            for (char i = 0; i < charList.length; i++) {
                charList[i] = (char)(SubImgCharMatcher.LOWER_ASCII+i);}
            return charList;
        }
        else if (charString.equals(SPACE_KEY)){
            return new char[]{' '};
        }
        else if (charString.length() == 1){
            return new char[]{charString.charAt(0)};}
        else if(charString.length() == 3 && charString.charAt(1) == '-'){
            char start = charString.charAt(0);
            char end = charString.charAt(2);
            char[] charList = new char[Math.abs(start-end)+1];
            for (char i = 0; i < charList.length; i++) {
                 charList[i] = (char)((start > end ? end: start) + i);
            }
            return charList;
        }
        else { throw new IllegalArgumentException(INCORRECT_FORMAT); }
    }

    /**
     * Parses the add command.
     * @param commandString the command string given by the user.
     * @throws IllegalArgumentException if the command is not formatted correctly.
     */
    private void parseAdd(String commandString) throws
                                    IllegalArgumentException {
        char[] charList = makeCharArray(commandString);
        for (char c : charList)
        {
            charMatcher.addChar(c);
        }
    }

    /**
     * Parses the remove command.
     * @param commandString the command string given by the user.
     * @throws IllegalArgumentException if the command is not formatted correctly.
     */
    private void parseRemove(String commandString) throws
                                    IllegalArgumentException {
        char[] charList = makeCharArray(commandString);
        for (char c : charList)
        {
            charMatcher.removeChar(c);
        }
    }

    /**
     * Prints the characters in the char list.
     */
    private void printChars(){
        for (char c : charMatcher.getCharSet()){ System.out.print(c+" ");}
        System.out.println();
    }

    /**
     * Prompts the user for input and reads it.
     * @return the user input split by spaces.
     */
    private String[] readInput(){
        System.out.print(USER_INPUT_PROMPT);
        return KeyboardInput.readLine().split(" ");
    }

    /**
     * Parses the rounding method command.
     * @param commandString the command string given by the user.
     * @throws IllegalArgumentException if the command is not formatted correctly.
     */
    private void parseRoundingMethod(String commandString) throws
                                        IllegalArgumentException {
        switch (commandString) {
            case UP -> charMatcher.setTypeOfRound(SubImgCharMatcher.ROUND_UP);
            case DOWN -> charMatcher.setTypeOfRound(SubImgCharMatcher.ROUND_DOWN);
            case ABS -> charMatcher.setTypeOfRound(SubImgCharMatcher.ROUND_ABS);
            default -> throw new IllegalArgumentException(INCORRECT_FORMAT);
        }
    }

    /**
     * Parses the output method command.
     * @param commandString the command string given by the user.
     * @throws IllegalArgumentException if the command is not formatted correctly.
     */
    private void parseOutputMethod(String commandString) throws
                                        IllegalArgumentException {
        if (commandString.equals(CONSOLE)){
            outputMethod = new ConsoleAsciiOutput();
        }
        else if (commandString.equals(HTML)){
            outputMethod = new HtmlAsciiOutput(HTML_OUTPUT_FILE,HTML_FONT);
        }
        else{
            throw new IllegalArgumentException(INCORRECT_FORMAT);
        }
    }

    /**
     * Generates the ascii art.
     * @throws TooSmallSetException if the character set is too small.
     */
    private void generateArt() throws TooSmallSetException {
        AsciiArtAlgorithm asciiArt = new AsciiArtAlgorithm(image,resolution, charMatcher);
        outputMethod.out(asciiArt.run());
    }

    /**
     * Runs the shell, prompting the user for input and executing commands.
     * The shell will run until the user types "exit".
     * The shell will print an error message if the command is not recognized,
     * or if the command cannot be executed.
     * @param imageName the name of the image file.
     */
    public void run(String imageName){
        try { this.image = new Image(imageName); }
        catch (IOException e) {
            System.out.println("Could not open file.");
            return;
        }
        
        String[] commands = readInput();
        while (!(commands[0].equals(EXIT))){
            runCommand(commands);
            commands = readInput();
        }
    }

    /**
     * Runs a single command.
     * @param commands the command to be executed
     */
    private void runCommand(String[] commands){
        switch (commands[0]) {
            case CHARS -> printChars();
            case ADD -> {
                try {
                    parseAdd(commands[1]);
                } 
                catch (IllegalArgumentException e) {
                    System.out.println(errWriter(commands[0], e.getMessage()));
                }
            }
            case REMOVE -> {
                try {
                    parseRemove(commands[1]);
                } 
                catch (IllegalArgumentException e) {
                    System.out.println(errWriter(commands[0], e.getMessage()));
                }
            }
            case RESOLUTION -> {
                try {
                    int resolution = parseResolution(commands[1]);
                    System.out.println("Resolution set to " + resolution + ".");
                } 
                catch (IllegalArgumentException | BadResolutionException e) {
                    System.out.println(errWriter("change resolution", e.getMessage()));
                }
            }
            case ROUND -> {
                try {
                    parseRoundingMethod(commands[1]);
                } 
                catch (IllegalArgumentException e) {
                    System.out.println(errWriter(CHG_RUND_MTD, e.getMessage()));
                }
            }
            case OUTPUT -> {
                try {
                    if (commands.length < 2) {
                        throw new IllegalArgumentException(INCORRECT_FORMAT);
                    }
                    parseOutputMethod(commands[1]);
                } 
                catch (IllegalArgumentException e) {
                    System.out.println(errWriter(CHG_OPUT_MTD, e.getMessage()));
                }
            }
            case RUN -> {
                try { generateArt(); }
                catch (TooSmallSetException e) {
                    System.out.println("Did not execute. " + e.getMessage());
                }
            }
            default -> System.out.println(errWriter("execute", "incorrect command."));
        }
    }

    /**
     * Main method for the shell.
     * @param args the path to the image file.
     */
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("Usage: java Shell <image file>");
            return;
        }
        Shell shell;
        try {
            shell = new Shell();
        }
        catch (IllegalArgumentException e){
            System.out.println("Could not start Shell. "+e.getMessage());
            return;
        }
        // shell.run(args[0]);
        shell.run("images/cat.jpeg");
        shell.run("images/board.jpeg");
    }

    /**
     * Exception for when the resolution is out of bounds.
     */
    private static class BadResolutionException extends Exception {
        public BadResolutionException() {
            super("exceeding boundaries.");
        }
    }

}