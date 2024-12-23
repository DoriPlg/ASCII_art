teodoraf,dori.plg
323291591,207685306

Changes in API:
Class SubImgCharMatcher extension of API
 public Set<Character> getCharSet()
 we return a reference to the current set of chars in orderto check whether a change has occured in the 
 chars we use to represent the image 
 public void setTypeOfRound(String setTypeOfRound)
 since the constructor of this class does not receive the type of round calculation we need to use to
 estimate the closest brightness to the one we look for we have given AsciiArtAlgorithm the possibility
 to send to this class the user's requirements
 public void setTypeOfRound(String typeOfRound) 
 this method allows SubImgCharMatcher to estimate the appropiate brightness acoording to the rounding 
 method chosen by user
 public final static int LOWER_ASCII
 we used this one to define the upper limit in adding all the chars according to the users request
 public final static int UPPER_ASCII
  we used this one to define the upper limit in adding all the chars according to the users request


Questions:
1. We went with the classes asked of us, adding two nested exception classes.
1.1. Shell is the user interface, it has an instance of SubImgCharMatcher, Image, AsciiOutput. It handles
exceptions raised by the methods and constructs the AsciiArtAlgorithm object with the correct parameters.
It also has a nested class for throwing exceptions unique to the domain.
1.2. AsciiArtAlgorithm is the main class of the program, it has instances of Image and SubImgCharMatcher
and holds the parameters of the desired ascii art. It also keeps track of whether the resolution and image
have been changed, to save runtime (minimize double computing).
It also has two nested classes, one for tracking resolution changes, and another for throwing exceptions
unique to the domain.
1.3. Image handles getting and accessing the image dimensions and pixels.
1.4. SubImgCharMatcher is tasked with keeping track of the chars to be used, their brightnesses and handles
matching them according to the planned rounding method.
It also keeps track of changing to avoid renormalizing the brightnesses more than is neccessary.
1.5 PrepareImage handles the whole preperation of an image from the original image recieved to the desired
brightness array.

2. In the class SubImgCharMatcher:
- we used a hash set to store the chars that we use for ascii art as we can add 
chars freely without the need to check whether we're trying to add a char that is already stored.
Also removing and adding a char takes O(1) on average We used a hash set also for storing added
or removed chars
- we used HashMap<Character,Double> to store the brightness for each char before the normalization.
We prefered a HashMap since adding,removing and searching an element take O(1) on average.
We can also easily retrieve the brightness for each char this way.
- we used SortedMap<Double,Character> to store the value of the normalized brightness for each
char in our set of chars.We prefered SortedMap because we have easy access to the closest values
to the one we want to estimate according to the chosen type of round.

3. We wrote a function that takes the attempted action and the error messsage delivered by the
exception, and parses them into a user-readable message. We also wrote two additional exception
classes to handle exceptions otherwise unique to the program. Additional exceptions were trickled
up and caught at Shell.runCommand(), except for IllegalArgumentException resulting from a bad
default char array, trickled to Shell.main()

4. In order to strengthen encapsulation, we provided LOWER_ASCII and UPPER_ASCII as public, so they will 
be accessible from Shell, in order to be able to parse an "all" command, and know what range is needed.
 We also needed to send the current char set in order to print all of the chars that are of use and in order
 to throw an exception in the case in which the char set is too small.(getCharSet)
 We also needed a way to find out what is the required method of estimation using round in order to 
 return the closest brightness in the method getImageBrightness.(setTypeOfRound).

5. We made no changes.
