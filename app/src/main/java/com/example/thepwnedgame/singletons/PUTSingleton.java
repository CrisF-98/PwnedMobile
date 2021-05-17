package com.example.thepwnedgame.singletons;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PUTSingleton {
    private static PUTSingleton instance = null;
    private enum EngPuts{
        put1("Random passwords are the strongest! You can even use password generators!"),
        put2("Never use personal informations as your password."),
        put3("Avoid using words that can be found in a dictionary."),
        put4("A good practice can be using easy-to-remember phrase, then modify it using uppercase letters, lowercase letters as well as symbols."),
        put5("Passwords should be changed on a regular basis."),
        put6("Don't write passwords down. Use password managers, instead.");

        private final String put;
        private EngPuts(String s) {
            put = s;
        }

        public String toString(){
            return this.put;
        }
    }

    private enum ItaPuts{
        put1("Le password casuali sono le più forti! Si possono usare anche generatori casuali."),
        put2("Mai usare come password delle informazioni personali."),
        put3("Meglio evitare di usare parole che si possono trovare in un vocabolario"),
        put4("Una buona pratica può essere quella di usare frasi facili da ricordare e modificarle usando lettere maiuscole, minuscole così come simboli."),
        put5("Bisognerebbe cambiare le password a intervalli regolari."),
        put6("Non scrivere le password su carta. Meglio usare dei gestori.");

        private final String put;
        private ItaPuts(String s) {
            put = s;
        }

        public String toString(){
            return this.put;
        }
    }

    public static PUTSingleton getInstance(){
        if (instance == null) {
            instance = new PUTSingleton();
        }
        return instance;
    }

    public String getAPUT(){
        if (Locale.getDefault().getDisplayLanguage().equals("it")){
            int pick = new Random().nextInt(ItaPuts.values().length);
            return ItaPuts.values()[pick].toString();
        } else {
            int pick = new Random().nextInt(EngPuts.values().length);
            return EngPuts.values()[pick].toString();
        }
    }
}
