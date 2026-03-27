package application.controller;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class PreferencesViewControllerTest {

    @Test
    void resolveStartupFilePath_KeepsTheCurrentValueWhenNoFileWasSelected() {
        assertThat(PreferencesViewController.resolveStartupFilePath(null, "/tmp/current.csv"))
                .isEqualTo("/tmp/current.csv");
    }

    @Test
    void resolveStartupFilePath_UsesTheSelectedAbsolutePath() {
        File selectedFile = new File("/tmp/chosen.csv");

        assertThat(PreferencesViewController.resolveStartupFilePath(selectedFile, "/tmp/current.csv"))
                .isEqualTo(selectedFile.getAbsolutePath());
    }
}
