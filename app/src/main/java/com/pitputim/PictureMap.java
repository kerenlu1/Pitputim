package com.pitputim;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A static class that contains the communication board gallery data.
 * In use for displaying the communication board images and audio.
 */

public class PictureMap {

    static List<String> emotions = Arrays.asList("angry", "ashamed", "ashamed_f", "boring",
            "confused", "dislike", "excited", "happy", "happy_f", "hungry", "love", "romantic",
            "romantic_f", "sad", "sad_f", "scared", "scared_f", "surprised", "tired", "to_cry",
            "to_cry_f", "to_feel_sick", "to_laugh", "to_laugh_f");
    static List<String> food = Arrays.asList("apple", "biscuits", "cake", "cereals", "chicken",
            "chicken_nuggets", "chocolate", "coffee", "fried_potatoes", "hamburger", "juice",
            "meat_balls", "milk", "milkshake", "omelette", "orange", "pizza", "popcorn", "rice",
            "salad", "sandwich", "soup", "spaghetti", "tea");
    static List<String> clothing = Arrays.asList("boots", "cap", "flip_flops", "gloves", "hat",
            "jacket", "jeans", "long_shirt", "long_t_shirt", "raincoat", "sandals", "scarf",
            "shirt", "shoes", "shorts", "skirt", "socks", "sweat_shirt", "sweater", "swim_suit",
            "t_shirt", "trousers", "underpants", "undershirt");
    static List<String> general = Arrays.asList("bad", "congratulations", "family", "friend",
            "friend_f", "friends", "fun", "good", "good_afternoon", "good_day", "good_night",
            "goodbye", "happy_birthday", "hello", "home", "i", "i_f", "i_want", "like",
            "nice_to_meet", "school", "thanks", "they", "to_clap", "to_embrace", "unlike", "we",
            "we_f", "you", "you_p");
    static List<String> verbs = Arrays.asList("to_brush_ones_teeth", "to_clean", "to_comb",
            "to_cook", "to_dance", "to_dress", "to_drink", "to_eat", "to_go", "to_hear", "to_jump",
            "to_paint", "to_phone", "to_play", "to_read", "to_rest", "to_run", "to_see",
            "to_shower", "to_sing", "to_sleep", "to_speak", "to_swim", "to_write");

    static Map<String, List> map = new HashMap<>();
    static {
        map.put("emotions", emotions);
        map.put("food", food);
        map.put("clothing", clothing);
        map.put("general", general);
        map.put("verbs", verbs);
    }
}
