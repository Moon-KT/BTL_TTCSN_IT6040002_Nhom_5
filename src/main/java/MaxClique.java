import java.util.*;

// Lớp đại diện cho đồ thị
class Graph {
    int vertices;
    List<Integer>[] adjList;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList[vertices];
        for (int i = 0; i < vertices; i++) {
            adjList[i] = new ArrayList<>();
        }
    }

    // Thêm cạnh vào đồ thị
    public void addEdge(int v, int w) {
        adjList[v].add(w);
        adjList[w].add(v);
    }

    // Kiểm tra nếu hai đỉnh v và w có quan hệ cạnh
    public boolean isAdjacent(int v, int w) {
        return adjList[v].contains(w);
    }
}

public class MaxClique {
    // Thuật toán Bron-Kerbosch với Pivoting
    public static void bronKerboschPivoting(Set<Integer> R, Set<Integer> P, Set<Integer> X, Graph g, List<Set<Integer>> cliques) {
        if (P.isEmpty() && X.isEmpty()) {
            cliques.add(new HashSet<>(R));
            return;
        }

        // Lựa chọn đỉnh pivot từ P ∪ X
        Integer pivot = choosePivot(P, X, g);
        Set<Integer> PminusN = new HashSet<>(P);
        PminusN.removeAll(neighbors(g, pivot));

        for (Integer v : PminusN) {
            Set<Integer> newR = new HashSet<>(R);
            newR.add(v);
            Set<Integer> newP = new HashSet<>(P);
            newP.retainAll(neighbors(g, v));
            Set<Integer> newX = new HashSet<>(X);
            newX.retainAll(neighbors(g, v));
            bronKerboschPivoting(newR, newP, newX, g, cliques);

            P.remove(v);
            X.add(v);
        }
    }

    // Chọn pivot từ P ∪ X (chọn đỉnh có nhiều hàng xóm nhất)
    public static Integer choosePivot(Set<Integer> P, Set<Integer> X, Graph g) {
        Set<Integer> union = new HashSet<>(P);
        union.addAll(X);

        Integer pivot = null;
        int maxDegree = -1;

        for (Integer v : union) {
            int degree = neighbors(g, v).size();
            if (degree > maxDegree) {
                maxDegree = degree;
                pivot = v;
            }
        }
        return pivot;
    }

    // Trả về danh sách các hàng xóm của đỉnh v
    public static Set<Integer> neighbors(Graph g, Integer v) {
        return new HashSet<>(g.adjList[v]);
    }

    // Thuật toán Nhánh Cận để tìm bè cực đại
    public static void branchAndBound(Graph g) {
        Set<Integer> bestClique = new HashSet<>();
        branchAndBoundRecursive(g, new HashSet<>(), new HashSet<>(), bestClique);
        System.out.println("Bè cực đại tối ưu: " + bestClique);
    }

    // Hàm đệ quy cho thuật toán Nhánh Cận
    public static void branchAndBoundRecursive(Graph g, Set<Integer> R, Set<Integer> P, Set<Integer> bestClique) {
        if (P.isEmpty()) {
            if (R.size() > bestClique.size()) {
                bestClique.clear();
                bestClique.addAll(R);
            }
            return;
        }

        int upperBound = P.size() + R.size();
        if (R.size() + upperBound <= bestClique.size()) {
            return; // Cắt nhánh nếu không thể tốt hơn kết quả hiện tại
        }

        for (Integer v : P) {
            Set<Integer> newR = new HashSet<>(R);
            newR.add(v);
            Set<Integer> newP = new HashSet<>(P);
            newP.retainAll(neighbors(g, v));
            branchAndBoundRecursive(g, newR, newP, bestClique);

            P.remove(v);
        }
    }

    // Phương thức nhập dữ liệu từ người dùng và khởi tạo đồ thị
    public static Graph initializeGraph() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập số lượng đỉnh: ");
        int n = scanner.nextInt();
        Graph g = new Graph(n);

        System.out.print("Nhập số lượng cạnh: ");
        int m = scanner.nextInt();
        System.out.println("Nhập các cạnh (mỗi cạnh theo định dạng: v w):");
        for (int i = 0; i < m; i++) {
            int v = scanner.nextInt();
            int w = scanner.nextInt();
            g.addEdge(v, w);
        }
        return g;
    }

    // Phương thức gọi thuật toán Bron-Kerbosch với Pivoting
    public static void runBronKerbosch(Graph g) {
        Set<Integer> R = new HashSet<>();
        Set<Integer> P = new HashSet<>();
        Set<Integer> X = new HashSet<>();
        for (int i = 0; i < g.vertices; i++) {
            P.add(i);
        }
        List<Set<Integer>> cliques = new ArrayList<>();
        bronKerboschPivoting(R, P, X, g, cliques);

        System.out.println("Các bè cực đại (Bron-Kerbosch):");
        for (Set<Integer> clique : cliques) {
            System.out.println(clique);
        }
    }

    // Phương thức gọi thuật toán Nhánh Cận
    public static void runBranchAndBound(Graph g) {
        System.out.println("Đang chạy thuật toán Nhánh Cận...");
        branchAndBound(g);
    }

    // Phương thức main chỉ gọi các phương thức trên
    public static void main(String[] args) {
        Graph g = initializeGraph();

        // Chạy thuật toán Bron-Kerbosch với Pivoting
        runBronKerbosch(g);

        // Chạy thuật toán Nhánh Cận
        runBranchAndBound(g);
    }
}
