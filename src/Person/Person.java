package Person;

import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private String ip;

    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public Person(String name, String ip, boolean isAdmin) {
        this.name = name;
        this.ip = ip;
        this.isAdmin = isAdmin;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Person) {
            return this.ip.equals(((Person) other).ip);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return ip.hashCode();
    }

    @Override
    public String toString() {
        return name + " - " + ip;
    }
}
