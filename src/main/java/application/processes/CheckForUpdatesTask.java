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
import java.util.Enumeration;
import java.util.jar.Manifest;

public class CheckForUpdatesTask extends Task<String> {
    private final Logger LOG;
    private final String URL = "https://raw.githubusercontent.com/SirMoM/BirthdayManager/releases/master/gradle.properties";

    public CheckForUpdatesTask() {
        LOG = LogManager.getLogger(getClass().getName());
    }

    @Override
    protected String call() throws Exception {
        LOG.info("Check for version!");
        String currentVersion = getCurrentVersion();
        String latestVersion = getLatestVersion();
        LOG.info("This version {} upstream version {}", currentVersion, latestVersion);
        if (!currentVersion.equals(latestVersion)) {
            LOG.info("Newer Version of the Software is available! New version {}", latestVersion);
            return "A new version is available " + latestVersion;
        } else {
            System.out.println(latestVersion);
            return null;
        }
    }

    private String getLatestVersion() throws IOException {
        // Sending get request
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

//    conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String output = in.readLine();
        in.close();

        System.out.println(output);

        // Parse result and return version
        return output.split("=")[1];
    }

    private String getCurrentVersion() throws IOException {
        String version = this.getClass().getPackage().getImplementationVersion();
        if (version == null){
            BufferedReader reader = new BufferedReader(new FileReader("gradle.properties"));
            version = reader.readLine().split("=")[1];
        }

        return version;
    }
}
