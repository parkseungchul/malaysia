package com.example;

public class TestMain {
    public static void main(String [] args){
        String str = "Improving your English can be done through various methods. Here are some suggestions:\n\n1. ";

        System.out.println(str.replaceAll("\n","<br>"));
    }
}
