package Person;

import java.io.Serializable;

/**
 * Represents a person who is allowed to connect to a Harbor. A Person has a name, an IP and may be an admin allowed
 * to execute commands on the server and retrieve Parcels, or not.
 */
public class Person implements Serializable {
    private String name;
    private String ip;
    private boolean isAdmin;

    /**
     * Returns if this person is an admin or not.
     * @return true if the person is an admin, otherwise false.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Constructor for the class.
     * @param name the name
     * @param ip the IP address, either local or external (for example 192.168.0.19 or 145.239.168.2)
     * @param isAdmin if this person is an admin or not
     */
    public Person(String name, String ip, boolean isAdmin) {
        this.name = name;
        this.ip = ip;
        this.isAdmin = isAdmin;
    }

    /**
     * Returns the IP address of this Person.
     * @return the IP address of this Person
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the name of this Person.
     * @return the name of this Person
     */
    public String getName() {
        return name;
    }

    /**
     * Returns if this Person is equal to another Person.
     * @param other the Person to compare with
     * @return true if this Person and the other Person has the same IP address
     */
    @Override
    public boolean equals(Object other) {
        if(other instanceof Person) {
            return this.ip.equals(((Person) other).ip);
        } else
            return false;
    }

    /**
     * Returns the hashcode of this Person.
     * @return the hashcode of this Person
     */
    @Override
    public int hashCode() {
        return ip.hashCode();
    }

    /**
     * Returns a String representing this Person.
     * @return this Person's name and IP.
     */
    @Override
    public String toString() {
        return name + " - " + ip;
    }
}
