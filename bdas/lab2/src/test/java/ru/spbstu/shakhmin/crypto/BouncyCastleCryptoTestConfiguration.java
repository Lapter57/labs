package ru.spbstu.shakhmin.crypto;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan({"ru.spbstu.shakhmin.config", "ru.spbstu.shakhmin.crypto"})
public class BouncyCastleCryptoTestConfiguration {
}
