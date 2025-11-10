package src.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import src.model.*;
public class FileRepository {

    private List<Customer> listCustomers;
    private List<Product> products;

    File customerFile, productFile, invoiceFile;

    // ctor
    public FileRepository(String customerFilePath, String productFilePath, String invoiceFilePath) {
        customerFile = new File(customerFilePath);
        productFile = new File(productFilePath);
        invoiceFile = new File(invoiceFilePath);
    }
    
    // parse KH.TXT
    public List<Customer> loadCustomers() throws IOException {
        listCustomers = new ArrayList<>();
        Files.lines(customerFile.toPath()).forEach(line -> {
            String[] p = line.split("\\|");
            if (p.length >= 4) {
            int id = Integer.parseInt(p[0]);
            String name = p[1];
            String addr = p[2];
            CustomerGroup g = CustomerGroup.valueOf(p[3]);
            listCustomers.add(new Customer(id, name, addr, g));
            } else {
            // xử lý dòng lỗi: log / show message
            }
        });
        return listCustomers;
    }

    public void saveCustomer(Customer c) throws IOException {
        String line = String.format("%05d|%s|%s|%s%n",
                    c.getId(), c.getName(), c.getAddress(), c.getGroup().name());
        Files.write(customerFile.toPath(), line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public int nextCustomerId() {
        return listCustomers.stream().mapToInt(Customer::getId).max().orElse(0) + 1;
    }


    // load product
    public List<Product> loadProducts() throws IOException {
        products = new ArrayList<>();
        Files.lines(productFile.toPath()).forEach(line -> {
            String[] p = line.split("\\|");
            if (p.length >= 3) {
                int id = Integer.parseInt(p[0]);
                String name = p[1];
                double price = Double.parseDouble(p[2]);
                Product prod = new Product(id, name, price);
                products.add(prod);
            } else {
            // xử lý dòng lỗi: log / show message
            }
        });
        return products;
    }
    public void saveProduct(Product p) throws IOException {
        String line = String.format("%05d|%s|%.2f%n",
                    p.getId(), p.getName(), p.getPrice());
        Files.write(productFile.toPath(), line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    public int nextProductId() throws IOException {
        return products.stream().mapToInt(Product::getId).max().orElse(0) + 1;
    }
    public Product getProductById(int id) {
        for (Product p : products) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}
