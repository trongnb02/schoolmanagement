package com.example.schoolmnt.sm.registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        //TODO: regex to check valid format email you want
        return true;
    }
}
