package util;

public class Tree {

    /**
     * Estimates the branching factor (how many branches each node has) of a tree with depth d, that
     * has n nodes
     *
     * @param n the number of nodes
     * @param d the depth of the tree
     */
    public static double calculateBranchingFactor(int n, int d) {
        double min = 1.0;
        double max = 10.0;
        double tolerance = 1e-10;
        double mid = 0;

        while ((max - min) > tolerance) {
            mid = (min + max) / 2;
            double result = Math.pow(mid, d + 1) - n * mid + (n - 1);

            if (result == 0.0) {
                break;
            } else if (result < 0) {
                min = mid;
            } else {
                max = mid;
            }
        }

        return mid;
    }

    /**
     * Calculate the number of nodes a t-ary tree with depth d has.
     */
    public static int calculateNodeCountOfTree(int t, int d) {
        int result = 0;
        for (int i = 0; i <= d; i++) {
            result += Math.pow(t, i);
        }
        return result;
    }
}
