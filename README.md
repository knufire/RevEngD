# Reverse-Engineered Design (RevEngD)

[![build status](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/badges/master/build.svg)](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/commits/master)
[![coverage report](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/badges/master/coverage.svg)](https://ada.csse.rose-hulman.edu/teamOdyssey/RevEngD/commits/master)

This is a repository for our **Software Design - CSSE 374** term project. This application reverse engineers the design of a supplied codebase using [SOOT](https://github.com/Sable/soot), a bytecode instrumentation framework. You will need to learn a few things about SOOT to do a good job in the project. Use these references whenever you get stuck:
1. [SOOT Survival Guide PDF](http://www.brics.dk/SootGuide/sootsurvivorsguide.pdf)
2. [Fundamental Soot Objects](https://github.com/Sable/soot/wiki/Fundamental-Soot-objects)
3. [Other Online Help](https://github.com/Sable/soot/wiki/Getting-help)

## About Seed Contents
The repo, as is, contains four examples for you to try out and expand. Lets' take a quick tour of the repo contents:

1. [SceneBuilder API](/src/main/java/csse374/revengd/soot/SceneBuilder.java) - Helps with setting up SOOT for a whole program analysis.


## Cloning the Repo
You can clone the repo locally using Git Bash/Shell as follows:
```bash
cd <to the your workspace folder>
git clone git@ada.csse.rose-hulman.edu:teamOdyssey/RevEngD.git
```

## Using IDE
You can import the cloned folder as a **Gradle Project** in Eclipse or IntelliJ IDEs.

## Other Notes
The project is configured to use Log4J, which you can also use for your own classes. See [SceneBuilder API](/src/main/java/csse374/revengd/soot/SceneBuilder.java) for an example. 
