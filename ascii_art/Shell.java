class Shell{
    private static final String RUN = "asciiArt";

    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("Usage: java Shell <image file>");
            return;
        }
        AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(args[0]);
        String input = KeyboardInput.readLine();
        while (!input.equals(RUN)){
            // TODO: split the input and call the appropriate method
        }
    }
}