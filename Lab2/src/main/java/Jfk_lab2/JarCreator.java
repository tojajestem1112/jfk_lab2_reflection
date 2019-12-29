package Jfk_lab2;

import javassist.CannotCompileException;
import javassist.CtClass;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarCreator
{
    LinkedList<CtClass> ctClasses= new LinkedList<>();
    LinkedList<String> packagesNames = new LinkedList<>();
    Manifest manifest = null;

    public JarCreator(LinkedList<CtClass> ctClasses, LinkedList<String> packagesNames, Manifest manifest)
    {
        this.ctClasses = ctClasses;
        this.packagesNames = packagesNames;
        this.manifest = manifest;
    }

    public int createJar(String outputPath) throws ErrorException {
        if(ctClasses.size()==0)
        {
            return 0;
        }

        try (
                FileOutputStream fos = new FileOutputStream(outputPath);
                JarOutputStream jos = new JarOutputStream(fos,manifest);
            ){
            for(int i=0; i<packagesNames.size(); i++)
            {
                JarEntry entry = new JarEntry(packagesNames.get(i).replace('.','/')+"/");
                jos.putNextEntry(entry);
                jos.closeEntry();
            }
            for(int i=0; i<ctClasses.size(); i++)
            {
                JarEntry entry = new JarEntry(ctClasses.get(i).getName().replace('.','/')+".class");
                jos.putNextEntry(entry);
                byte[] buffer = ctClasses.get(i).toBytecode();
                jos.write(buffer);
                jos.closeEntry();
            }


        } catch (FileNotFoundException e) {
            throw new ErrorException("File not found error "+outputPath, 23);
        } catch (CannotCompileException g) {
            throw new ErrorException("Cannot compile Exception", 24);
        } catch (IOException f) {
            throw new ErrorException("Stream Exception", 25);
        }

        return 1;

    }
}
