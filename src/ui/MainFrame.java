package src.ui;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

import src.model.*;
import src.io.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    // Customer Tab Components
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JTextField tfCustomerName, tfCustomerAddress;
    private JComboBox<String> cbCustomerGroup;
    private JButton btnAddCustomer, btnSaveCustomer;

    // Product Tab Components
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField tfProductName, tfProductPrice;
    private JButton btnAddProduct, btnSaveProduct;

    // Invoice Tab Components
    private JComboBox<Customer> cbInvoiceCustomer;
    private JTable invoiceItemTable;
    private DefaultTableModel invoiceItemTableModel;
    private JComboBox<Product> cbInvoiceProduct;
    private JTextField tfInvoiceQuantity;
    private JButton btnAddInvoiceItem, btnSaveInvoice;

    // Report Tab Components
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JComboBox<String> cbSortOption;
    private JButton btnLoadReport;
    //
    FileRepository fileRepository;


    public MainFrame() {
        setTitle("Quản lý bán hàng");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Khách hàng", createCustomerPanel());
        tabbedPane.addTab("Mặt hàng", createProductPanel());
        tabbedPane.addTab("Hóa đơn", createInvoicePanel());
        tabbedPane.addTab("Báo cáo", createReportPanel());

        add(tabbedPane);
    }

    // --------------------------- CUSTOMER PANEL -----------------------------
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] cols = {"Mã KH", "Họ tên", "Địa chỉ", "Nhóm"};
        customerTableModel = new DefaultTableModel(cols, 0);
        customerTable = new JTable(customerTableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Form Input
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfCustomerName = new JTextField(15);
        tfCustomerAddress = new JTextField(15);
        cbCustomerGroup = new JComboBox<>(new String[]{"MUA_LE", "MUA_BUON", "MUA_QUA_MANG"});
        btnAddCustomer = new JButton("Thêm KH");
        btnSaveCustomer = new JButton("Lưu KH");

        formPanel.add(new JLabel("Họ tên:"));
        formPanel.add(tfCustomerName);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(tfCustomerAddress);
        formPanel.add(new JLabel("Nhóm:"));
        formPanel.add(cbCustomerGroup);
        formPanel.add(btnAddCustomer);
        formPanel.add(btnSaveCustomer);

        panel.add(formPanel, BorderLayout.NORTH);

        // Action Listeners
        btnAddCustomer.addActionListener(e -> addCustomerToTable());
        btnSaveCustomer.addActionListener(e -> saveCustomerToFile());

        loadCustomersToTable(); // load existing customers
        return panel;
    }

    private void addCustomerToTable() {
        try {
            String name = tfCustomerName.getText().trim();
            String address = tfCustomerAddress.getText().trim();
            String group = (String) cbCustomerGroup.getSelectedItem();

            if (name.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không được để trống tên hoặc địa chỉ!");
                return;
            }

            // Giả sử ID auto-increment
            int id = fileRepository.nextCustomerId();
            customerTableModel.addRow(new Object[]{String.format("%05d", id), name, address, group});

            tfCustomerName.setText("");
            tfCustomerAddress.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void saveCustomerToFile() {
        try {
            for (int i = 0; i < customerTableModel.getRowCount(); i++) {
                int id = Integer.parseInt((String) customerTableModel.getValueAt(i, 0));
                String name = (String) customerTableModel.getValueAt(i, 1);
                String address = (String) customerTableModel.getValueAt(i, 2);
                String groupStr = (String) customerTableModel.getValueAt(i, 3);
                CustomerGroup group = CustomerGroup.valueOf(groupStr);

                Customer c = new Customer(id, name, address, group);
                fileRepository.saveCustomer(c); // ghi vào KH.TXT
            }
            JOptionPane.showMessageDialog(this, "Đã lưu tất cả khách hàng!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu: " + ex.getMessage());
        }
    }

    private void loadCustomersToTable() {
        try {
            List<Customer> customers = fileRepository.loadCustomers();
            customerTableModel.setRowCount(0);
            for (Customer c : customers) {
                customerTableModel.addRow(new Object[]{String.format("%05d", c.getId()), c.getName(), c.getAddress(), c.getGroup().name()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi load KH: " + ex.getMessage());
        }
    }

    // --------------------------- PRODUCT PANEL -----------------------------
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Mã MH", "Tên MH", "Đơn giá"};
        productTableModel = new DefaultTableModel(cols, 0);
        productTable = new JTable(productTableModel);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfProductName = new JTextField(15);
        tfProductPrice = new JTextField(8);
        btnAddProduct = new JButton("Thêm MH");
        btnSaveProduct = new JButton("Lưu MH");

        formPanel.add(new JLabel("Tên MH:"));
        formPanel.add(tfProductName);
        formPanel.add(new JLabel("Đơn giá:"));
        formPanel.add(tfProductPrice);
        formPanel.add(btnAddProduct);
        formPanel.add(btnSaveProduct);

        panel.add(formPanel, BorderLayout.NORTH);

        btnAddProduct.addActionListener(e -> addProductToTable());
        btnSaveProduct.addActionListener(e -> saveProductToFile());

        loadProductsToTable();
        return panel;
    }

    private void addProductToTable() {
        try {
            String name = tfProductName.getText().trim();
            String priceStr = tfProductPrice.getText().trim();
            if (name.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không được bỏ trống!");
                return;
            }

            double price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();

            int id = fileRepository.nextProductId();
            productTableModel.addRow(new Object[]{String.format("%05d", id), name, price});

            tfProductName.setText("");
            tfProductPrice.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số >= 0");
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void saveProductToFile() {
        try {
            for (int i = 0; i < productTableModel.getRowCount(); i++) {
                int id = Integer.parseInt((String) productTableModel.getValueAt(i, 0));
                String name = (String) productTableModel.getValueAt(i, 1);
                double price = Double.parseDouble(productTableModel.getValueAt(i, 2).toString());
                Product p = new Product(id, name, price);
                fileRepository.saveProduct(p);
            }
            JOptionPane.showMessageDialog(this, "Đã lưu tất cả mặt hàng!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu: " + ex.getMessage());
        }
    }

    private void loadProductsToTable() {
        try {
            List<Product> products = fileRepository.loadProducts();
            productTableModel.setRowCount(0);
            for (Product p : products) {
                productTableModel.addRow(new Object[]{String.format("%05d", p.getId()), p.getName(), p.getPrice()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi load MH: " + ex.getMessage());
        }
    }

    // --------------------------- INVOICE PANEL -----------------------------
    private JPanel createInvoicePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbInvoiceCustomer = new JComboBox<>();
        loadCustomersToCombo(cbInvoiceCustomer);

        cbInvoiceProduct = new JComboBox<>();
        loadProductsToCombo(cbInvoiceProduct);
        tfInvoiceQuantity = new JTextField(5);
        btnAddInvoiceItem = new JButton("Thêm vào hóa đơn");
        btnSaveInvoice = new JButton("Lưu hóa đơn");

        topPanel.add(new JLabel("Khách hàng:"));
        topPanel.add(cbInvoiceCustomer);
        topPanel.add(new JLabel("Sản phẩm:"));
        topPanel.add(cbInvoiceProduct);
        topPanel.add(new JLabel("Số lượng:"));
        topPanel.add(tfInvoiceQuantity);
        topPanel.add(btnAddInvoiceItem);
        topPanel.add(btnSaveInvoice);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã MH", "Tên MH", "Đơn giá", "Số lượng", "Thành tiền"};
        invoiceItemTableModel = new DefaultTableModel(cols, 0);
        invoiceItemTable = new JTable(invoiceItemTableModel);
        panel.add(new JScrollPane(invoiceItemTable), BorderLayout.CENTER);

        btnAddInvoiceItem.addActionListener(e -> addInvoiceItem());
        btnSaveInvoice.addActionListener(e -> saveInvoice());

        return panel;
    }

    private void loadCustomersToCombo(JComboBox<Customer> combo) {
        combo.removeAllItems();
        try {
            List<Customer> customers = fileRepository.loadCustomers();
            for (Customer c : customers) combo.addItem(c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi load KH cho combo");
        }
    }

    private void loadProductsToCombo(JComboBox<Product> combo) {
        combo.removeAllItems();
        try {
            List<Product> products = fileRepository.loadProducts();
            for (Product p : products) combo.addItem(p);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi load MH cho combo");
        }
    }

    private void addInvoiceItem() {
        try {
            Product p = (Product) cbInvoiceProduct.getSelectedItem();
            int qty = Integer.parseInt(tfInvoiceQuantity.getText().trim());
            if (qty <= 0) throw new NumberFormatException();

            double lineTotal = p.getPrice() * qty;
            invoiceItemTableModel.addRow(new Object[]{
                String.format("%05d", p.getId()), p.getName(), p.getPrice(), qty, lineTotal
            });

            tfInvoiceQuantity.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số > 0");
        }
    }

    private void saveInvoice() {
        try {
            Customer c = (Customer) cbInvoiceCustomer.getSelectedItem();
            List<InvoiceItem> items = new ArrayList<>();
            for (int i = 0; i < invoiceItemTableModel.getRowCount(); i++) {
                Product p = fileRepository.getProductById(Integer.parseInt((String) invoiceItemTableModel.getValueAt(i, 0)));
                int qty = Integer.parseInt(invoiceItemTableModel.getValueAt(i, 3).toString());
                items.add(new InvoiceItem(p, qty));
            }
            Invoice inv = new Invoice(c, items);
            // fileRepository.saveInvoice(inv); // ghi HOADON.TXT
            JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn!");
            invoiceItemTableModel.setRowCount(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn: " + ex.getMessage());
        }
    }

    // --------------------------- REPORT PANEL -----------------------------
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbSortOption = new JComboBox<>(new String[]{"Theo họ tên khách hàng", "Theo số lượng giảm dần"});
        btnLoadReport = new JButton("Tải báo cáo");
        topPanel.add(new JLabel("Sắp xếp:"));
        topPanel.add(cbSortOption);
        topPanel.add(btnLoadReport);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Mã KH", "Họ tên", "Tổng SL", "Tổng tiền"};
        reportTableModel = new DefaultTableModel(cols, 0);
        reportTable = new JTable(reportTableModel);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        btnLoadReport.addActionListener(e -> loadReport());

        return panel;
    }

    private void loadReport() {
        try {
            // List<InvoiceSummary> summaries = fileRepository.loadInvoiceSummaries();
            // String sortOption = (String) cbSortOption.getSelectedItem();
            // if (sortOption.equals("Theo họ tên khách hàng")) {
            //     summaries.sort((a, b) -> a.getCustomer().getName().compareToIgnoreCase(b.getCustomer().getName()));
            // } else {
            //     summaries.sort((a, b) -> Integer.compare(b.getTotalQuantity(), a.getTotalQuantity()));
            // }

            // reportTableModel.setRowCount(0);
            // for (InvoiceSummary s : summaries) {
            //     reportTableModel.addRow(new Object[]{
            //         String.format("%05d", s.getCustomer().getId()),
            //         s.getCustomer().getName(),
            //         s.getTotalQuantity(),
            //         s.getTotalAmount()
            //     });
            // }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải báo cáo: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
