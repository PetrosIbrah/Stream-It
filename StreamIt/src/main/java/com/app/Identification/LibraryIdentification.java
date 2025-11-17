package com.app.Identification;

public class LibraryIdentification {
    private static String[] SavedFileNames;

    public static void Init (int ImageCount) {
        SavedFileNames = new String[ImageCount];
    }
    public static String[] getSavedFileNames() {return SavedFileNames;}

    public static void setSavedFileNames(String[] savedFileNames) {SavedFileNames = savedFileNames;}

    public static void AddSavedFile (int i, String SavedFileName) {
        SavedFileNames[i] = SavedFileName;
    }
}
