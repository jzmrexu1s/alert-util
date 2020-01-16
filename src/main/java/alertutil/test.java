package alertutil;

public class test {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i ++) {
            Thread t = new alert_invoker();
            System.out.println("Starting thread " + i + "... ");
            t.start();
        }
    }

    static class alert_invoker extends Thread {
        private alert_invoker() {};
        @Override
        public void run() {
            AlertUtil a = AlertUtil.getInstance();
            a.addone();
        }
    }
}
