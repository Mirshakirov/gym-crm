package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.facade.GymFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class AppTest {
1
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

            ConsoleMenu menu = new ConsoleMenu(context.getBean(GymFacade.class));

            menu.start();
        }
    }
}