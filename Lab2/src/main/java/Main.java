public class Main
{
    public static void main(String [] args)
    {
        try
        {
            ParameterReader parReader = new ParameterReader(args);
        }
        catch(ErrorException e)
        {
            System.out.println(e);
        }
    }
}
