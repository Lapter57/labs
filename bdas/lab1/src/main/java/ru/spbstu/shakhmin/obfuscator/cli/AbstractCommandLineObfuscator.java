package ru.spbstu.shakhmin.obfuscator.cli;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.jetbrains.annotations.NotNull;
import ru.spbstu.shakhmin.obfuscator.Obfuscator;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommandLineObfuscator<O extends Obfuscator>
        implements CommandLineObfuscator {

    @NotNull
    protected final O obfuscator;

    @NotNull
    protected final Options options;

    @NotNull
    protected final Map<String, String> optionToValue;

    @NotNull
    protected final CommandLineParser parser;

    protected AbstractCommandLineObfuscator(@NotNull final O obfuscator) {
        this.obfuscator = obfuscator;
        this.options = new Options();
        constructOptions();
        this.optionToValue = new HashMap<>();
        this.parser = new DefaultParser();
    }

    protected abstract void constructOptions();
}
