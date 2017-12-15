# Reverse-Engineered Design (RevEngD)

[![build status](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/badges/master/build.svg)](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/commits/master)
[![coverage report](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/badges/master/coverage.svg)](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/commits/master)

This is a repository for our **Software Design - CSSE 374** term project. This application reverse engineers the design of a supplied codebase using [SOOT](https://github.com/Sable/soot), a bytecode instrumentation framework. Use these references for more information about the SOOT library:
1. [SOOT Survival Guide PDF](http://www.brics.dk/SootGuide/sootsurvivorsguide.pdf)
2. [Fundamental Soot Objects](https://github.com/Sable/soot/wiki/Fundamental-Soot-objects)
3. [Other Online Help](https://github.com/Sable/soot/wiki/Getting-help)

# Running the Project
## Required Flags
  -d : The path to the directory to be analyzed. EX: "C:/Users/User/Projects/MyProject"  
  -m : The fully qualified name of a class that contains the main method for the project. EX: odyssey.app.Runner  
  -c : A space separated list of fully qualified class names to be displayed.  EX: odyssey.app.Configuration odysse.app.Runner
## Optional Flags
  -a : The most secure level of access to look at. Defaults to private.  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;In order of most restricting, the options are "public", "protected", "package", "private"  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Any option in the above list will display itself and anything to its left   
  -i : The path to output the SVG representation of the UML. EX: "C:/Users/User/Pictures/MyFavoriteUML.svg"   
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Default directory is "./build/plantuml/diagram.svg"  
  --include-ancestors : The presence of this flag tells the application to output all interfaces and supertypes it finds  
  --include-Object : The presence of this flag tells the application to include java.lang.Object in the output  

# Who Drove When
Hand Drawn UML Creation -- Collin    
Relation/Relationship -- Collin     
Filters -- Collin   
Analyzers -- Mostly Rahul/Collin/Christopher  
Configuration -- Rahul/Collin/Christopher   
UML Parser -- Rahul  
Runner/Startup Logic -- Christopher   
AnalyzerFactory -- Christopher   
Processor -- Christopher   
README.MD -- Christopher

# Cloning the Repo
You can clone the repo locally using Git Bash/Shell as follows:
```bash
cd <to the your workspace folder>
git clone git@ada.csse.rose-hulman.edu:teamOdyssey/RevEngD.git
```

## Using IDE
You can import the cloned folder as a **Gradle Project** in Eclipse or IntelliJ IDEs.

## Other Notes
The project is configured to use Log4J, which can also be used in other classes. 
See [SceneBuilder API](/src/main/java/csse374/revengd/soot/SceneBuilder.java) for an example. 

