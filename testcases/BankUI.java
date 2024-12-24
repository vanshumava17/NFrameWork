import com.vku.nframework.client.*;

public class BankUI {

    public static void main(String gg[]) {
        try {
            NFrameworkClient client = new NFrameworkClient();
            String branchName = (String) client.execute("/banking/branchname", gg[0]);
            System.out.println(branchName);

	    String branch = (String) client.execute("/banking/branchname",gg[0]);
		System.out.println(branchName);
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
    }
}