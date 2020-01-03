package Jfk_lab2;

import javassist.CtClass;

import java.util.LinkedList;

public interface ScriptExecuting {
    public LinkedList<CtClass> getCtClasses();
    public LinkedList<String> getPackagesNames();
}
