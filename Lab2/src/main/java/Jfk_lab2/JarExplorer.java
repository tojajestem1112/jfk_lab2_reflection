package Jfk_lab2;

import Jfk_lab2.ErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarExplorer
{

    private String jarPath;
    private LinkedList<String> classNames = new LinkedList<>();
    private LinkedList<String> packagesNames = new LinkedList<>();
    private LinkedList<InputStream> streamsForOtherFiles = new LinkedList<>();
    private LinkedList<String> entriesForOtherFiles = new LinkedList<>();
    private ClassLoader classLoader;
    private Manifest manifest=null;

    public JarExplorer(String jarPath) throws ErrorException {
        this.jarPath = jarPath;
        Enumeration<JarEntry> enumeration;
        JarFile jarFile;


        try
        {
            jarFile = new JarFile(jarPath+"//");
            manifest = jarFile.getManifest();
            enumeration = jarFile.entries();
            URL[] urls = {new URL("jar:file:"+jarPath+"!/")};
            classLoader = URLClassLoader.newInstance(urls);
        } catch (IOException e) {
            throw new ErrorException("The input file has been not found [.jar]", 3);
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
            else if(!jarEntry.isDirectory()&&!jarEntry.getName().endsWith(".MF"))
            {
                try {
                    streamsForOtherFiles.add(jarFile.getInputStream(jarEntry));
                    entriesForOtherFiles.add(jarEntry.getName());
                } catch (IOException e) {
                    throw new ErrorException ("IO EXCEPTION", 100);
                }
            }
        }



    }
    public void showClassNames()
    {
        System.out.println("Classes:\n");
        for(int i =0; i<classNames.size(); i++)
        {
            System.out.println("\t"+classNames.get(i));
        }
    }
    public void showPackageNames()
    {
        System.out.println("Packages:\n");
        for(int i=0; i<packagesNames.size();i++)
        {
            System.out.println("\t"+packagesNames.get(i));
        }
    }
    public void showMethodsOfClasses(LinkedList<String> classes)
    {
        System.out.println("\nLIST OF METHODS IN CLASSES");
        for(int i=0; i<classes.size();i++)
        {
            System.out.println("\tClass "+classes.get(i));

            try {
                Class singleClass = classLoader.loadClass(classes.get(i));
                Method[] methods = singleClass.getDeclaredMethods();
                showFunctions(methods, true);
            } catch (ClassNotFoundException e) {
                System.out.println("[WARN] Class " + classes.get(i) + "not found. Methods of class can not be showed");
            }
        }
    }
    public void showCtorsOfClasses(LinkedList<String> classes)
    {
        System.out.println("\nLIST OF CONSTRUCTORS IN CLASSES");
        for(int i=0; i<classes.size();i++)
        {
            System.out.println("\tClass "+classes.get(i));
            try {
                Class singleClass = classLoader.loadClass(classes.get(i));
                Constructor<?>[] ctors = singleClass.getDeclaredConstructors();
                showFunctions(ctors, false);
            } catch (ClassNotFoundException e) {
                System.out.println("[WARN] Class " + classes.get(i) + "not found. Constructors of class can not be showed");
            }
        }
    }
    public void showFieldsOfClasses(LinkedList<String> classes)
    {
        System.out.println("LIST OF FIELDS IN CLASSES");
        for(int i=0; i<classes.size();i++)
        {
            System.out.println("\tClass "+classes.get(i));
            try
            {
                Class singleClass = classLoader.loadClass(classes.get(i));
                Field[] fields = singleClass.getDeclaredFields();
                for(int k=0; k<fields.length; k++)
                {
                    System.out.println("\t\t"+Modifier.toString(fields[k].getModifiers())
                                            + " "
                                            +fields[k].getType().getName()
                                            + " "
                                            +fields[k].getName());
                }
            }catch(ClassNotFoundException e){
                System.out.println("[WARN] Class " + classes.get(i) + " not found. Methods of class can not be showed");
            }
        }
    }
    private void showFunctions(Executable[] functions, boolean isMethod)
    {
        for(int i=0; i<functions.length;i++)
        {

                //System.out.println(Modifier.toString(object[i].getModifiers())+object[i].getName());
                System.out.print("\t\t"
                                +Modifier.toString(functions[i].getModifiers())
                                +" ");
                if(isMethod)
                {
                    Method met = (Method) functions[i];
                    System.out.print(met.getReturnType().getName());
                }
                System.out.print(" " + functions[i].getName() + "(");

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

    public LinkedList<String> getClassNames() {return classNames;}

    public LinkedList<String> getPackagesNames() {return packagesNames;}

    public Manifest getManifest() {return manifest;}

    public LinkedList<InputStream> getStreamsForOtherFiles() {
        return streamsForOtherFiles;
    }

    public LinkedList<String> getEntriesForOtherFiles() {
        return entriesForOtherFiles;
    }
}
