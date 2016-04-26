import discover.OptionDecoder;
import run.Snapshot;
import run.SystemRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

        for (int i = 0; i < snapshot.transmittersHistory.length; i++) {
            System.out.println("Transmitter: " + i);
            TreeMap<Long, Long> hist = new TreeMap<>();
            for (int j = 0; j < snapshot.transmittersHistory[i].size(); j++) {
                hist.put(snapshot.ticks.get(j), snapshot.transmittersHistory[i].get(j));
            }
            writeFile("trans-" + i + ".csv", hist);
        }

        for (int i = 0; i < snapshot.decodedTransmitterHistory.length; i++) {
            System.out.println("Decoded: " + i);
            TreeMap<Long, Long> hist = new TreeMap<>();
            for (int j = 0; j < snapshot.decodedTransmitterHistory[i].size(); j++) {
                hist.put(snapshot.decodedTicks.get(j), snapshot.decodedTransmitterHistory[i].get(j));
            }
            writeFile("data-" + i + ".csv", hist);
        }
    }

    private static void writeFile(String name, TreeMap<Long, Long> hist) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(new File(name)));
        for (Long key : hist.keySet()) {
            writer.println(key + "," + hist.get(key));
        }
        writer.close();
    }
}
