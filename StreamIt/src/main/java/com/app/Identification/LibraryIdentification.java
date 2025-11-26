package com.app.Identification;

public class LibraryIdentification {
    private static String[] SavedFileNames;

    public static String[] getSavedFileNames() {return SavedFileNames;}
    public static void setSavedFileNames(String[] savedFileNames) {SavedFileNames = savedFileNames;}


    private static boolean MoviesFlag;
    private static String[] Movies;

    public static String[] getMovies() {return Movies;}
    public static void setMovies(String[] movies) {Movies = movies;}

    private static boolean ShowsFlag;
    private static String[] Shows;

    public static String[] getShows() {return Shows;}
    public static void setShows(String[] shows) {Shows = shows;}

    private static String[] Recommended;

    public static String[] getRecommended() {return Recommended;}
    public static void setRecommended(String[] recommended) {Recommended = recommended;}




}
