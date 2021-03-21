public class Order {
  private int id;
  private Client client;
  private OrderStatus status;

  public Order(int id, Client client) {
    this.id = id;
    this.client = client;
    status = OrderStatus.NEW;
  }

  public int getId() {
    return id;
  }

  public Client getClient() {
    return client;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "Заказ №" + id +
            " клиента " + client.getName() +
            ".";
  }
}
