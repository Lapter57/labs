package ru.spbstu.shakhmin.obfuscator.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import ru.spbstu.shakhmin.obfuscator.XMLObfuscator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Map;

@Component
public class CommandLineXMLObfuscator extends AbstractCommandLineObfuscator<XMLObfuscator> {

    private static final String INPUT_OPTION = "input";
    private static final String OUTPUT_OPTION = "output";

    @NotNull
    private final Transformer transformer;

    public CommandLineXMLObfuscator(@NotNull final XMLObfuscator xmlObfuscator)
            throws TransformerConfigurationException {
        super(xmlObfuscator);
        this.transformer = TransformerFactory.newInstance().newTransformer();
    }

    @Override
    protected void addOptions(@NotNull final Options options) {
        options.addOption(Option.builder("i")
                .argName(INPUT_OPTION)
                .longOpt(INPUT_OPTION)
                .required()
                .hasArg()
                .desc("Input XML file")
                .build());
        options.addOption(Option.builder("o")
                .argName(OUTPUT_OPTION)
                .longOpt(OUTPUT_OPTION)
                .required()
                .hasArg()
                .desc("Output XML file")
                .build());
    }

    @NotNull
    @Override
    protected String getSource(@NotNull final Map<String, String> optionToValue) throws IOException {
        return Files.readString(new File(optionToValue.get(INPUT_OPTION)).toPath());
    }

    @Override
    protected void postProcess(@NotNull final String processedSource,
                               @NotNull final Map<String, String> optionToValue) throws Exception {
        final var doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(processedSource)));
            doc.normalizeDocument();
            transformer.transform(
                    new DOMSource(doc),
                    new StreamResult(new File(optionToValue.get(OUTPUT_OPTION))));
    }
}
