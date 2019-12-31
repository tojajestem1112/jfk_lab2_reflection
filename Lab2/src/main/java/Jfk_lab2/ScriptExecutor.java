package Jfk_lab2;

import javassist.*;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class ScriptExecutor {

    private LinkedList<String> classNames = new LinkedList<>();
    private LinkedList<String> addedPackagesNames = new LinkedList<>();
    private LinkedList<String> addedClassNames = new LinkedList<>();
    private LinkedList<CtClass> ctClasses = new LinkedList<>();
    ClassPool classPool = ClassPool.getDefault();
    public ScriptExecutor(String jarPath, String scriptPath, LinkedList<String> classNames) throws ErrorException {
        this.classNames = classNames;


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


    private void executeScript(String scriptPath) throws ErrorException {
        System.out.println("EXECUTING SCRIPT");
        File file = new File(scriptPath);
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader buffReader = new BufferedReader(fileReader);
        ){
            String scriptLine=null;
            while((scriptLine=buffReader.readLine()) != null)
            {
                if(scriptLine.equals("")) continue;
                executeCommend(scriptLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException f)
        {
            throw new ErrorException("Closing reared failure", 26);
        }

    }
    private String [] getCommendAndContent(String scriptLine) throws ErrorException {

        scriptLine = scriptLine.replace('\t', ' ');
        scriptLine=delRedundantWS(scriptLine);

        String[] commendAndContent = scriptLine.split(" ",2);
        if(commendAndContent.length!=2)// there is only 1 part - there is not content
        {
            throw new ErrorException("Commend execution failure!", 31);
        }
        commendAndContent[1] = delRedundantWS(commendAndContent[1]);//deleting WS between commend and content

        return commendAndContent;
    }
    private void executeCommend(String scriptLine) throws ErrorException {
        String[] commendAndContent = getCommendAndContent(scriptLine);
        //commend as 0 ; content as 1
        if(commendAndContent[0].equals("add-package"))
        {
            System.out.println("Adding package "+commendAndContent[1]+"....");
            addPackage(commendAndContent[1]);
        }else if(commendAndContent[0].equals("remove-package"))
        {
            System.out.println("Removing package "+commendAndContent[1]+"....");
            removePackage(commendAndContent[1]);
        }else if(commendAndContent[0].equals("add-class"))
        {
            System.out.println("Adding class "+commendAndContent[1]+"....");
            addClass(commendAndContent[1], false);
        }else if(commendAndContent[0].equals("add-interface"))
        {
            System.out.println("Adding interface "+commendAndContent[1]+"....");
            addClass(commendAndContent[1], true);
        }else if(commendAndContent[0].equals("remove-class"))
        {
            System.out.println("Removing class "+commendAndContent[1]+"....");
            removeClass(commendAndContent[1], false);
        }else if(commendAndContent[0].equals("remove-interface"))
        {
            System.out.println("Removing interface "+commendAndContent[1]+"....");
            removeClass(commendAndContent[1], true);
        }else if(commendAndContent[0].equals("add-method"))
        {
            System.out.println("Adding method: "+commendAndContent[1]+"....");
            int index1 = commendAndContent[1].indexOf(' ');
            if(index1<0)
                throw new ErrorException("Invalid command(Did u forget about method?)",98);
            String clazzName = commendAndContent[1].substring(0,index1);
            String methodString = commendAndContent[1].substring(index1,commendAndContent[1].length());
            addFunction(clazzName,methodString, false);
        }else if(commendAndContent[0].equals("remove-method"))
        {
            System.out.println("Removing method "+commendAndContent[1]+"....");
            String clazzName = getClassNameFromFunction(commendAndContent[1]);
            removeMethod(clazzName, commendAndContent[1]);
        }else if(commendAndContent[0].equals("set-method-body"))
        {
            System.out.println("Setting method body: "+commendAndContent[1]+"....");
            String[] tempTable = commendAndContent[1].split("\\)", 2);//tempTable[1] gives code
            if(tempTable.length<2)
                throw new ErrorException("Invalid commend(did u forget about src path?)",62);
            String methodName = tempTable[0]+")";
            String clazzName = getClassNameFromFunction(methodName);
            setMethodContent(clazzName, methodName,  tempTable[1],0);

        }else if(commendAndContent[0].equals("add-before-method"))
        {
            System.out.println("Adding before method: "+commendAndContent[1]+"....");
            String[] tempTable = commendAndContent[1].split(" ", 2);
            if(tempTable.length<2)
                throw new ErrorException("Invalid commend(did u forget about src path?)",63);
            String methodName = tempTable[0];
            String clazzName =getClassNameFromFunction(methodName);
            setMethodContent(clazzName, methodName,  tempTable[1],-1);

        }else if(commendAndContent[0].equals("add-after-method"))
        {
            System.out.println("Adding after method: "+commendAndContent[1]);
            String[] tempTable = commendAndContent[1].split(" ", 2);
            if(tempTable.length<2)
                throw new ErrorException("Invalid commend(did u forget about src path?)",64);
            String methodName = tempTable[0];
            String clazzName =getClassNameFromFunction(methodName);
            setMethodContent(clazzName, methodName,  tempTable[1],1);
        }else if(commendAndContent[0].equals("add-field"))
        {
            System.out.println("Adding filed: "+commendAndContent[1]+"....");
            String[] parts = commendAndContent[1].split(" ", 2);//part 1 gives field
            if(parts.length<2)
                throw new ErrorException("Invalid commend(did u forget about field name?)",65);
            String clazzName = parts[0];
            String fieldName=delRedundantWS(parts[1]);

            addField(clazzName, fieldName);


        }else if(commendAndContent[0].equals("remove-field"))
        {
            System.out.println("Removing field: "+commendAndContent[1]+"....");
            String[] parts = commendAndContent[1].split(" ", 2);
            if(parts.length<2)
                throw new ErrorException("Invalid commend(did u forget about field name?)",65);
            String clazzName = parts[0];
            String fieldName=delRedundantWS(parts[1]);
            removeField(clazzName, fieldName);

        }else if(commendAndContent[0].equals("add-ctor"))
        {
            System.out.println("Adding constructor: "+commendAndContent[1]+"....");
            int index1 = commendAndContent[1].indexOf(' ');
            if(index1<0)
                throw new ErrorException("Invalid command(Did u forget about ctor?)",98);
            String clazzName = commendAndContent[1].substring(0,index1);
            String ctorName = commendAndContent[1].substring(index1,commendAndContent[1].length());
            addFunction(clazzName, ctorName,true);
        }else if(commendAndContent[0].equals("remove-ctor"))
        {
            System.out.println("Removing constructor....");
            String clazzName = getClassNameFromFunction(commendAndContent[1]);
            removeCtor(clazzName, commendAndContent[1]);
        }else if(commendAndContent[0].equals("set-ctor-body"))
        {
            System.out.println("Setting ctor body ....");
            String[] tempTable = commendAndContent[1].split(" ", 2);
            String ctorName = tempTable[0];
            String clazzName = getClassNameFromFunction(ctorName);
            setContBody(clazzName, ctorName,  tempTable[1]);
        } else
        {
            throw new ErrorException("Unknown commend", 32);
        }

    }
    public LinkedList<CtClass> getCtClasses() {return ctClasses; }
    public LinkedList<String> getPackagesNames() {return addedPackagesNames;}
    private String delRedundantWS(String string) {
        while(string.startsWith(" "))
        {
            string=string.substring(1,string.length());
        }
        while(string.endsWith(" "))
        {
            string=string.substring(0,string.length()-1);
        }
        return string;
    }
    private void addPackage(String nameOfPackage) throws ErrorException {
        Pattern pat = Pattern.compile("([a-zA-Z_$][a-zA-Z_$0-9]*\\.)*[a-zA-Z_$][a-zA-Z_$0-9]*");
        if(pat.matcher(nameOfPackage).matches())
        {
            addedPackagesNames.add(nameOfPackage);
            System.out.println("Package has benn added");
        }
        else
        {
            throw new ErrorException("Invalid name of package!",41);
        }
    }
    private void removePackage(String packageName) {
        boolean removed = false;
        for(int i=0;i<addedPackagesNames.size();i++)
        {
            if(addedPackagesNames.get(i).startsWith(packageName))
            {
                addedPackagesNames.remove(i);
                i--;
                removed = true;
            }
        }
        if(!removed)
        {
            System.out.println("[WARN] Package "+packageName+" was not created by you or it doesnt exist. The package can not be deleted");
        }
        else {
            for (int k = 0; k < ctClasses.size(); k++) {
                if (ctClasses.get(k).getName().startsWith(packageName)) {
                    ctClasses.remove(k);
                    k--;
                }
            }
            System.out.println("Package has been removed.");
        }

    }
    private void addClass(String className, boolean isInterface) throws ErrorException {
        Pattern pat = Pattern.compile("([a-zA-Z_$][a-zA-Z_$0-9]*\\.)*[a-zA-Z_$][a-zA-Z_$0-9]*");
        if(pat.matcher(className).matches())
        {
            CtClass clazz;
            if(isInterface)
                 clazz = classPool.makeInterface(className);
            else
                clazz = classPool.makeClass(className);
            ctClasses.add(clazz);
            addedClassNames.add(clazz.getName());
            System.out.println("Class/Interface has benn added");
        }
        else
        {
            throw new ErrorException("Invalid name of class!",41);
        }
    }
    private void removeClass(String className, boolean isInterface) {
        boolean removed = false;
        if(!addedClassNames.contains(className))
        {
            System.out.println("[WARN] That class/inreface hasn`t been added by you or it doesnt exist. You cannot delete it.");
        }
        else {

            for (int k = 0; k < ctClasses.size(); k++) {

                if (ctClasses.get(k).getName().equals(className)) {
                    if (!ctClasses.get(k).isInterface() && isInterface)
                        System.out.println("[WARN] That class IS NOT interface! You deleted class!(next time use remove-class expression)");
                    else if (ctClasses.get(k).isInterface() && !isInterface)
                        System.out.println("[WARN] That class is INTERFACE. You deleted interface!(next time use remove-interface expression)");
                    ctClasses.remove(k);
                    addedClassNames.remove(className);
                    removed=true;
                    break;
                }


            }
            if(removed)
                System.out.println("Class/Interface has been removed");
            else
                System.out.println("Class/Interface not found");
        }
    }
    private void addFunction(String className, String functionOuterBody, boolean isCtor) throws ErrorException {
        try {
            CtClass clazz = classPool.get(className);
            if(!isCtor) {
                CtMethod method = CtNewMethod.make(functionOuterBody, clazz);
                clazz.addMethod(method);
            } else {
                CtConstructor ctor = CtNewConstructor.make(functionOuterBody, clazz);
                clazz.addConstructor(ctor);
            }
            System.out.println("Function has benn added");
        } catch (NotFoundException e) {
            throw new ErrorException("Invalid name of class",52);
        }catch(CannotCompileException f)
        {
            throw new ErrorException("Compilation Error: " + f.getReason(), 53);
        }
    }
    private String getClassNameFromFunction(String functionName) {
        String clazzName ="";
        String[] parts = functionName.split("\\.");
        for(int i=0;i<parts.length; i++)
        {

            clazzName += parts[i];
            if(!parts[i+1].contains("(")) {
                clazzName += ".";
            }
            else
            {
                break;
            }
        }
        return clazzName;
    }
    private void removeMethod(String clazzName, String longFunctionName) throws ErrorException {
        try {
            longFunctionName=longFunctionName.replace(" ", "");//must be (int,int)
            boolean removed = false;
            CtClass clazz = classPool.get(clazzName);
            CtMethod[] mets = clazz.getDeclaredMethods();
            for(CtMethod ct : mets)
            {
                if(ct.getLongName().equals(longFunctionName)) {
                    clazz.removeMethod(ct);
                    removed = true;
                    System.out.println("Method "+longFunctionName+" has been removed");
                    break;
                }
            }
            if(!removed)
                System.out.println("[WARN] Method not found");
        } catch (NotFoundException e) {
            throw new ErrorException("Class "+clazzName + " not found", 54);
        }
    }
    private void removeCtor(String clazzName, String longFunctionName) throws ErrorException {
        try {
            boolean removed = false;
            CtClass clazz = classPool.get(clazzName);
            CtConstructor[] ctors = clazz.getDeclaredConstructors();
            for(CtConstructor ctor : ctors)
            {
                if(ctor.getLongName().equals(longFunctionName)) {
                    clazz.removeConstructor(ctor);
                    removed = true;
                    break;
                }
            }
            if(removed)
                System.out.println("Ctor has been deleted");
            else
                System.out.println("[WARN] Ctor not found");
        } catch (NotFoundException e) {
            throw new ErrorException("Class "+clazzName+" not found", 60);
        }
    }
    private void addField(String clazzName, String fieldName) throws ErrorException {
        try{
            if(!fieldName.contains(";"))
                fieldName+=";";
            CtClass clazz = classPool.get(clazzName);
            CtField field = CtField.make(fieldName,clazz);
            clazz.addField(field);
            System.out.println("Field has been added");
        } catch (NotFoundException e) {
            throw new ErrorException("Class "+clazzName+" not found.", 57);
        }catch (CannotCompileException e) {
            throw new ErrorException("Compile Error: "+e.getReason(), 58);
        }
        }
    private void removeField(String clazzName, String fieldName) throws ErrorException {
        try{

            CtClass clazz = classPool.get(clazzName);
            CtField field = clazz.getField(fieldName);
            clazz.removeField(field);
            System.out.println("Field has been removed");
        } catch (NotFoundException e) {
            throw new ErrorException("Class "+clazzName+ " or field "+fieldName+" not found",59);
        }
    }
    private void setMethodContent(String clazzName,String methodName,String srcPath, int positionOfInsert) throws ErrorException {
        srcPath = delRedundantWS(srcPath);
        methodName=methodName.replace(" ","");
        boolean changed = false;
        CodeGetter srcFile = new CodeGetter(srcPath);
        String srcCode = srcFile.getCode();
        try {
            CtClass clazz = classPool.get(clazzName);
            CtMethod[] mets = clazz.getDeclaredMethods();
            for(CtMethod ct : mets)
            {
                if(ct.getLongName().equals(methodName)) {
                    if(positionOfInsert==0)
                        ct.setBody(srcCode);
                    else if(positionOfInsert==-1)
                        ct.insertBefore(srcCode);
                    else if(positionOfInsert==1)
                        ct.insertAfter(srcCode);
                    changed=true;
                    System.out.println("The content of method has been changed");
                    break;
                }
            }
            if(!changed)
                System.out.println("[WARN]Method not found");
        } catch (NotFoundException e) {
            throw new ErrorException("Class "+clazzName+" not found",56);
        } catch (CannotCompileException e) {
            throw new ErrorException("Cannot compile code: "+e.getReason(),55);
        }
    }
    private void setContBody(String clazzName,String  ctorName,String srcPath) throws ErrorException {
        srcPath = delRedundantWS(srcPath);
        boolean changed = false;
        ctorName=ctorName.replace(" ","");
        CodeGetter srcFile = new CodeGetter(srcPath);
        String srcCode = srcFile.getCode();
        try {
            CtClass clazz = classPool.get(clazzName);
            CtConstructor[] mets = clazz.getDeclaredConstructors();
            for(CtConstructor ct : mets)
            {
                if(ct.getLongName().equals(ctorName)) {
                    ct.setBody(srcCode);
                    changed = true;
                    System.out.println("The body of ctor has been changed");
                    break;
                }
            }
            if(!changed)
                System.out.println("[WARN] Ctor not found.");

        } catch (NotFoundException e) {
            throw new ErrorException("Class "+clazzName+ " not found", 61);
        } catch (CannotCompileException e) {
            throw new ErrorException("Compilation error: "+e.getReason(), 61);
        }

    }

}
