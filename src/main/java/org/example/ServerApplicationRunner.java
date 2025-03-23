package org.example;

import org.example.core.ServerAdministrator;

public class ServerApplicationRunner {
    
    public static void main(String[] args) {

        ServerAdministrator controller = new ServerAdministrator();
        controller.run();
    }
}