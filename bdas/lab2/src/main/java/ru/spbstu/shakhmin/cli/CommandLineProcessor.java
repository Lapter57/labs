package ru.spbstu.shakhmin.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.spbstu.shakhmin.crypto.BouncyCastleCrypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CommandLineProcessor {

    private static final String MODE_OPTION = "mode";
    private static final String INPUT_OPTION = "input";
    private static final String OUTPUT_OPTION = "output";
    private static final String KEY_ALIAS_OPTION = "alias";
    private static final String KEY_PASSWORD_OPTION = "password";

    @NotNull
    private final BouncyCastleCrypto bouncyCastleCrypto;

    @NotNull
    private final Options options;

    @NotNull
    private final CommandLineParser parser;

    @NotNull
    private final HelpFormatter formatter;

    @NotNull
    private final Map<String, String> optionToValue;

    public CommandLineProcessor(@NotNull final BouncyCastleCrypto bouncyCastleCrypto) {
        this.bouncyCastleCrypto = bouncyCastleCrypto;
        this.parser = new DefaultParser();
        this.formatter = new HelpFormatter();
        this.optionToValue = new HashMap<>();
        this.options = new Options();
        addOptions();
    }

    private void addOptions() {
        addOption("m", MODE_OPTION, String.format("Mode (%s, %s, %s, %s)",
                Mode.ENCRYPTION.mode, Mode.DECRYPTION.mode, Mode.SIGNATURE.mode, Mode.VERIFICATION.mode), true);
        addOption("i", INPUT_OPTION, "Input file", true);
        addOption("o", OUTPUT_OPTION, String.format("Output file (Not required for %s)", Mode.VERIFICATION.mode), false);
        addOption("a", KEY_ALIAS_OPTION,
                String.format("Alias of key (Required for %s and %s)", Mode.DECRYPTION.mode, Mode.SIGNATURE.mode), false);
        addOption("p", KEY_PASSWORD_OPTION,
                String.format("Password of key (Required for %s and %s)", Mode.DECRYPTION.mode, Mode.SIGNATURE.mode), false);
    }

    private void addOption(@NotNull final String opt,
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

    private boolean checkKeyOptions(@Nullable final String keyAlias,
                                    @Nullable final String keyPassword,
                                    @NotNull final CryptoProcessor processor) throws Exception {
        if (keyAlias != null && keyPassword != null) {
            saveToFile(processor.process());
            return true;
        }
        log.error(String.format(
                "%s option and %s option are required",
                KEY_ALIAS_OPTION, KEY_PASSWORD_OPTION));
        return false;
    }

    private void saveToFile(@NotNull final byte[] data) throws IOException {
        Files.write(Path.of(optionToValue.get(OUTPUT_OPTION)), data);
    }

    public void process(@NotNull final String... args) {
        try {
            parse(args);
            final var msg = Files.readAllBytes(Path.of(optionToValue.get(INPUT_OPTION)));
            final var keyAlias = optionToValue.get(KEY_ALIAS_OPTION);
            final var keyPassword = optionToValue.get(KEY_PASSWORD_OPTION);
            boolean success = true;
            switch (Mode.from(optionToValue.get(MODE_OPTION))) {
                case ENCRYPTION:
                    saveToFile(bouncyCastleCrypto.encryptData(msg));
                    break;
                case DECRYPTION:
                    success = checkKeyOptions(keyAlias, keyPassword, () ->
                            bouncyCastleCrypto.decryptData(msg, keyAlias, keyPassword));
                    break;
                case SIGNATURE:
                    success = checkKeyOptions(keyAlias, keyPassword, () ->
                                    bouncyCastleCrypto.signData(msg, keyAlias, keyPassword));
                    break;
                case VERIFICATION:
                    log.info(String.format("Verification: %s", bouncyCastleCrypto.verifySignData(msg)));
                    break;
            }
            final var modeName = Mode.from(optionToValue.get(MODE_OPTION)).name();
            log.info(String.format("%s was %s",
                    modeName.charAt(0) + modeName.substring(1).toLowerCase(),
                    success ? "completed" : "failed"));
        } catch (Exception e) {
            log.error(e.getMessage());
            formatter.printHelp("BouncyCastleCrypto", options);
        }
    }

    @FunctionalInterface
    private interface CryptoProcessor {
        byte[] process() throws Exception;
    }

    enum Mode {
        ENCRYPTION("encr"),
        DECRYPTION("decr"),
        SIGNATURE("sign"),
        VERIFICATION("verif");

        @NotNull
        private final String mode;

        Mode(@NotNull final String mode) {
            this.mode = mode;
        }

        @NotNull
        static Mode from(@NotNull final String value) {
            switch (value) {
                case "encr":
                    return ENCRYPTION;
                case "decr":
                    return DECRYPTION;
                case "sign":
                    return SIGNATURE;
                case "verif":
                    return VERIFICATION;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + value);
            }
        }
    }
}
