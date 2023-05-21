/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.youchat;

/**
 *
 * @author 2020338 - Douglas Santos
 */
import java.util.Random;

public enum GoodByes {
    GOODBYE1("Goodbye and take care!"),
    GOODBYE2("Farewell, my friend!"),
    GOODBYE3("Adios amigo!"),
    GOODBYE4("See you later, alligator!"),
    GOODBYE5("Take it easy and goodbye!"),
    GOODBYE6("Hasta la vista, baby!"),
    GOODBYE7("Catch you on the flip side!"),
    GOODBYE8("Toodle-oo, kangaroo!"),
    GOODBYE9("So long, and thanks for all the fish!"),
    GOODBYE10("Have a great day and goodbye!");

    private final String message;

    /**
     * Constructs a GoodByes ENUM constant with the specified goodbye message.
     *
     * @param message the goodbye message
     */
    GoodByes(String message) {
        this.message = message;
    }

    /**
     * Returns the goodbye message.
     *
     * @return the goodbye message
     */
    public String getMessage() {
        return message;
    }
}
