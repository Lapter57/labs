package ru.spbstu.shakhmin;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.spbstu.shakhmin.obfuscator.XMLObfuscator;

@Configuration
@Import({XMLObfuscator.class})
public class XMLObfuscatorTestConfig {
}
