package discover;

import java.util.ArrayList;
import java.util.List;

/**
 * @author plorio
 */
public class NumberSum<T> {
    private final List<Item> items;

    public NumberSum() {
        items = new ArrayList<>();
    }

    public void add(long num, T item) {
        items.add(new Item(num, item));
    }

    public List<List<T>> options(long targetSum) {
        ArrayList<List<T>> lists = new ArrayList<>();

        int combinations = 2 << (items.size() - 1);
        for (int i = 1; i <= combinations; i++) {
            long sum = 0;
            int count = 0;
            for (int j = 0; j < items.size(); j++) {
                if (((i >> j) & 1) == 1) {
                    sum += items.get(j).num;
                    count += 1;
                }
            }
            if (sum == targetSum) {
                List<T> values = new ArrayList<>(count);
                for (int j = 0; j < items.size(); j++) {
                    if (((i >> j) & 1) == 1) {
                        values.add(items.get(j).item);
                    }
                }
                lists.add(values);
            }
        }

        return lists;
    }

    private class Item implements Comparable<Item> {
        private final long num;
        private final T item;

        public Item(long num, T item) {
            this.num = num;
            this.item = item;
        }

        @Override
        public int compareTo(Item o) {
            return Long.compare(num, o.num);
        }
    }
}
