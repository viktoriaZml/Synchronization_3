import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
  private final int timeForCook = 3000;
  private final int timeForChoise = 3000;
  private final int timeForEat = 3000;
  private final int maxNumberOfOrders = 5;
  private int numberOfOrders = 0;
  private int numberOfCompleteOrders = 0;
  List<Client> clients = new ArrayList<Client>();
  List<Order> newOrders = new ArrayList<Order>();
  List<Order> readyOrders = new ArrayList<Order>();
  private Lock lock;
  private Condition readyOrder;
  private Condition newOrder;
  private Condition waiter;
  Thread chef;

  public Restaurant() {
    lock = new ReentrantLock(true);
    readyOrder = lock.newCondition();
    newOrder = lock.newCondition();
    waiter = lock.newCondition();
    chef = new Thread(null, this::cook, "Петя");
    chef.start();
  }

  public int getNumberOfCompleteOrders() {
    return numberOfCompleteOrders;
  }

  public int getMaxNumberOfOrders() {
    return maxNumberOfOrders;
  }

  public void cook() {
    System.out.println("Повар на работе!");
    while (true) {
      Order order = null;
      lock.lock();
      try {
        if (newOrders.size() > 0) {
          order = newOrders.remove(0);
          System.out.println("Повар готовит " + order);
        } else {
          newOrder.await();
        }
      } catch (InterruptedException e) {
        System.out.println("Повар завершил работу");
        break;
      } finally {
        lock.unlock();
      }
      if (order != null)
        try {
          Thread.sleep(timeForCook);
          lock.lock();
          try {
            order.setStatus(OrderStatus.READY);
            readyOrders.add(order);
            waiter.signal();
          } finally {
            lock.unlock();
          }
          System.out.println("Повар закончил готовить " + order);
        } catch (InterruptedException e) {
          System.out.println("Повар не успел приготовить " + order);
          break;
        }
    }
  }

  public void close() {
    chef.interrupt();
  }

  public void visitorAction() {
    while (numberOfOrders < maxNumberOfOrders) {
      System.out.println(Thread.currentThread().getName() + " в ресторане");
      try {
        Thread.sleep(new Random().nextInt(timeForChoise));
        Client client = new Client(Thread.currentThread().getName());
        lock.lock();
        try {
          clients.add(client);
          waiter.signal();
        } finally {
          lock.unlock();
        }
        lock.lock();
        try {
          while (client.getOrder() == null || client.getOrder().getStatus() != OrderStatus.READY)
            readyOrder.await();
          System.out.println(Thread.currentThread().getName() + " приступил к еде");
        } finally {
          lock.unlock();
        }
        Thread.sleep(new Random().nextInt(timeForEat));
      } catch (InterruptedException e) {
        System.out.println(Thread.currentThread().getName() + " не успел сделать заказ");
      }
      numberOfCompleteOrders++;
      System.out.println(Thread.currentThread().getName() + " вышел из ресторана");
    }
  }

  public void waiterJob() {
    System.out.println(Thread.currentThread().getName() + " на работе!");
    while (true) {
      Client client = null;
      Order order = null;
      lock.lock();
      try {
        if (clients.size() > 0 && numberOfOrders < maxNumberOfOrders) {
          client = clients.remove(0);
          order = new Order(++numberOfOrders, client);
          client.setOrder(order);
          System.out.println(Thread.currentThread().getName() + " взял " + order);
          newOrders.add(order);
          newOrder.signal();
        } else if (readyOrders.size() > 0) {
          order = readyOrders.remove(0);
          System.out.println(Thread.currentThread().getName() + " несет заказ " + order);
          readyOrder.signal();
        } else
          waiter.await();
      } catch (InterruptedException e) {
        System.out.println(Thread.currentThread().getName() + " завершил работу");
        break;
      } finally {
        lock.unlock();
      }
    }
  }
}
