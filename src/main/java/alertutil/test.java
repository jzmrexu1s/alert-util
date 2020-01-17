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
        System.out.println("Starting thread with identical content... ");
        q.start();

        TimeUnit.SECONDS.sleep(1);
        Thread t_out = new alert_out();
        t_out.start();
    }

    static class alert_out extends Thread {
        @Override
        public void run() {
            AlertUtil a = AlertUtil.getInstance();
            a.print_alerts();
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
            a.add_alert(new AlertUtil.Alert("default", String.valueOf(this.id)));
        }
    }
}
