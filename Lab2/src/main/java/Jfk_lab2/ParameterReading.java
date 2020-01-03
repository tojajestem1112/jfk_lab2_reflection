package Jfk_lab2;

import java.util.LinkedList;

public interface ParameterReading {
    public String toString();
    public String getJarPath();
    public boolean isListOfPackagesParameter();
    public boolean isListOfClassesParameter();
    public LinkedList<String> getListOfClassForListMethods();
    public LinkedList<String> getListOfClassForListFields();
    public LinkedList<String> getListOfClassForListCtors();
    public String getScriptPath();
    public String getOutputPath();
}
