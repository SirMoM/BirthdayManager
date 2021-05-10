package application.util;


import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class ApplicationSetup {
    private static final Logger LOG = LogManager.getLogger(ApplicationSetup.class.getName());

    public static void setup() {
        LOG.info("Setup application");
        checkDir();
        // secure compatibility to older versions
        moveProperties();

        PropertyManager.getInstance();
        checkLastOpenedFiles();
        checkOnStartFile();
    }

    // moves the properties file to the .gbm directory
    private static void moveProperties() {
        File newPropertiesFile = new File(".gmb/BirthdayManager.properties");
        File propertiesFile = new File("BirthdayManager.properties");

        if ((propertiesFile.exists() && propertiesFile.isFile()) && !newPropertiesFile.exists()) {
            LOG.info("Moving properties to .gbm");
            Properties properties = PropertyManager.getInstance().getProperties();
            properties.clear();
            try(FileInputStream fis = new FileInputStream(propertiesFile)) {
                properties.load(fis);
                PropertyManager.getInstance().storeProperties("Load from previous version!");
            } catch (IOException ioException) {
                LOG.catching(ioException);
                return;
            }

            try {
                Files.deleteIfExists(propertiesFile.toPath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static void checkOnStartFile() {
        String onStartFile = PropertyManager.getProperty(PropertyFields.FILE_ON_START);
        if (onStartFile == null) return;
        File file = new File(onStartFile);
        if (!file.exists() || !file.isFile() || !onStartFile.endsWith(".csv")) {
            LOG.info("On start file is not valid {} and will be cleansed", onStartFile);
            PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FILE_ON_START, "");
        }
    }

    private static void checkLastOpenedFiles() {
        String lastOpenedFiles = PropertyManager.getProperty(PropertyFields.LAST_OPENED);
        if (lastOpenedFiles == null) return;
        String[] filePaths = lastOpenedFiles.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(filePaths[0]);
        if (file.exists() && file.isFile() && !lastOpenedFiles.endsWith(".csv")) {
            stringBuilder.append(file.getAbsolutePath());
        }
        for (int i = 1; i < filePaths.length; i++) {
            file = new File(filePaths[i]);
            if (file.exists() && file.isFile() && !lastOpenedFiles.endsWith(".csv")) {
                stringBuilder.append(","+file.getAbsolutePath());
            }
        }

        if(!stringBuilder.toString().isEmpty())
            PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_OPENED, stringBuilder.toString());

        if(!lastOpenedFiles.equals(stringBuilder.toString()))
            LOG.info("Updated last opened files from {} to {}", lastOpenedFiles, stringBuilder.toString());
    }


    // checks if the dir exists and if not creates it
    private static File checkDir() {
        File gbmDir = new File(".gbm/");
        if (gbmDir.exists() && gbmDir.isDirectory()) {
            return gbmDir;
        } else {
            if (gbmDir.mkdir()) {
                LOG.info("Created .gbm dir!");
                return gbmDir;
            } else {
                LOG.fatal("Could not create .gbm dir. Shutdown program!");
                Platform.exit();
            }
        }
        assert false;
        return null;
    }
}
