package org.tcelor.quarkus.api;

import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.runtime.Quarkus;

@QuarkusMain  
public class Main {

    public static void main(String ... args) {
        System.out.println("Launching app ...");
        Quarkus.run(args); 
    }
}