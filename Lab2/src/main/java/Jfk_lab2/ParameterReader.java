package Jfk_lab2;

import java.util.LinkedList;

public class ParameterReader
{
    private boolean listOfPackagesParameter = false;
    private boolean listOfClassesParameter = false;

    private LinkedList<String> listOfClassForListMethods  = new LinkedList<>();
    private LinkedList<String> listOfClassForListFields = new LinkedList<>();
    private LinkedList<String> listOfClassForListCtors = new LinkedList<>();

    private String scriptPath = null;
    private String jarPath = null;
    private String outputPath = null;


    public ParameterReader(String [] args) throws ErrorException {
        for(int i=0; i< args.length; i++)
        {
            if(args[i].equals("--list-packages"))
            {
                if(listOfPackagesParameter == true)
                    System.out.println("[WARN] duplicate of --list-package parameter!");
                listOfPackagesParameter = true;
            }
            else if(args[i].equals("--list-classes"))
            {
                if(listOfClassesParameter == true)
                    System.out.println("[WARN] duplicate of --list-classes");
                listOfClassesParameter = true;
            }
            else if(i == args.length-1)//two-party parameters
            {
                //We need 2 args, not one
                throw new ErrorException("Invalid number of Patameters", 1);
            }
            else if(args[i].equals("--list-methods"))
            {
                listOfClassForListMethods.add(args[i+1]);
                i+= 1;
            }
            else if(args[i].equals("--list-fields"))
            {
                listOfClassForListFields.add(args[i+1]);
                i+=1;
            }
            else if(args[i].equals("--list-ctors"))
            {
                listOfClassForListCtors.add(args[i+1]);
                i+=1;
            }
            else if(args[i].equals("--script"))
            {
                scriptPath = args[i+1];
                i+=1;
            }
            else if(args[i].equals("--i"))
            {
                jarPath = args[i+1];
                i+=1;
            }
            else if(args[i].equals("--o"))
            {
                outputPath = args[i+1];
                i+=1;
            }
            else
            {
                throw new ErrorException("Invalid parameter detected", 2);
            }
        }

        checkParameters();
    }
    private void checkParameters() throws ErrorException {
        if(jarPath == null)
        {
            throw new ErrorException("JarPath not found!", 3);
        }
        if(scriptPath!= null && outputPath==null)
        {
            System.out.println("[WARN] Output file is not detected, there is no sense to execute script");
        }
        if(scriptPath == null && outputPath!=null)
        {
            System.out.println("[WARN] You are copying jar file");
        }
    }

    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Analiza wejścia:\n");
        stringBuilder.append("Plik: "+jarPath+"\n");
        if(listOfPackagesParameter) stringBuilder.append("\tlista pakietów\n");
        if(listOfClassesParameter) stringBuilder.append("\tlista klas\n");
        if(listOfClassForListMethods.size()>0)
        {
            stringBuilder.append("\tLista metod klas:\n");
            for(int i=0; i< listOfClassForListMethods.size(); i++)
            {
                stringBuilder.append("\t\t"+listOfClassForListMethods.get(i)+"\n");
            }
        }
        if(listOfClassForListFields.size()>0)
        {
            stringBuilder.append("\tLista pól klas:\n");
            for(int i=0; i< listOfClassForListFields.size(); i++)
            {
                stringBuilder.append("\t\t"+listOfClassForListFields.get(i)+"\n");
            }
        }
        if(listOfClassForListCtors.size()>0)
        {
            stringBuilder.append("\tLista konstruktorow klas:\n");
            for(int i=0; i< listOfClassForListCtors.size(); i++)
            {
                stringBuilder.append("\t\t"+listOfClassForListCtors.get(i)+"\n");
            }
        }
        if(scriptPath!= null)
        {
            stringBuilder.append("Modyfikacja przy użyciu skryptu "+scriptPath+"\n");
        }
        if(outputPath!=null)
        {
            stringBuilder.append("Plik Wyjściowy: "+outputPath);
        }
        return stringBuilder.toString();
    }

    public String getJarPath() {
        return jarPath;
    }
    public boolean isListOfPackagesParameter()
    {
        return listOfPackagesParameter;
    }
    public boolean isListOfClassesParameter()

    {
        return listOfClassesParameter;
    }
    public LinkedList<String> getListOfClassForListMethods()
    {
        return listOfClassForListMethods;
    }
    public LinkedList<String> getListOfClassForListFields()
    {
        return listOfClassForListFields;
    }
    public LinkedList<String> getListOfClassForListCtors()
    {
        return listOfClassForListCtors;
    }
    public String getScriptPath(){ return scriptPath;}
    public String getOutputPath(){return outputPath;}
}
