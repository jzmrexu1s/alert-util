package alertutil;

import java.util.concurrent.TimeUnit;

public class test {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i ++) {
            Thread t = new alert_invoker(i);
            System.out.println("Starting thread " + i + "... ");
            t.start();
        }
        TimeUnit.SECONDS.sleep(2);
        Thread q = new alert_invoker(3);
        System.out.println("Starting thread with identical content after 1 sec... ");
        q.start();
        TimeUnit.SECONDS.sleep(1);
        Thread r = new alert_refresh();
        System.out.println("Starting thread to refresh... ");
        r.start();
        System.out.println("Check the hashmap... ");
        TimeUnit.SECONDS.sleep(1);
        Thread t_out = new alert_out();
        t_out.start();
    }

    static class alert_out extends Thread {
        @Override
        public void run() {
            AlertUtil a = AlertUtil.getInstance();
            a.printAllAlerts();
        }
    }

    static class alert_refresh extends Thread {
        @Override
        public void run() {
            AlertUtil a = AlertUtil.getInstance();
            a.refresh();
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
            a.addAlert(String.valueOf(this.id), "default", "hahahaha123", false);
        }
    }
}
