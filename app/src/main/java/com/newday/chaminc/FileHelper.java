package com.newday.chaminc;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHelper {

    public static void writeData(ArrayList<String> arrayList, Context context, int whichFragment){
        String filename = "";
        if(whichFragment==1) {
            filename = "list1";
        }
        else if (whichFragment == 11) {
            filename = "list1a";
        }
        else if (whichFragment == 12) {
            filename = "list1b";
        }
        else if (whichFragment == 13) {
            filename = "list1c";
        }
        else if (whichFragment == 14) {
            filename = "list1d";
        }
        else if (whichFragment == 15) {
            filename = "list1e";
        }
        else if (whichFragment == 16) {
            filename = "list1f";
        }
        else if (whichFragment == 17) {
            filename = "list1g";
        }
        else if (whichFragment == 18) {
            filename = "list1h";
        }
        else if (whichFragment == 19) {
            filename = "list1i";
        }
        else if (whichFragment == 111) {
            filename = "list1j";
        }
        else if (whichFragment == 2) {
            filename = "list2";
        }
        else if (whichFragment == 21) {
            filename = "list2a";
        }
        else if (whichFragment == 22) {
            filename = "list2b";
        }
        else if (whichFragment == 23) {
            filename = "list2c";
        }
        else if (whichFragment == 24) {
            filename = "list2d";
        }
        else if (whichFragment == 25) {
            filename = "list2e";
        }
        else if (whichFragment == 26) {
            filename = "list2f";
        }
        else if (whichFragment == 27) {
            filename = "list2g";
        }
        else if (whichFragment == 28) {
            filename = "list2h";
        }
        else if (whichFragment == 29) {
            filename = "list2i";
        }
        else if (whichFragment == 211) {
            filename = "list2j";
        }
        else if (whichFragment == 3) {
            filename = "list3";
        }
        else if (whichFragment == 4) {
            filename = "list4";
        }
        else if (whichFragment == 5) {
            filename = "list5";
        }
        else if (whichFragment == 6) {
            filename = "list6";
        }
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(arrayList);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readData (Context context, int whichFragment){
        String filename = "";
        if(whichFragment==1) {
            filename = "list1";
        }
        else if (whichFragment == 11) {
            filename = "list1a";
        }
        else if (whichFragment == 12) {
            filename = "list1b";
        }
        else if (whichFragment == 13) {
            filename = "list1c";
        }
        else if (whichFragment == 14) {
            filename = "list1d";
        }
        else if (whichFragment == 15) {
            filename = "list1e";
        }
        else if (whichFragment == 16) {
            filename = "list1f";
        }
        else if (whichFragment == 17) {
            filename = "list1g";
        }
        else if (whichFragment == 18) {
            filename = "list1h";
        }
        else if (whichFragment == 19) {
            filename = "list1i";
        }
        else if (whichFragment == 111) {
            filename = "list1j";
        }
        else if (whichFragment == 2) {
            filename = "list2";
        }
        else if (whichFragment == 21) {
            filename = "list2a";
        }
        else if (whichFragment == 22) {
            filename = "list2b";
        }
        else if (whichFragment == 23) {
            filename = "list2c";
        }
        else if (whichFragment == 24) {
            filename = "list2d";
        }
        else if (whichFragment == 25) {
            filename = "list2e";
        }
        else if (whichFragment == 26) {
            filename = "list2f";
        }
        else if (whichFragment == 27) {
            filename = "list2g";
        }
        else if (whichFragment == 28) {
            filename = "list2h";
        }
        else if (whichFragment == 29) {
            filename = "list2i";
        }
        else if (whichFragment == 211) {
            filename = "list2j";
        }
        else if (whichFragment == 3) {
            filename = "list3";
        }
        else if (whichFragment == 4) {
            filename = "list4";
        }
        else if (whichFragment == 5) {
            filename = "list5";
        }
        else if (whichFragment == 6) {
            filename = "list6";
        }
        ArrayList<String> itemsList = null;
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            itemsList = (ArrayList<String>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        return itemsList;
    }
}
