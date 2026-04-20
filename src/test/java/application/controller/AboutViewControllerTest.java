package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class AboutViewControllerTest {

  @Test
  void readBundledText_ReturnsTheUtf8Content() throws IOException {
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream("Version 0.6.2".getBytes(StandardCharsets.UTF_8));

    assertThat(AboutViewController.readBundledText(inputStream)).isEqualTo("Version 0.6.2");
  }
}
