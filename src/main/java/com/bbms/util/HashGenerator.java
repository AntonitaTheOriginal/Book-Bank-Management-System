package com.bbms.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "Admin@123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println(hash);
    }
}
