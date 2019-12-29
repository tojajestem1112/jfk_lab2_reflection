package Jfk_lab2;

import java.io.IOException;
import java.sql.SQLOutput;

public class Main
{
    public static void main(String [] args) {
        try {
            ParameterReader parReader = new ParameterReader(args);
            //Check up parameters
            //System.out.println(parReader);

            JarExplorer explorer = new JarExplorer(parReader.getJarPath());
            if (parReader.isListOfClassesParameter())
                explorer.showClassNames();
            if (parReader.isListOfPackagesParameter())
                explorer.showPackageNames();
            if (parReader.getListOfClassForListMethods().size() > 0) {
                explorer.showMethodsOfClasses(parReader.getListOfClassForListMethods());
            }
            if (parReader.getListOfClassForListCtors().size() > 0) {
                explorer.showCtorsOfClasses(parReader.getListOfClassForListCtors());
            }
            if (parReader.getListOfClassForListFields().size() > 0) {
                explorer.showFieldsOfClasses(parReader.getListOfClassForListFields());
            }
            //output path "C:\Users\Dawid\Desktop\test_making_jars\singlePackage\target\maven-status"

            //EDITION AND WRITING NEW JAR FILE
            if(parReader.getOutputPath()!= null)
            {
                ScriptExecutor scriptExe = new ScriptExecutor(parReader.getJarPath(),parReader.getOutputPath(), explorer.getClassNames(), explorer.getPackagesNames());
                JarCreator jarCreator = new JarCreator(scriptExe.getCtClasses(), scriptExe.getPackagesNames(), explorer.getManifest());
                jarCreator.createJar(parReader.getOutputPath());
            }
            else
            {
                System.out.println("Output file hasn`t been generated: missing --o parameter");
            }


        } catch (ErrorException e) {
            System.out.println(e);

        }
    }
}
