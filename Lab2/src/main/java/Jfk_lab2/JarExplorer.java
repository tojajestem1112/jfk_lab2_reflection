package Jfk_lab2;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExplorer
{

    private String jarPath;
    private LinkedList<String> classNames = new LinkedList<>();
    private LinkedList<String> packagesNames = new LinkedList<>();
    private ClassLoader classLoader;

    public JarExplorer(String jarPath) throws ErrorException {
        this.jarPath = jarPath;
        Enumeration<JarEntry> enumeration;
        JarFile jarFile;


        try
        {
            jarFile = new JarFile(jarPath+"//");
            enumeration = jarFile.entries();
            URL[] urls = {new URL("jar:file:"+jarPath+"!/")};
            classLoader = URLClassLoader.newInstance(urls);
        } catch (IOException e) {
            throw new ErrorException("Nie znaleziono pliku zrodlowego [.jar]", 3);
        }

        while(enumeration.hasMoreElements())
        {
            JarEntry jarEntry = enumeration.nextElement();
            if(jarEntry.isDirectory() && !jarEntry.getName().contains("-"))
            {
                String clazz = jarEntry.getName();
                clazz = clazz.substring(0,clazz.length()-1)
                                    .replace('/','.');
                packagesNames.add(clazz);

            }
            else if(!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class"))
            {
                String clazz = jarEntry.getName();
                clazz = clazz.substring(0,clazz.length()-6)
                                .replace('/','.');
                classNames.add(clazz);
            }
        }



    }
    public void showClassNames()
    {
        System.out.println("KLASY:\n");
        for(int i =0; i<classNames.size(); i++)
        {
            System.out.println("\t"+classNames.get(i));
        }
    }
    public void showPackageNames()
    {
        System.out.println("PAKIETY:\n");
        for(int i=0; i<packagesNames.size();i++)
        {
            System.out.println("\t"+packagesNames.get(i));
        }
    }
    public void showMethodsOfClasses(LinkedList<String> classes)
    {
        System.out.println("\nLISTA METOD KLAS");
        for(int i=0; i<classes.size();i++)
        {
            System.out.println("\tKlasa "+classes.get(i));

            try {
                Class singleClass = classLoader.loadClass(classes.get(i));
                Method[] methods = singleClass.getDeclaredMethods();
                showFunctions(methods);
            } catch (ClassNotFoundException e) {
                System.out.println("[WARN] Class " + classes.get(i) + "not found. Methods of class can not be showed");
            }
        }
    }
    public void showCtorsOfClasses(LinkedList<String> classes)
    {
        System.out.println("\nLISTA KONSTRUKTORÓW KLAS");
        for(int i=0; i<classes.size();i++)
        {
            System.out.println("\tKlasa "+classes.get(i));
            try {
                Class singleClass = classLoader.loadClass(classes.get(i));
                Constructor<?>[] ctors = singleClass.getDeclaredConstructors();
                showFunctions(ctors);
            } catch (ClassNotFoundException e) {
                System.out.println("[WARN] Class " + classes.get(i) + "not found. Constructors of class can not be showed");
            }
        }
    }
    private void showFunctions(Executable[] functions)
    {
        for(int i=0; i<functions.length;i++)
        {

                //System.out.println(Modifier.toString(object[i].getModifiers())+object[i].getName());
                System.out.print("\t\t"
                                +Modifier.toString(functions[i].getModifiers())
                                +" "
                                +functions[i].getName() + "(");

                Parameter[] parameters = functions[i].getParameters();
                for(int k=0; k<parameters.length; k++)
                {
                    System.out.print(parameters[k].getType().getName()
                                        + " "
                                        + parameters[k].getName());

                    if(k!=parameters.length-1)
                        System.out.print(" , ");
                }
                System.out.print(")\n");
        }
    }
}
