package src.model;


public class InvoiceItem {
  Product product;
  int quantity;   // > 0
  double getLineTotal() { return product.price * quantity; }
  // ctor
    public InvoiceItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}