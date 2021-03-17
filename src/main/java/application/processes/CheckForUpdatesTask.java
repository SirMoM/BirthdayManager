package application.processes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckForUpdatesTask extends Task<String> {
  private final Logger LOG;
  private final String URL =
      "https://api.github.com/users/SirMoM/packages/maven/sirmom.birthdaymanager/versions";
  private String ACCESS_TOKEN = "cb24149ef51b7796e35cc508c3c35143b6fa2ef2";

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
      return null;
    }
  }

  private String getLatestVersion() throws IOException {
    // Sending get request
    URL url = new URL(URL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String output;

    StringBuilder response = new StringBuilder();

    while ((output = in.readLine()) != null) {
      response.append(output);
    }

    in.close();

    // Parse result and return version
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.toString());
    return root.get(0).get("name").toPrettyString();
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
