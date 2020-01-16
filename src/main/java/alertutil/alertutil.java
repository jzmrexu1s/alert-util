package alertutil;

import java.util.concurrent.atomic.AtomicInteger;

class AlertUtil {
    private AtomicInteger val = new AtomicInteger(0);

    private static class Holder {
        private static AlertUtil instance = new AlertUtil();
    }

    public static AlertUtil getInstance() {
        return Holder.instance;
    }

    public void addone() {
        System.out.println(this.val.addAndGet(1));
    }

}
