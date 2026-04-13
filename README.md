# Space Trash
A videogame where meteors fall from the sky, and the player must deflect them to avoid destruction. The player can interact with meteors, and the game features randomized meteor types, and speeds.

# To run
* Install maven from [here](https://maven.apache.org/download.cgi) or via your preferred package manager
* Install python3.10 from [here](https://www.python.org/downloads/release/python-3100/) or via your prefered package manager
* Setup and enable the Python virtual environment under .venv, install the packages from requirements.txt under python3.10
* run 'mvn clean javafx:run'

# Features added
* Meteors fall from the top of the screen at random speeds and positions.
* Player can deflect meteors using a button.
* Meteors are represented by different shapes (circle, rectangle, polygon).
* Meteors spawn at regular intervals.
* Added gameover and start screen

# To-Do List
* Figure out how to track player movement (dots every 10 secs?)(Don't blow up laptop lol).
* Speed up shapes based on time passed.
* Clean up sizing of everything :/.
* Finish objective ex. "Don't hit triangles" 
* Sound Effects: Add sounds for meteor deflections, explosions, and other game events.
* Polish: Make end screen better (wait till last meteors off screen), and add better graphics (stars?)