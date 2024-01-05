package com.kuzetech.bigdata.lab.exception;

public class App {
    public static void main(String[] args) {
        try {
            Exception e = new NullPointerException();
            throw e;
        } catch (NullPointerException e) {
            System.out.println(1);
        } catch (Exception e) {
            System.out.println(2);
        }


        try {
            throw new RuntimeException("test");
        } catch (Exception e) {
            System.out.println(3);
        }
    }
}
