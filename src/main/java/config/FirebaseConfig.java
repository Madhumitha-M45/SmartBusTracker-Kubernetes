package config;
import java.io.FileInputStream;
import java.io.IOException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseConfig {
    private FirebaseConfig() {}
    public static void initialize() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                System.out.println("[FIREBASE] Firebase is already initialized.");
                return;
            }
            String serviceAccountPath = "/home/madhumitha/firebase/serviceAccountKey.json";
            System.out.println("[FIREBASE] Initializing Firebase...");
            System.out.println("[FIREBASE] Service account path: " + serviceAccountPath);
            FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
            FirebaseOptions options =FirebaseOptions.builder()
                            .setCredentials(
                                    GoogleCredentials.fromStream(serviceAccount)
                            )
                            .build();
            FirebaseApp.initializeApp(options);
            serviceAccount.close();
            System.out.println("[FIREBASE] Firebase initialized successfully.");
        } catch (IOException e) {
            System.err.println("[FIREBASE] Firebase initialization failed.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[FIREBASE] Unexpected Firebase initialization error.");
            e.printStackTrace();
        }
    }
}