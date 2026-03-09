package sorter;
import dataclass.sort_array;

public class tree_sort extends sorter {

    private class TreeNode {
        int value;
        TreeNode left;
        TreeNode right;

        TreeNode(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private TreeNode root;

    public tree_sort(sort_array arr) {
        super(arr);
    }

    @Override
    public int[] solve() {
        int[] array = arr.get_array();
        int n = arr.get_size();
        
        if (n == 0) {
            return array;
        }
        
        // Build binary search tree
        root = null;
        for (int value : array) {
            root = insert(root, value);
        }
        
        // Perform in-order traversal to get sorted array
        int[] sortedArray = new int[n];
        inOrderTraversal(root, sortedArray, new int[]{0});
        
        // Copy sorted values back to original array
        System.arraycopy(sortedArray, 0, array, 0, n);
        
        return array;
    }

    private TreeNode insert(TreeNode node, int value) {
        if (node == null) {
            return new TreeNode(value);
        }
        
        if (value < node.value) {
            node.left = insert(node.left, value);
        } else {
            node.right = insert(node.right, value);
        }
        
        return node;
    }

    private void inOrderTraversal(TreeNode node, int[] array, int[] index) {
        if (node == null) {
            return;
        }
        
        // Traverse left subtree
        inOrderTraversal(node.left, array, index);
        
        // Visit node
        array[index[0]] = node.value;
        index[0]++;
        
        // Traverse right subtree
        inOrderTraversal(node.right, array, index);
    }
}
