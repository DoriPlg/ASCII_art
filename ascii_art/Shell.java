package ascii_art;

import java.io.IOException;

import ascii_art.AsciiArtAlgorithm.BadResolutionException;
import ascii_art.AsciiArtAlgorithm.TooSmallSetException;

public class Shell{
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
    
    
    private AsciiArtAlgorithm asciiArtAlgorithm;

    /**
     * Constructor for the Shell class.
     * @param imageName the path to the image file.
     * @throws IOException if the file cannot be opened.
     */
    public Shell(String imageName) throws IOException {
        this.asciiArtAlgorithm = new AsciiArtAlgorithm(imageName);
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
    private int parseResolution(String commandString) throws IllegalArgumentException, BadResolutionException {
        if (commandString.equals(UP)){
            return asciiArtAlgorithm.changeResolution(true);
        }
        else if (commandString.equals(DOWN)){
            return asciiArtAlgorithm.changeResolution(false);
        }
        else {
            throw new IllegalArgumentException("incorrect direction.");
        }
    }

    /**
     * Makes a char array from a string, in the format of the shell.
     * @param charString the string to be converted.
     * @return the char array.
     * @throws IllegalArgumentException if the string is not formatted correctly.
     */
    private char[] makeCharArray(String charString) throws IllegalArgumentException {
        if (charString.equals(ALL_CHARS)){
            char[] charList = new char[126-32+1];
            for (char i = 0; i < charList.length; i++) { charList[i] = (char)(32+i);}
            return charList;
        }
        else if (charString.equals(SPACE_KEY)){ return new char[]{' '};}
        else if (charString.length() == 1){ return new char[]{charString.charAt(0)};}
        else if(charString.length() == 3 && charString.charAt(1) == '-'){
            char start = charString.charAt(0);
            char end = charString.charAt(2);
            char[] charList = new char[Math.abs(start-end)+1];
            if (start > end){
                for (char i = end; i <= charList.length; i++) { charList[i] = (char)(end+i); }
            }
            else {
                for (char i = start; i <= charList.length; i++) { charList[i] = (char)(start+i);}
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
    private void parseAdd(String commandString) throws IllegalArgumentException {
        asciiArtAlgorithm.addChars(makeCharArray(commandString));
    }

    /**
     * Parses the remove command.
     * @param commandString the command string given by the user.
     * @throws IllegalArgumentException if the command is not formatted correctly.
     */
    private void parseRemove(String commandString) throws IllegalArgumentException {
        asciiArtAlgorithm.removeChars(makeCharArray(commandString));
    }

    /**
     * Prints the characters in the char list.
     */
    private void printChars(){
        for (char c : asciiArtAlgorithm.getCharList()) {
            System.out.print(c+" ");
        }
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
    private void parseRoundingMethod(String commandString) throws IllegalArgumentException {
        if (commandString.equals(UP)){
            asciiArtAlgorithm.changeRoundingMethod(1);
        }
        else if (commandString.equals(DOWN)){
            asciiArtAlgorithm.changeRoundingMethod(-1);
        }
        else if (commandString.equals(ABS)){
            asciiArtAlgorithm.changeRoundingMethod(0);
        }
        else {
            throw new IllegalArgumentException(INCORRECT_FORMAT);
        }
    }

    /**
     * Parses the output method command.
     * @param commandString the command string given by the user.
     * @throws IllegalArgumentException if the command is not formatted correctly.
     */
    private void parseOutputMethod(String commandString) throws IllegalArgumentException {
        if (commandString.equals("console")){
            asciiArtAlgorithm.consoleOutput();
        }
        else if (commandString.equals("html")){
            asciiArtAlgorithm.htmlOutput(HTML_OUTPUT_FILE,HTML_FONT);
        }
        else{
            throw new IllegalArgumentException(INCORRECT_FORMAT);
        }
    }

    /**
     * Runs the shell, prompting the user for input and executing commands.
     * The shell will run until the user types "exit".
     * The shell will print an error message if the command is not recognized, 
     * or if the command cannot be executed.
     */
    public void run(){
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
        if (commands[0].equals(CHARS)){ printChars(); }
        else if (commands[0].equals(ADD)){ try { parseAdd(commands[1]); }
            catch (IllegalArgumentException e){ 
                System.out.println(errWriter(commands[0],e.getMessage())); }}
        else if (commands[0].equals(REMOVE)){ try { parseRemove(commands[1]); }
            catch (IllegalArgumentException e){
                System.out.println(errWriter(commands[0],e.getMessage()));}}
        else if (commands[0].equals(RESOLUTION)){
            try {
                int resolution = parseResolution(commands[1]);
                System.out.println("Resolution set to "+resolution);
            }
            catch (IllegalArgumentException | BadResolutionException e){
                System.out.println(
                    errWriter("change resolution",e.getMessage()));}}
        else if (commands[0].equals(ROUND)){
            try { parseRoundingMethod(commands[1]);}
            catch (IllegalArgumentException e){
                System.out.println(errWriter(CHG_RUND_MTD,e.getMessage()));
            }
        }
        else if (commands[0].equals(OUTPUT)){
            try {
                if (commands.length < 2){
                    throw new IllegalArgumentException(INCORRECT_FORMAT);}
                parseOutputMethod(commands[1]);}
            catch (IllegalArgumentException e){
                System.out.println(errWriter("change output",e.getMessage()));
            }}
        else if (commands[0].equals(RUN)){
            try{ asciiArtAlgorithm.asciiArt();}
            catch (TooSmallSetException e){ 
                System.out.println("Did not execute. "+e.getMessage());}
        }
        else { System.out.println(errWriter("execute","incorrect command."));}  
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
            shell = new Shell(args[0]);
        }
        catch (IOException e){
            System.out.println("Could not open file.");
            return;
        }
        shell.run();
    }
}