package com.weyland.bishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Главный класс для запуска эмулятора "Бишоп".

@SpringBootApplication
public class BishopPrototypeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BishopPrototypeApplication.class, args);
    }

}