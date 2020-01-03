package Jfk_lab2;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.jar.Manifest;

public interface JarExploring {
    public void showClassNames();
    public void showPackageNames();
    public void showMethodsOfClasses(LinkedList<String> classes);
    public void showCtorsOfClasses(LinkedList<String> classes);
    public void showFieldsOfClasses(LinkedList<String> classes);
    public LinkedList<String> getClassNames();
    public Manifest getManifest();
    public LinkedList<InputStream> getStreamsForOtherFiles();
    public LinkedList<String> getEntriesNamesForOtherFiles();
}
