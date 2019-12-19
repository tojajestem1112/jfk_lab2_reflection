package Jfk_lab2;

public class Main
{
    public static void main(String [] args)
    {
        try
        {
            ParameterReader parReader = new ParameterReader(args);
            System.out.println(parReader);
        }
        catch(ErrorException e)
        {
            System.out.println(e);
        }
    }
}
