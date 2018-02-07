# Reverse-Engineered Design (RevEngD)

[![build status](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/badges/master/build.svg)](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/commits/master)
[![coverage report](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/badges/master/coverage.svg)](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/commits/master)

This is a repository for our **Software Design - CSSE 374** term project. This application reverse engineers the design of a supplied codebase using [SOOT](https://github.com/Sable/soot), a bytecode instrumentation framework. Use these references for more information about the SOOT library:
1. [SOOT Survival Guide PDF](http://www.brics.dk/SootGuide/sootsurvivorsguide.pdf)
2. [Fundamental Soot Objects](https://github.com/Sable/soot/wiki/Fundamental-Soot-objects)
3. [Other Online Help](https://github.com/Sable/soot/wiki/Getting-help)

# Running the Project
The project can either be run using these command line arguments and/or a properties file. The command line arguments will overwrite any properties file setting.

## Required Flags
  -d : The path to the directory to be analyzed. EX: "C:/Users/User/Projects/MyProject"  
  -m : The fully qualified name of a class that contains the main method for the project. EX: odyssey.app.Runner  
  -c : A space separated list of fully qualified class names to be displayed.  EX: odyssey.app.Configuration odysse.app.Runner
## Optional Flags
  -config : The path to the properties file to use. 
  -a : The most secure level of access to look at. Defaults to private.  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;In order of most restricting, the options are "public", "protected", "package", "private"  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Any option in the above list will display itself and anything to its left   
  -i : The path to output the SVG representation of the UML. EX: "C:/Users/User/Pictures/MyFavoriteUML.svg" 
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Default directory is "./build/plantuml/diagram.svg"    
  --include-ancestors : The presence of this flag tells the application to output all interfaces and supertypes it finds    
  --include-Object : The presence of this flag tells the application to include java.lang.Object in the output    
  -e : The fully qualified starting method to draw a sequence diagram of. EX: "<odyssey.app.Runner: void main(java.lang.String[])>"  
  -s : The path to output the SVG representation of the Sequence Diagram. EX: "C:/Users/User/Pictures/MySeqDiagram.svg"  
  -max-depth : A number representing the maximum call depth the sequence diagram should output, defaults to 5. EX: 20  
  --expand-jdk : The presence of this flag tells the application to step inside of method calls to JDK objects and output them in the sequence diagram.   
  --include-super : The presence of this flag will cause the application to output super calls in the sequence diagram   
## Properties File
The properties file consists of single flag and its arguments separated by spaces on each line.
  -mrs : The MethodResolver to use for resolving interface calls. EX: odyssey.methodresolution.ChainMethodResolver
  -mra : The Algorithm(s) to use for resolving interface calls. EX: odyssey.methodresolution.CallGraphAlgorithm odyssey.methodresolution.HierachyAlgorithm  
  --exclude-lambda : When set to true, will display functional constructs, such as lambda methods  
  -analyzers : A list of fully qualified analyzers to be added to the execution pipeline. EX: odyssey.analyzers.SingletonAnalyzer  
  -filters : A list of fully qualified filters to be added to user analyzers created by the -analyzers flag. EX odyssey.filters.DollarSignFilter  
  -messageRenderers : A list of fully qualified message renderers to be added to the Seqeunce Diagram Rendering process. EX: odyssey.renderers.MessageRenderer  
  -classRenderers : A list of fully qualified class renderers to be added to the Seqeunce Diagram Rendering process. EX: odyssey.renderers.SingletonClassRenderer  
  -relationshipRenderers : A list of fully qualified relationship renderers to be added to the Seqeunce Diagram Rendering process. EX: odyssey.renderers.SingletonRelationshipRenderer  


## Example of Running
One way to run the project would be to build the project using gradle and run the RevEngD.bat file located in
"build/distributions/RevEngD-SNAPSHOT.zip/bin" using the command line and passing the above arguments in. An example command to run the program would be  
```
./RevEngD.bat -d "C:/Projects/coolProject" -m app.main -c app.main app.otherClass
 ```
 The other way would be to modify the build.gradle file to pass the arguments in via the args array. Then the application can be run using the gradle run task.  
 An example 
 ```
 run {
     args = ['-d', "C:/Projects/MyCoolProject/CoolProject" , '-m', 'app.AwesomeMainClass', '-c', 'app.CoolClass1', 'app.BestClassEver']
 }
```

# Who Drove When
-----M1-----  
Hand Drawn UML Creation -- Collin    
Relation/Relationship -- Collin     
Filters -- Collin   
Analyzers -- Mostly Rahul/Collin/Christopher  
Configuration -- Rahul/Collin/Christopher    
UML Parser -- Rahul   
Runner/Startup Logic -- Christopher     
Processor -- Christopher     
README.MD -- Christopher  
-----M2-----  
AssociationAnalyzer -- Collin/Rahul  
DependencyAnalyzer -- Christopher/Collin/Rahul  
SequenceAnalyzer -- Collin/Rahul  
Message Hirearchy -- Christopher  
README.MD -- Christopher 
-----M3-----
README.MD -- Christopher
Algorithms -- Collin/Rahul
Patterns -- Christopher/Colling
Refactoring -- Rahul/Collin/Christopher
-----M4-----

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

