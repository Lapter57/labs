package ru.spbstu.shakhmin.obfuscator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CommandLineObfuscator {

    private static final String MODE_OPTION= "mode";
    private static final String INPUT_OPTION = "input";
    private static final String OUTPUT_OPTION = "output";

    @NotNull
    private final Options options;

    @NotNull
    private final Map<String, String> optionToValue;

    @NotNull
    private final CommandLineParser parser;

    @NotNull
    private final HelpFormatter formatter;

    @NotNull
    private final Obfuscator xmlObfuscator;

    public CommandLineObfuscator(@NotNull final Obfuscator xmlObfuscator) {
        this.options = new Options();
        constructOptions();
        this.optionToValue = new HashMap<>();
        this.parser = new DefaultParser();
        this.formatter = new HelpFormatter();
        this.xmlObfuscator = xmlObfuscator;
    }

    private void constructOptions() {
        options.addOption(Option.builder("m")
                .argName("Mode")
                .longOpt("mode")
                .required()
                .hasArg()
                .desc(String.format("Mode (%s, %s)", Mode.OBFUSCATION.mode, Mode.UNOBFUSCATION.mode))
                .build());
        options.addOption(Option.builder("i")
                .argName("Input")
                .longOpt("input")
                .required()
                .hasArg()
                .desc("Input XML file")
                .build());
        options.addOption(Option.builder("o")
                .argName("Output")
                .longOpt("output")
                .required()
                .hasArg()
                .desc("Output XML file")
                .build());
    }

    private void parse(@NotNull final String... args) throws ParseException {
        final var cmd = parser.parse(options, args);
        optionToValue.put(MODE_OPTION, cmd.getOptionValue(MODE_OPTION));
        optionToValue.put(INPUT_OPTION, cmd.getOptionValue(INPUT_OPTION));
        optionToValue.put(OUTPUT_OPTION, cmd.getOptionValue(OUTPUT_OPTION));
    }

    public void process(@NotNull final String... args) {
        try {
            parse(args);
            final var inputXML = Files.readString(new File(optionToValue.get(INPUT_OPTION)).toPath());
            final var processedXML = Mode.OBFUSCATION == Mode.from(optionToValue.get(MODE_OPTION))
                    ? xmlObfuscator.obfuscate(inputXML)
                    : xmlObfuscator.unobfuscate(inputXML);
            final var doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(processedXML)));
            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(new DOMSource(doc), new StreamResult(new File(optionToValue.get(OUTPUT_OPTION))));
            final var modeName = Mode.from(optionToValue.get(MODE_OPTION)).name();
            log.info(String.format("%s was completed", modeName.charAt(0) + modeName.substring(1).toLowerCase()));
        } catch (Exception e) {
            log.error(String.format("Something went wrong. Reason: %s", e.getMessage()));
            formatter.printHelp("obfuscation", options);
        }
    }

    enum Mode {
        OBFUSCATION("obf"),
        UNOBFUSCATION("unobf");

        @NotNull
        private final String mode;

        Mode(@NotNull final String mode) {
            this.mode = mode;
        }

        @NotNull
        static Mode from(@NotNull final String value) {
            if (OBFUSCATION.mode.equals(value)){
                return OBFUSCATION;
            } else if (UNOBFUSCATION.mode.equals(value)) {
                return UNOBFUSCATION;
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
}
