import com.kinde.accounts.KindeAccountsClient;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;

public class TestConstructor {
    public static void main(String[] args) {
        // Test with null KindeClient
        KindeAccountsClient client1 = new KindeAccountsClient((KindeClient) null);
        System.out.println("Created client with null KindeClient: " + (client1 != null));
        
        // Test with null KindeClientSession (should throw exception)
        try {
            KindeAccountsClient client2 = new KindeAccountsClient((KindeClientSession) null);
            System.out.println("Created client with null KindeClientSession: " + (client2 != null));
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly threw exception for null KindeClientSession: " + e.getMessage());
        }
        
        System.out.println("All tests passed!");
    }
}
