package Shipments.Parcels;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class containing all the saved parcels.
 */
public class Archive implements Serializable {
    private HashMap<String, ArrayList<Parcel>> parcels;
    private String parcelsFile;

    /**
     * Constructor for the class.
     * @param parcelsFile the path to the file
     */
    public Archive(String parcelsFile) {
        this.parcelsFile = parcelsFile;
        this.parcels = new HashMap<>();
    }

    /**
     * Tries to load the parcels stored on disk.
     * @throws FileNotFoundException if no such file is found or it cannot be read
     */
    public void loadParcels() throws FileNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(parcelsFile)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Archive parcelList= (Archive) objectInputStream.readObject();
            this.parcels = parcelList.getParcels();
        } catch (IOException | ClassNotFoundException e) {
            throw new FileNotFoundException();
        }
    }

    /**
     * Returns all the parcels.
     * @return all the parcels
     */
    public HashMap<String, ArrayList<Parcel>> getParcels() {
        return parcels;
    }

    /**
     * Saves all the parcels to disk.
     * @return if the save was successful
     */
    public boolean saveArchive() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(parcelsFile)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            System.out.println("saved archive to disk");
        } catch (IOException e) {
            System.out.println("unable to save archive");
            return false;
        }
        return true;
    }

    /**
     * Adds a parcel to the list of all received parcels.
     * @param parcel the parcel to add
     */
     public void addParcel(Parcel parcel) {
        if(parcels.containsKey(parcel.getSource()))
            parcels.get(parcel.getSource()).add(parcel);
        else {
            ArrayList<Parcel> parcelsFromSender = new ArrayList<>();
            parcelsFromSender.add(parcel);
            parcels.put(parcel.getSource(), parcelsFromSender);
        }
         System.out.println("added parcel to archive in memory");
     }
}
