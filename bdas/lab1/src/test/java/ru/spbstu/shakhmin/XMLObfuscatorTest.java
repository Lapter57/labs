package ru.spbstu.shakhmin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XMLObfuscatorTest {

    private final Obfuscator xmlObfuscator = XMLObfuscator.create();

    private String xmlSource;

    private String obfuscatedXml;

    @BeforeEach
    public void init() throws IOException {
        final var classLoader = getClass().getClassLoader();
        final var file = new File(classLoader.getResource("test.xml").getFile());
        xmlSource = Files.readString(file.toPath());
        obfuscatedXml = xmlObfuscator.obfuscate(xmlSource);
    }

    @Test
    public void obfuscate() {
        assertNotEquals(xmlSource, obfuscatedXml);
    }

    @Test
    public void unobfuscate() throws Exception {
        final var dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        final var db = dbf.newDocumentBuilder();

        final var doc1 = db.parse(new InputSource(new StringReader(xmlSource)));
        doc1.normalizeDocument();

        final var doc2 = db.parse(new InputSource(new StringReader(xmlObfuscator.unobfuscate(obfuscatedXml))));
        doc2.normalizeDocument();

        assertTrue(doc1.isEqualNode(doc2));
    }
}