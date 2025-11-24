public class FenwickTree {
    private int[] tree;
    private int[] arr;
    private int n;

    public FenwickTree(int size) {
        if (size <= 0) throw new IllegalArgumentException("Размер должен быть больше 0");
        this.n = size;
        this.tree = new int[n + 1];
        this.arr = new int[n];
    }

    public void build(int[] inputArr) {
        if (inputArr == null || inputArr.length != n) {
            throw new IllegalArgumentException("Неверный размер массива");
        }

        this.tree = new int[n + 1];
        this.arr = inputArr.clone();

        for (int i = 0; i < n; i++) {
            int value = arr[i];
            arr[i] = 0;
            update(i, value);

        }
    }

    public void update(int index, int delta) {
        if (index < 0 || index >= n) {
            throw new IllegalArgumentException("Индекс за пределами");
        }
        arr[index] += delta;

        int i = index + 1;
        while (i <= n) {
            tree[i] += delta;
            i += i & -i;
        }
    }

    //установка нового значения элемента
    public void set(int index, int value) {
        if (index < 0 || index >= n) throw new IllegalArgumentException("Индекс за пределами");
        int delta = value - arr[index];
        update(index, delta);
    }

    public int prefixSum(int index) {
        if (index < 0 || index >= n) {
            throw new IllegalArgumentException("Индекс за пределами");
        }
        int sum = 0;
        int i = index + 1;
        while (i > 0) {
            sum += tree[i];
            i -= i & -i;
        }
        return sum;
    }

    public int totalSum() { return prefixSum(n - 1); }

    public int rangeSum(int left, int right) {
        if (left < 0 || right >= n || left > right) {
            throw new IllegalArgumentException("Неверный диапазон");
        }
        if (left == 0) return prefixSum(right);
        return prefixSum(right) - prefixSum(left - 1);
    }

    public int size() { return n; }

    public String treeToString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 1; i <= n; i++) {
            sb.append(tree[i]);
            if (i < n) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}