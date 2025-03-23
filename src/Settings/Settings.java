package Settings;

import netscape.javascript.JSObject;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Settings implements Serializable {
    private String selectedIconPackage;
    private String selectedSoundPackage;
    private Color color1, color2, color3, color4;
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
            getInstance().validateSettings();
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
        getInstance().selectedIconPackage = "Standard";
        getInstance().selectedSoundPackage = "Standard";
        getInstance().color1 = Color.DARK_GRAY;
        getInstance().color2 = Color.WHITE;
        getInstance().color3 = Color.GREEN;
        getInstance().color4 = Color.CYAN;
    }

    public static String getSelectedIconPackage() {
        return getInstance().selectedIconPackage;
    }

    public static void setSelectedIconPackage(String selectedIconPackage) {
        getInstance().selectedIconPackage = selectedIconPackage;
    }

    public static String getSelectedSoundPackage(){
        return getInstance().selectedSoundPackage;
    }

    public static void setSelectedSoundPackage(String selectedSoundPackage){
        getInstance().selectedSoundPackage = selectedSoundPackage;
    }

    public static void setColor1(Color c){
        getInstance().color1 = c;
    }

    public static void setColor2(Color c){
        getInstance().color2 = c;
    }

    public static void setColor3(Color c){
        getInstance().color3 = c;
    }

    public static void setColor4(Color c){
        getInstance().color4 = c;
    }

    public static Color getColor1(){
        return getInstance().color1;
    }

    public static Color getColor2(){
        return getInstance().color2;
    }

    public static Color getColor3(){
        return getInstance().color3;
    }

    public static Color getColor4(){
        return getInstance().color4;
    }

    public static Settings getInstance() {
        return instance;
    }

    public static void saveToFile(){
        try {
            FileOutputStream fout = new FileOutputStream("./src/Settings/config.jcs");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(getInstance());
            oos.flush();
            oos.close();
            fout.close();
        } catch(IOException ioe){
            System.out.println("[Settings] Problem saving" + ioe);
        }
    }
}
