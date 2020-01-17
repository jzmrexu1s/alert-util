package alertutil;

import java.util.concurrent.TimeUnit;

public class test {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i ++) {
            Thread t = new alert_invoker(i);
            System.out.println("Starting thread " + i + "... ");
            t.start();
        }
        TimeUnit.SECONDS.sleep(1);
        Thread q = new alert_invoker(3);
        System.out.println("Starting thread with identical content after 1 sec... ");
        q.start();
        System.out.println("Check the hashmap... ");
        TimeUnit.SECONDS.sleep(1);
        Thread t_out = new alert_out();
        t_out.start();
    }

    static class alert_out extends Thread {
        @Override
        public void run() {
            AlertUtil a = AlertUtil.getInstance();
            a.printAlerts();
        }
    }

    static class alert_invoker extends Thread {
        private int id;
        private alert_invoker(int id) {
            this.id = id;
        };
        @Override
        public void run() {
            AlertUtil a = AlertUtil.getInstance();
            a.addAlert(String.valueOf(this.id), "default", "hahahaha123");
        }
    }
}
