package src.model;



public class Product {
    int id;         // 5-digit auto-increment
    String name;
    double price;   // >= 0
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price =price;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
}
