package Jfk_lab2;

import Jfk_lab2.ErrorException;
import javassist.CannotCompileException;
import javassist.CtClass;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarCreator
{
    LinkedList<CtClass> ctClasses= new LinkedList<>();
    LinkedList<String> packagesNames = new LinkedList<>();
    LinkedList<InputStream> streamsForOtherFiles = new LinkedList<>();
    LinkedList<String> entriesForOtherFiles = new LinkedList<>();
    Manifest manifest = null;

    public JarCreator(LinkedList<CtClass> ctClasses, LinkedList<String> packagesNames,
                      Manifest manifest, LinkedList<InputStream> streamsForOtherFiles, LinkedList<String> entriesForOtherFiles)
    {
        this.ctClasses = ctClasses;
        this.packagesNames = packagesNames;
        this.manifest = manifest;
        this.streamsForOtherFiles = streamsForOtherFiles;
        this.entriesForOtherFiles = entriesForOtherFiles;
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
            byte buff[]=new byte[1024];
            int var;
            for(int i=0; i<streamsForOtherFiles.size();i++)
            {

                JarEntry entry = new JarEntry(entriesForOtherFiles.get(i));
                jos.putNextEntry(entry);
                while((var= (streamsForOtherFiles.get(i).read(buff, 0, buff.length))) !=-1)
                    jos.write(buff,0, var );
                jos.closeEntry();
            }


            for(int i=0; i<packagesNames.size(); i++)//in need because of empty packages
            {
                JarEntry entry = new JarEntry(packagesNames.get(i).replace('.','/')+"/");
                jos.putNextEntry(entry);
                jos.closeEntry();
            }
            for(int i=0; i<ctClasses.size(); i++)//adding all classes
            {
                JarEntry entry = new JarEntry(ctClasses.get(i).getName().replace('.','/')+".class");
                jos.putNextEntry(entry);
                byte[] buffer = ctClasses.get(i).toBytecode();
                jos.write(buffer);
                jos.closeEntry();
            }


        } catch (FileNotFoundException e) {
            throw new ErrorException("Cannot create new jar! (is jar opened in other program?)"+outputPath, 23);
        } catch (CannotCompileException g) {
            throw new ErrorException("Cannot compile Exception", 24);
        } catch (IOException f) {
            throw new ErrorException("Stream Exception: "+f.getMessage(), 25);
        }

        return 1;

    }
}
