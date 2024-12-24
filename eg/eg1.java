import java.lang.reflect.*;

class XYZException extends Exception{
    public XYZException(String message){
        super(message);
    }
}

class ABCD{
    public int pqr(int a,int b) throws XYZException {
        throw new XYZException("Invalid data ");
    }
}

public class eg1 {
    public static void main(String[] args) {
        try{
            Class c = ABCD.class;
        Class parameters[] = {int.class,int.class};
        Method method = c.getMethod("pqr", parameters);
        Object obj = c.newInstance();
        Object arguments[] = {20,30};
        method.invoke(obj,arguments);
        
        }catch(NoSuchMethodException | IllegalAccessException | InstantiationException  e){
            System.out.println(e);
        }catch(InvocationTargetException ite) {
            // Exception e = ite.getCause();   // Throwable can't be coverted into  Exception : error
            Throwable t = ite.getCause();
            XYZException x =(XYZException) t; // bcoz we know it is a XYZException that's why we did this
            System.out.println(x);
        }
    }
}
