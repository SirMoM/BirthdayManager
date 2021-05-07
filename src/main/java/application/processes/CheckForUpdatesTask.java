package application.processes;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckForUpdatesTask extends Task<String> {
    private static final Logger LOG = LogManager.getLogger(CheckForUpdatesTask.class.getName());
    private static final String URL = "https://raw.githubusercontent.com/SirMoM/BirthdayManager/releases/master/gradle.properties";

    @Override
    protected String call() throws Exception {
        String currentVersion = getCurrentVersion();
        String latestVersion = getLatestVersion();

        LOG.info("This version {} upstream version {}", currentVersion, latestVersion);

        if (!currentVersion.equals(latestVersion)) {
            LOG.info("Newer Version of the Software is available! New version {}", latestVersion);
            return "A new version is available " + latestVersion;
        } else {
            return null;
        }
    }

    private String getLatestVersion() throws IOException {
        // Sending get request
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read result
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String output = in.readLine();
        in.close();

        // Parse result and return version
        return output.split("=")[1];
    }

    public static String getCurrentVersion(){
        String version = CheckForUpdatesTask.class.getPackage().getImplementationVersion();
        if (version == null) {
            try(BufferedReader reader = new BufferedReader(new FileReader("gradle.properties"))){
                version = reader.readLine().split("=")[1];
            } catch (IOException ioException) {
                LOG.catching(ioException);
            }
        }
        return version;
    }
}
