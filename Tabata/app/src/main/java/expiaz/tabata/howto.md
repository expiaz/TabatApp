add module dependency
http://www.truiton.com/2015/02/android-studio-add-library-project/
https://stackoverflow.com/questions/25610727/adding-external-library-in-android-studio

Goto File -> new -> Import Module.
Source Directory -> Browse the project path.
Specify the Module Name
// Open build.gradle (Module:app) file
// Add the following line with your module name : compile project(':module_name')