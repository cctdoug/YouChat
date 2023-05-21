/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.youchat;

import java.util.Random;

/**
 *
 * @author 2020338 - Douglas Santos
 */
/**
 * Generates a random goodbye message from the GoodByes ENUM.
 *
 * @return a random goodbye message
 */
public class GoodbyeMessages {

    public String getMessage() {
        // Get all ENUMS from GoodByes
        GoodByes[] phrases = GoodByes.values();
        Random rand = new Random();
        // Generate a random index within the range of ENUMS
        int index = rand.nextInt(phrases.length);
        // Return the goodbye message associated with the randomly selected ENUM 
        return phrases[index].getMessage();
    }
}
