public class Main {
  private static final int pauseTime = 5000;

  public static void main(String[] args) {
    final Restaurant restaurant = new Restaurant();
    ThreadGroup threadGroup = new ThreadGroup("group");

    Thread thread1 = new Thread(threadGroup, restaurant::waiterJob, "Официант1");
    thread1.start();
    Thread thread2 = new Thread(threadGroup, restaurant::waiterJob, "Официант2");
    thread2.start();
    Thread thread3 = new Thread(threadGroup, restaurant::waiterJob, "Официант3");
    thread3.start();

    Thread thread4 = new Thread(threadGroup, restaurant::visitorAction, "Посетитель1");
    thread4.start();
    Thread thread5 = new Thread(threadGroup, restaurant::visitorAction, "Посетитель2");
    thread5.start();

    while (restaurant.getNumberOfCompleteOrders() < restaurant.getMaxNumberOfOrders())
      try {
        Thread.sleep(pauseTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    threadGroup.interrupt();
    restaurant.close();
    System.out.println("Выполнено " + restaurant.getNumberOfCompleteOrders() + " заказов.");
  }
}
