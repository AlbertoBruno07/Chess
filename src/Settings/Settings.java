package Settings;

import netscape.javascript.JSObject;

import java.io.*;
import java.util.ArrayList;

public class Settings implements Serializable {
    private String selectedIconPackage;
    private String selectedSoundPackage;
    private static Settings instance = new Settings();
    
    private static ArrayList<String> possibleIconPacksAL;
    public static String[] possibleIconPacks;

    private static ArrayList<String> possibleSoundPacksAL;
    public static String[] possibleSoundPacks;

    public static void initializeSettings(){
        initializeInstance();

        possibleIconPacksAL = loadPossiblePacks("icons");
        possibleIconPacks = new String[possibleIconPacksAL.size()];
        for(int i = 0; i < possibleIconPacksAL.size(); i++)
            possibleIconPacks[i] = possibleIconPacksAL.get(i);

        possibleSoundPacksAL = loadPossiblePacks("sounds");
        possibleSoundPacks = new String[possibleSoundPacksAL.size()];
        for(int i = 0; i < possibleSoundPacksAL.size(); i++)
            possibleSoundPacks[i] = possibleSoundPacksAL.get(i);

        try{
            instance.validateSettings();
        } catch(InvalidSettingException ise){
            System.out.println(ise);
        }
    }

    private void validateSettings() {
        if(!possibleIconPacksAL.contains(selectedIconPackage)){
            selectedIconPackage = "Standard";
            throw new InvalidSettingException("[Settings] Invalid icon package. Using Standard package.");
        }
        if(!possibleSoundPacksAL.contains(selectedSoundPackage)){
            selectedSoundPackage = "Standard";
            throw new InvalidSettingException("[Settings] Invalid icon package. Using Standard package.");
        }
    }

    private static ArrayList<String> loadPossiblePacks(String targetPackage) {
        ArrayList<String> possiblePacksAL = new ArrayList<String>();
        try {
            BufferedReader in =new BufferedReader (new FileReader("./src/" + targetPackage + "/packages.jcp"));
            String line = in.readLine();
            while(line != null){
                possiblePacksAL.add(line);
                line = in.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("[Settings] Cannot read packages.jcp file");
        } catch (IOException e) {
            System.out.println("[Settings] Cannot read line");
        }
        //If sth went wrong, use the Standard package
        if(possiblePacksAL.isEmpty())
            possiblePacksAL.add("Standard");

        return possiblePacksAL;
    }

    public static void initializeInstance(){
        try{
            FileInputStream fis = new FileInputStream("./src/Settings/config.jcs");
            ObjectInputStream ois = new ObjectInputStream(fis);
            instance = (Settings) ois.readObject();
            ois.close();
            fis.close();
        } catch(Exception e){
            System.out.println("[Settings] Cannot read settings from file, loading default");
            initializeSettingsToDefault();
        }
    }

    public static void initializeSettingsToDefault(){
        instance.selectedIconPackage = "Standard";
    }

    public static String getSelectedIconPackage() {
        return instance.selectedIconPackage;
    }

    public static void setSelectedIconPackage(String selectedIconPackage) {
        instance.selectedIconPackage = selectedIconPackage;
    }

    public static String getSelectedSoundPackage(){
        return instance.selectedSoundPackage;
    }

    public static void setSelectedSoundPackage(String selectedSoundPackage){
        instance.selectedSoundPackage = selectedSoundPackage;
    }

    public static Settings getInstance() {
        return instance;
    }

    public static void saveToFile(){
        try {
            FileOutputStream fout = new FileOutputStream("./src/Settings/config.jcs");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(instance);
            oos.flush();
            oos.close();
            fout.close();
        } catch(IOException ioe){
            System.out.println("[Settings] Problem saving" + ioe);
        }
    }
}
