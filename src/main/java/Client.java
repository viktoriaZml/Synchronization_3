public class Client {
  private String name;
  private Order order;

  public Client(String name) {
    this.name = name;
    order = null;
  }

  public String getName() {
    return name;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }
}
