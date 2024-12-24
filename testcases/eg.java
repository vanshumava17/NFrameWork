
import com.vku.nframework.common.*;

class a implements java.io.Serializable{
    public int x;
    a(){
        x =10;
    }
}

public class eg {
    public static void main(String gg[]){
        a aa = new a();

        String g = JSONUtil.toJSON(aa);

        System.out.println(g);
    }
}
