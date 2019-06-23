package com.zq.learn.netty;

import java.util.Enumeration;
import java.util.Properties;

public class Test {
    public static void main(String[] args) {
        /*Properties properties = System.getProperties();
        Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            System.out.println(key + "=" + properties.get(key));
        }*/
        System.out.println(System.getProperty("line.separator").length());
    }
}
