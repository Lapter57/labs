package ru.spbstu.shakhmin;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({XMLObfuscator.class})
public class XMLObfuscatorTestConfig {
}
