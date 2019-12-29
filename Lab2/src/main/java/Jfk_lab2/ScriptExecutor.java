package Jfk_lab2;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.LinkedList;

public class ScriptExecutor {

    private LinkedList<String> classNames = new LinkedList<>();
    private LinkedList<String> packagesNames = new LinkedList<>();
    private LinkedList<CtClass> ctClasses = new LinkedList<>();
    ClassPool classPool = ClassPool.getDefault();
    public ScriptExecutor(String jarPath, String scriptPath, LinkedList<String> classNames, LinkedList<String> packagesNames) throws ErrorException {
        this.classNames = classNames;
        this.packagesNames = packagesNames;


        try {
            classPool.insertClassPath(jarPath);
            classPool.appendClassPath(jarPath);
        } catch (NotFoundException e) {
            throw new ErrorException("Classpool error: jarPath not found", 20);
        }

        prepareClasses();
        if(scriptPath!=null)
        {
            executeScript(scriptPath);
        }
    }
    private void prepareClasses()
    {
        for(int i=0; i<classNames.size();i++)
        {
            try {
                CtClass ct = classPool.get(classNames.get(i));
                ctClasses.add(ct);
            } catch (NotFoundException e) {
                System.out.println("[WARN] Class "+classNames.get(i)+ " has been not found!");
            }
        }
    }
    public void executeScript(String scriptPath)
    {

    }


    public LinkedList<CtClass> getCtClasses() {return ctClasses; }
    public LinkedList<String> getPackagesNames() {return packagesNames;}
}
