package Jfk_lab2;

public class Main
{
    public static void main(String [] args)
    {
        try
        {
            ParameterReader parReader = new ParameterReader(args);
            //Check up parameters
            //System.out.println(parReader);

            JarExplorer explorer = new JarExplorer(parReader.getJarPath());
            if (parReader.isListOfClassesParameter())
                explorer.showClassNames();
            if (parReader.isListOfPackagesParameter())
                explorer.showPackageNames();
            if(parReader.getListOfClassForListMethods().size()>0)
            {
                explorer.showMethodsOfClasses(parReader.getListOfClassForListMethods());
            }
            if(parReader.getListOfClassForListCtors().size()>0)
            {
                explorer.showCtorsOfClasses(parReader.getListOfClassForListCtors());
            }
            if(parReader.getListOfClassForListFields().size()>0)
            {

            }
        }
        catch(ErrorException e)
        {
            System.out.println(e);
        }
    }
}
