package Jfk_lab2;

public class Main
{
    public static void main(String [] args) {
        try {
            ParameterReader parReader = new ParameterReader(args);
            //Check up parameters
            //System.out.println(parReader);

            JarExplorer explorer = new JarExplorer(parReader.getJarPath());//Explore jarfile
            if (parReader.isListOfClassesParameter())//Should i show list of classes?
                explorer.showClassNames();
            if (parReader.isListOfPackagesParameter())//Should I show list of packages?
                explorer.showPackageNames();
            if (parReader.getListOfClassForListMethods().size() > 0) {//Should I show list of methods?
                explorer.showMethodsOfClasses(parReader.getListOfClassForListMethods());
            }
            if (parReader.getListOfClassForListCtors().size() > 0) {//Should I show list of ctrs for classes?
                explorer.showCtorsOfClasses(parReader.getListOfClassForListCtors());
            }
            if (parReader.getListOfClassForListFields().size() > 0) {//should I show list of fields for classes?
                explorer.showFieldsOfClasses(parReader.getListOfClassForListFields());
            }
            //EXAMPLE output path
            //output path "C:\\Users\\Dawid\\Desktop\\test_making_jars\\singlePackage\\cp97.jar"

            //EDITION AND WRITING NEW JAR FILE
            if(parReader.getOutputPath()!= null)
            {
                //With scriptPath == null executor will not try to execute scrpt

                ScriptExecutor scriptExe = new ScriptExecutor(parReader.getJarPath()
                        , parReader.getScriptPath(), explorer.getClassNames());

                System.out.println("Creating jar file!");
                //Params: Ctclasses from executor, created packages, Manifest, InputStream and jarentry name for other files [like *.jpg]
                JarCreator jarCreator = new JarCreator(scriptExe.getCtClasses(), scriptExe.getPackagesNames()
                        , explorer.getManifest(), explorer.getStreamsForOtherFiles()
                        , explorer.getEntriesNamesForOtherFiles());

                jarCreator.createJar(parReader.getOutputPath());
            }
            else
            {
                //Without --o there is no sense to execute script
                System.out.println("Output file hasn`t been generated: missing --o parameter");
            }


        } catch (ErrorException e) {//Program stops after ErrorException -> it`s user fault
            System.out.println(e);

        }
    }
}
