teodoraf,dori.plg
323291591,207685306

Questions:
1. We went with the classes asked of us, adding two nested exception classes.
1.1. Shell is the user interface, it has an instance of AsciiArtAlgorithm, and is tasked with parsing the user input (using the supplied KeyboardInput), displaying errors and calling the apppropriate command in AsciiArtAlgorithm.
1.2. AsciiArtAlgorithm is the main class of the program, it has instances of Image and SubImgCharMatcher and holds the parameters of the desired ascii art. It also keeps track of whether the subimages or the character set has been changed, to save runtime (minimize double computing).
It also has two nested classes for throwing exceptions unique to the domain.
1.3. Image handles buffering the image, breaking up it into subimages in the desired resoluion and returning an array of brightnesses fit to be parsed into ascii art
1.4. SubImgCharMatcher is tasked with keeping track of the chars to be used, their brightnesses and handles matching them according to the planned rounding method.

2. For each collection used, explain why you chose it explicitly. Consider runtime and memory

3. We wrote a function that takes the attempted action and the error messsage delivered by the exception, and parses them into a user-readable message. We also wrote two additional exception classes to handle exceptions otherwise unique to the program. Additional exceptions were trickled up and caught at Shell.runCommand(), except for Images IOException, trickled through asciiArtAlgorithm to Shell.main()

4. If you changed SubImageCharMatcher's API explain why it was needed

5. We did not change the supplied classes.
