package ru.spbstu.shakhmin.obfuscator.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;
import ru.spbstu.shakhmin.obfuscator.Obfuscator;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractCommandLineObfuscator<O extends Obfuscator> implements CommandLineObfuscator {

    private static final String MODE_OPTION = "mode";

    @NotNull
    private final O obfuscator;

    @NotNull
    private final Options options;

    @NotNull
    private final CommandLineParser parser;

    @NotNull
    private final HelpFormatter formatter;

    @NotNull
    private final Map<String, String> optionToValue;

    protected AbstractCommandLineObfuscator(@NotNull final O obfuscator) {
        this.obfuscator = obfuscator;
        this.parser = new DefaultParser();
        this.formatter = new HelpFormatter();
        this.optionToValue = new HashMap<>();
        this.options = new Options();
        addOptions();
        addOption("m", MODE_OPTION,
                String.format("Mode (%s, %s)", Mode.OBFUSCATION.mode, Mode.UNOBFUSCATION.mode), true);
    }

    protected void addOption(@NotNull final String opt,
                             @NotNull final String longOpt,
                             @NotNull final String desc,
                             final boolean required) {
        options.addOption(Option.builder(opt)
                .argName(longOpt)
                .longOpt(longOpt)
                .required(required)
                .hasArg()
                .desc(desc)
                .build());
    }


    private void parse(@NotNull final String... args) throws ParseException {
        final var cmd = parser.parse(options, args);
        for (final var option : options.getOptions()) {
            final var optionName = option.getLongOpt();
            if (cmd.hasOption(optionName)) {
                optionToValue.computeIfAbsent(optionName, k -> cmd.getOptionValue(optionName));
            }
        }
    }

    @Override
    public void process(@NotNull final String... args) {
        try {
            parse(args);
            final var source = getSource(optionToValue);
            final var processedSource = Mode.OBFUSCATION == Mode.from(optionToValue.get(MODE_OPTION))
                        ? obfuscator.obfuscate(source)
                        : obfuscator.unobfuscate(source);
            postProcess(processedSource, optionToValue);
            final var modeName = Mode.from(optionToValue.get(MODE_OPTION)).name();
            log.info(String.format("%s was completed", modeName.charAt(0) + modeName.substring(1).toLowerCase()));
        } catch (Exception e) {
            log.error(e.getMessage());
            formatter.printHelp("Obfuscation", options);
        }
    }

    protected abstract void postProcess(@NotNull final String processedSource,
                                        @NotNull final Map<String, String> optionToValue) throws Exception;

    protected abstract void addOptions();

    @NotNull
    protected abstract String getSource(@NotNull final Map<String, String> optionToValue) throws Exception;

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
