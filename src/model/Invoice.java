package src.model;
import java.util.*;

public class Invoice {
    static int id_cnt=0;
  int id;                 // có thể thêm id nếu muốn
  Customer customer;
  List<InvoiceItem> items;
  double getTotal() //{items.getLineTotal(); }
  {
    double total = 0;
    for (InvoiceItem item : items) {
      total += item.getLineTotal();
    }
    return total;
  }
  // ctor
    public Invoice(Customer customer, List<InvoiceItem> items) {
        this.id = id_cnt++;
        this.customer = customer;
        this.items = items;
    }
}
