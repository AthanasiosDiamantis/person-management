package com.saki.personmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonManagementApplication {

    public static void main(String[] args) {
        // Temporary hash generation - remove after use!
        // Temporäre Hash-Generierung - danach entfernen!
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println("admin123: " + encoder.encode("admin123"));
//        System.out.println("user123: " + encoder.encode("user123"));

        SpringApplication.run(PersonManagementApplication.class, args);
    }

}
