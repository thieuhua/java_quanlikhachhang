package src.model;
public class Customer {
    int id;         // 5-digit auto-increment
    String name;
    String address;
    CustomerGroup group;

    // ctor
    public Customer(int id, String name, String address, CustomerGroup group) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.group = group;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public CustomerGroup getGroup() {
        return group;
    }
}


