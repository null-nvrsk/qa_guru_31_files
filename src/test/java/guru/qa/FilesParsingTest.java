package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import guru.qa.models.Glossary;
import guru.qa.models.GlossaryInner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class FilesParsingTest {

    private ClassLoader cl = FilesParsingTest.class.getClassLoader();
    private static final Gson gson = new Gson();

    @Test
    void pdfFileParsingTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File downloaded = $("a[href*='.pdf']").download();

        PDF pdf = new PDF(downloaded);
        Assertions.assertEquals("JUnit 5 User Guide", pdf.title);
    }

    @Test
    void xlsFileParsingTest() throws Exception {
        open("https://cbr.ru/oper_br/iro/");
        File downloaded = $("a[href*='rates_table.xlsx']").download();
        XLS xls = new XLS(downloaded);

        String actualValue = xls.excel.getSheet("2024").getRow(3).getCell(5).toString();

        Assertions.assertTrue(actualValue.contains("17.0"));
    }

    @Test
    void csvFileParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("qa_guru.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(is))) {

            List<String[]> data = reader.readAll();
            Assertions.assertEquals(2, data.size());
            Assertions.assertArrayEquals(
                    new String[] {"Tuchs", "Files"},
                    data.get(0)
            );
            Assertions.assertArrayEquals(
                    new String[] {"Vasenkov", "REST Assured"},
                    data.get(1)
            );

//            assertThat(data.get(0)[1]).contains("lesson");
        }
    }

    @Test
    void zipParseTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("example.txt.zip");
                ZipInputStream zis = new ZipInputStream(resource);
        ) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                System.out.println(entry.getName());
//             assertThat(entry.getName()).isEqualTo("example.txt");
            }
        }
    }

    @Test
    void jsonFileParsingTest() throws Exception {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(
                cl.getResourceAsStream("glossary.json")
        )) {
            JsonObject actual = gson.fromJson(reader, JsonObject.class);

            Assertions.assertEquals("example glossary", actual.get("title").getAsString());
            Assertions.assertEquals(23423, actual.get("ID").getAsInt());

            JsonObject inner = actual.get("Glossary").getAsJsonObject();

            Assertions.assertEquals("S", inner.get("title").getAsString());
            Assertions.assertTrue(inner.get("flag").getAsBoolean());

//            assertThat(actual.get("title").getAsString()).isEqualTo("example glossary");
//
//            assertThat(actual.get("Glossary").getAsJsonObject()
//                    .get("title").getAsString())
//                    .isEqualTo("S");
//
//            assertThat(actual
//                    .get("GlossDiv").getAsJsonObject()
//                    .get("flag").getAsBoolean())
//                    .isTrue();
        }
    }

    @Test
    void jsonFileParsingImprovedTest() throws Exception {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(
                cl.getResourceAsStream("glossary.json")
        )) {
            Glossary actual = gson.fromJson(reader, Glossary.class);

            Assertions.assertEquals("example glossary", actual.getTitle());
            Assertions.assertEquals(23423, actual.getId());

            Assertions.assertEquals("S", actual.getGlossary().getTitle());
            Assertions.assertTrue(actual.getGlossary().getFlag());
        }
    }
}
