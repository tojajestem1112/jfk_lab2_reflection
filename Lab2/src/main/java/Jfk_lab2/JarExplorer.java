package Jfk_lab2;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExplorer
{

    private String jarPath;
    private LinkedList<String> classNames = new LinkedList<>();
    private LinkedList<String> packagesNames = new LinkedList<>();

    public JarExplorer(String jarPath) throws ErrorException {
        this.jarPath = jarPath;
        Enumeration<JarEntry> enumeration;
        JarFile jarFile;

        try
        {
            jarFile = new JarFile(jarPath+"//");
            enumeration = jarFile.entries();
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
        System.out.println("Klasy:\n");
        for(int i =0; i<classNames.size(); i++)
        {
            System.out.println("\t"+classNames.get(i));
        }
    }
    public void showPackageNames()
    {
        System.out.println("Pakiety:\n");
        for(int i=0; i<packagesNames.size();i++)
        {
            System.out.println("\t"+packagesNames.get(i));
        }
    }
}
