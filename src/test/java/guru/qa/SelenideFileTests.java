package guru.qa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class SelenideFileTests {

//    static {
//        Configuration.fileDownload = FileDownloadMode.PROXY;
//    }

    @Test
    void downloadFileTest() throws IOException {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloaded = $("a[data-testid='raw-button']").download();

        try (InputStream is = new FileInputStream(downloaded)) {
            byte[] data = is.readAllBytes();
            String dataString = new String(data, StandardCharsets.UTF_8);
            Assertions.assertTrue(dataString.contains("Contributions to JUnit 5 are both welcomed and appreciated"));
        }
    }

    @Test
    void uploadFileTest() {
        open("https://tellibus.com/fineuploader/#demo");
        $("input[type='file']").uploadFromClasspath("wc.jpg");
        $(".qq-upload-file").shouldHave(text("wc.jpg"));

    }
}
