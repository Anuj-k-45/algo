import java.util.*;

class AVLNode {
    int key, height;
    AVLNode left, right;

    AVLNode(int key) {
        this.key = key;
        height = 1;
    }
}

public class AVLRangeTree {

    AVLNode root;

    int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    int max(int a, int b) {
        return Math.max(a, b);
    }

    int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode t2 = x.right;

        x.right = y;
        y.left = t2;

        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x;
    }

    AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode t2 = y.left;

        y.left = x;
        x.right = t2;

        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y;
    }

    AVLNode insert(AVLNode node, int key) {

        if (node == null)
            return new AVLNode(key);

        if (key < node.key)
            node.left = insert(node.left, key);
        else if (key > node.key)
            node.right = insert(node.right, key);
        else
            return node;

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // LL
        if (balance > 1 && key < node.left.key)
            return rightRotate(node);

        // RR
        if (balance < -1 && key > node.right.key)
            return leftRotate(node);

        // LR
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RL
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    void rangeSearch(AVLNode node, int low, int high) {

        if (node == null)
            return;

        if (low < node.key)
            rangeSearch(node.left, low, high);

        if (node.key >= low && node.key <= high)
            System.out.print(node.key + " ");

        if (high > node.key)
            rangeSearch(node.right, low, high);
    }

    public static void main(String[] args) {

        AVLRangeTree tree = new AVLRangeTree();

        int arr[] = {49,23,80,10,37,62,89,3,19,30,59,70,100};

        for(int x : arr)
            tree.root = tree.insert(tree.root, x);

        int low = 18;
        int high = 77;

        System.out.println("Nodes in range [" + low + "," + high + "]");

        tree.rangeSearch(tree.root, low, high);
    }
}