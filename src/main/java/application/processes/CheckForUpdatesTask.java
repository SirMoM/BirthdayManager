package application.processes;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
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
        String version = "";
        ClassLoader classLoader = getClass().getClassLoader();
        Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            Manifest manifest = new Manifest(resources.nextElement().openStream());
            // check that this is your manifest and do what you need or get the next one
            if (manifest.getMainAttributes().getValue("Implementation-Title") == "BirthdayManager") {
                version = manifest.getMainAttributes().getValue("Implementation-Version");
                break;
            } else {
                LOG.debug("Didn't find own manifest!");
            }
        }
        return version;
    }
}
