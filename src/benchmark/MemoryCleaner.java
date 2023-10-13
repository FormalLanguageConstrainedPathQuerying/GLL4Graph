package benchmark;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class MemoryCleaner {

    public void freeMemory() throws InterruptedException {
        long m;
        long m2 = getReallyUsedMemory();
        do {
            Thread.sleep(567);
            m = m2;
            m2 = getReallyUsedMemory();
        } while (m2 < m);
    }

    private long getReallyUsedMemory() {
        long before = getGcCount();
        System.gc();
        while (getGcCount() == before);
        return getCurrentlyUsedMemory();
    }
    private long getGcCount() {
        long sum = 0;
        for (GarbageCollectorMXBean b : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = b.getCollectionCount();
            if (count != -1) { sum +=  count; }
        }
        return sum;
    }

    private long getCurrentlyUsedMemory() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()
                + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
    }
}
