import discover.OptionDecoder;
import run.Snapshot;
import run.SystemRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * @author plorio
 */
public class ValueLog {
    public static void main(String[] args) throws IOException {
        SystemRunner systemRunner = new SystemRunner(2, 9);
        Snapshot snapshot = null;
        for (int i = 0; i < 10000; i++) {
            snapshot = systemRunner.tick(true);
        }

        TreeMap<Long, Long[]> items = new TreeMap<>();
        for (int i = 0; i < snapshot.transmittersHistory.length; i++) {
            System.out.println("Transmitter: " + i);
            for (int j = 0; j < snapshot.transmittersHistory[i].size(); j++) {
                insert(items, snapshot.ticks.get(j), i, snapshot.transmittersHistory[i].get(j));
            }
        }

        for (int i = 0; i < snapshot.decodedTransmitterHistory.length; i++) {
            System.out.println("Decoded: " + i);
            for (int j = 0; j < snapshot.decodedTransmitterHistory[i].size(); j++) {
                insert(items, snapshot.decodedTicks.get(j), 9 + i, snapshot.decodedTransmitterHistory[i].get(j));
            }
        }

        writeFile("res.csv", items);
    }

    private static void insert(TreeMap<Long, Long[]> tree, long key, int index, long value) {
        if (key < 0) {
            return;
        }

        Long[] longs = tree.get(key);
        if (longs == null) {
            longs = new Long[19];
            longs[18] = 0L;
            tree.put(key, longs);
        }
        longs[index] = value;
        longs[18] += 1;
    }

    private static void writeFile(String name, TreeMap<Long, Long[]> tree) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(new File(name)));
        writer.println("tick,t1,t2,t3,t4,t5,t6,t7,t8,t9,d1,d2,d3,d4,d5,d6,d7,d8,d9");
        for (Long key : tree.keySet()) {
            Long[] longs = tree.get(key);
            if (longs[18] != 18) {
                continue;
            }
            writer.print(key);
            for (int i = 0; i < longs.length - 1; i++) {
                writer.print(",");
                writer.print(longs[i]);
            }
            writer.println();
        }
        writer.close();
    }
}
