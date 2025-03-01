package Settings;

import netscape.javascript.JSObject;

import java.io.*;
import java.util.ArrayList;

public class Settings implements Serializable {
    private String selectedIconPackage;
    private static Settings instance = new Settings();
    
    private static ArrayList<String> possibleIconPacksAL;
    public static String[] possibleIconPacks;

    public static void initializeSettings(){
        initializeInstance();
        loadPossibleIconPacks();
        try{
            instance.validateSettings();
        } catch(InvalidSettingException ise){
            System.out.println(ise);
        }
    }

    private void validateSettings() {
        if(!possibleIconPacksAL.contains(selectedIconPackage)){
            selectedIconPackage = "Standard";
            throw new InvalidSettingException("Invalid icon package. Using Standard package.");
        }
    }

    private static void loadPossibleIconPacks() {
        possibleIconPacksAL = new ArrayList<String>();
        try {
            BufferedReader in =new BufferedReader (new FileReader("./src/icons/packages.jcp"));
            String line = in.readLine();
            while(line != null){
                possibleIconPacksAL.add(line);
                line = in.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("[Settings] Cannot read packages.jcp file");
        } catch (IOException e) {
            System.out.println("[Settings] Cannot read line");
        }
        //If sth went wrong, use the Standard package
        if(possibleIconPacksAL.isEmpty())
            possibleIconPacksAL.add("Standard");

        possibleIconPacks = new String[possibleIconPacksAL.size()];
        for(int i = 0; i < possibleIconPacksAL.size(); i++)
            possibleIconPacks[i] = possibleIconPacksAL.get(i);
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
