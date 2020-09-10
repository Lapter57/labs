package ru.spbstu.shakhmin.obfuscator;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.InputSource;
import ru.spbstu.shakhmin.obfuscator.Obfuscator;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = XMLObfuscatorTestConfiguration.class)
class XMLObfuscatorTest {

    @Autowired
    private Obfuscator xmlObfuscator;

    private String xmlSource;

    private String obfuscatedXml;

    @BeforeEach
    public void init() throws IOException {
        final var classLoader = getClass().getClassLoader();
        final var file = new File(classLoader.getResource("test.xml").getFile()).toPath();
        xmlSource = Files.readString(file);
        obfuscatedXml = xmlObfuscator.obfuscate(xmlSource);
    }

    @Test
    public void obfuscate() throws Exception {
        assertFalse(compareXMLStrings(xmlSource, obfuscatedXml));
    }

    @Test
    public void unobfuscate() throws Exception {
        assertTrue(compareXMLStrings(xmlSource, xmlObfuscator.unobfuscate(obfuscatedXml)));
    }

    private boolean compareXMLStrings(@NotNull final String xml1,
                                      @NotNull final String xml2) throws Exception {
        final var dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        final var db = dbf.newDocumentBuilder();

        final var doc1 = db.parse(new InputSource(new StringReader(xml1)));
        doc1.normalizeDocument();

        final var doc2 = db.parse(new InputSource(new StringReader(xml2)));
        doc2.normalizeDocument();

        return doc1.isEqualNode(doc2);
    }
}