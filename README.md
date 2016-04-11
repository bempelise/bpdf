# README

Author: Evangelos Bempelis

## INSTALATION

To install and use bpdf, the following software needs to be installed:

* Java SDK
Download Java SDK and unzip in a directory. Set the an environmental 
variable `$JAVA_HOME` indicating the installation directory 
(e.g. `home/user/programs/jdk1.8.0_20`). Add `$JAVA_HOME/bin` to your 
`$PATH`. Test if everything works by trying `java -version` in a 
terminal.

* Groovy
Download Groovy and unzip in a directory. Set the an environmental 
variable `$GROOVY_HOME` indicating the installation directory 
(e.g. `home/user/programs/groovy-2.4.0`). Add `$GROOVY_HOME/bin` to 
your `$PATH`. Test if everything works by trying `groovy -version` in 
a terminal.

* Gradle
Download Gradle and unzip in a directory. Set the an environmental 
variable `$GRADLE_HOME` indicating the installation directory 
(e.g. `home/user/programs/groovy-2.4.0`). Add `$GRADLE_HOME/bin` to 
your `$PATH`. Test if everything works by trying `gradle -version` in 
a terminal.

Java is needed for compilation of the project. Groovy is a Java-based 
scripting language that is used to parse the DSL used to express
BPDF graphs. Finally, Gradle is a modern build system used to 
facilitate the compilation of a project spanning in many files with 
multiple dependencies.

## COMPILATION AND EXECUTION

Once everything is set up, open a terminal on the root folder of the 
BPDF project (i.e., the folder with the build.gradle file).

Use `gradle` to compile the project and `gradle run` to compile and 
run the project. 

## PROJECT ORGANIZATION

* Main folder:  
** Files
`README.txt` and `LICENSE.txt`: This file and the software license.

`*bpdf`: These files are examples of bpdf graphs. The `.bpdf` 
extension is optional, just to make the files stand out. 

`build.gradle`: is the gradle makefile.

`build.xml`: is the ant makefile but the latter is obsolete and will 
NOT compile the project properly. The ant makefile needs to be 
updated to use the latest libraries added in the project.

`bpdf.todo`: todo list of the project.

`bpdf.sublime-project`: Concerns only users of sublime text.

`bpdf.sublime-workspace`: Concerns only users of sublime text. Is 
generated once the project is opened in sublimed. *Do no include in 
version control*, as it changes frequently.

** Folders

`src`: contains all the source code.

`docs`: contains documentation

`build`: Should not be there when the code is cloned but once the 
project is compiled it is generated and it contains the bytecode/
binaries. *Do not include in version control*, as it changes 
frequently.