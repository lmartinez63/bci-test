package com.nisum.bci.service;

public interface PasswordService {

    String encode(String raw);

    boolean matches(String raw, String encoded);
}
