package be.howest.ti.game.logic;

public class Info {
    private static final String[] AUTHORS = {"Lohani Pratik",
            "Taris Wouter",
            "Van Mulders Michiel",
            "Vermeulen Sebastiaan",
            "Windens Jitse"};

    protected Info(){
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }


    public static String[] getAuthors() {
        return AUTHORS;
    }
}
