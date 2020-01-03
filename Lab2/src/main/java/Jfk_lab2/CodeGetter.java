package Jfk_lab2;

import java.io.*;

public class CodeGetter extends File implements GettingCodeFromPath
{
    CodeGetter(String path){
        super(path);
    }
    public String getCode() throws ErrorException {
        StringBuilder returning = new StringBuilder();
        returning.append("{");
        try (
                FileReader fileReader = new FileReader(this);
                BufferedReader buffReader = new BufferedReader(fileReader);
        ){
            String line=null;
            while((line=buffReader.readLine()) != null)
            {
                if(line.equals("")) continue;
                returning.append(line+" ");
            }
        } catch (FileNotFoundException e) {
            throw new ErrorException("Can not find src file!", 28);
        } catch(IOException f)
        {
            throw new ErrorException("Can not open src file!", 28);
        }
        return returning.toString()+"}";
    }
}
