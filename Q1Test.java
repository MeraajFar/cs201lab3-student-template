public class Q1Test {
     public static void main(String[] args) {

          String result, traversal1, traversal2, traversal3;

          traversal1 = "1-2";
          traversal2 = "2-1-3";
          traversal3 = "1-3-2";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println("Expected Result : Invalid traversals");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "1-2-3";
          traversal2 = "2-1-3";
          traversal3 = "1-3-2";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println(
                    "Expected Result : Traversal 1 - Inorder, Traversal 2 - Preorder, Traversal 3 - Postorder");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "1-2-3";
          traversal2 = "3-2-1";
          traversal3 = "2-3-1";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println("Expected Result : Invalid traversals");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "3-1-2-5-4";
          traversal2 = "2-1-4-5-3";
          traversal3 = "1-2-3-4-5";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println(
                    "Expected Result : Traversal 1 - Preorder, Traversal 2 - Postorder, Traversal 3 - Inorder");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "10-20-30-40-50";
          traversal2 = "20-10-30-40-50";
          traversal3 = "10-50-40-30-20";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println(
                    "Expected Result : Traversal 1 - Inorder, Traversal 2 - Preorder, Traversal 3 - Postorder");
          System.out.println("  Actual Result : " + result);
          System.out.println();
     } 

     public static String verify(String traversal1, String traversal2, String traversal3){
          int[][] traversals = new int[3][];
          String[] inputs = {traversal1, traversal2, traversal3};
          try {
               for (int i = 0; i < inputs.length; i++) {
                    traversals[i] = parseTraversal(inputs[i]);
               }
          } catch (IllegalArgumentException exception) {
               return "Invalid traversals";
          }

          if (traversals[0].length != traversals[1].length
                    || traversals[0].length != traversals[2].length) {
               return "Invalid traversals";
          }

          // Try each assignment because the input order is unknown.
          for (int inorder = 0; inorder < 3; inorder++) {
               for (int preorder = 0; preorder < 3; preorder++) {
                    if (inorder == preorder) {
                         continue;
                    }
                    int postorder = 3 - inorder - preorder;
                    int[] next = {0};
                    Node root = buildTree(traversals[preorder], next,
                              Long.MIN_VALUE, Long.MAX_VALUE);
                    if (next[0] != traversals[preorder].length) {
                         continue;
                    }

                    java.util.List<Integer> actualInorder = new java.util.ArrayList<>();
                    java.util.List<Integer> actualPostorder = new java.util.ArrayList<>();
                    collectTraversals(root, actualInorder, actualPostorder);
                    if (matches(traversals[inorder], actualInorder)
                              && matches(traversals[postorder], actualPostorder)) {
                         String[] orders = new String[3];
                         orders[inorder] = "Inorder";
                         orders[preorder] = "Preorder";
                         orders[postorder] = "Postorder";
                         return "Traversal 1 - " + orders[0] + ", Traversal 2 - "
                                   + orders[1] + ", Traversal 3 - " + orders[2];
                    }
               }
          }
          return "Invalid traversals";
     }

     private static int[] parseTraversal(String traversal) {
          if (traversal == null || traversal.trim().isEmpty()) {
               throw new IllegalArgumentException("Empty traversal");
          }
          java.util.List<Integer> values = new java.util.ArrayList<>();
          java.util.regex.Matcher token = java.util.regex.Pattern
                    .compile("\\s*[+-]?[0-9]+\\s*").matcher(traversal);
          java.util.Set<Integer> seen = new java.util.HashSet<>();
          int position = 0;
          while (position < traversal.length()) {
               token.region(position, traversal.length());
               if (!token.lookingAt()) {
                    throw new IllegalArgumentException("Invalid integer");
               }
               int value = Integer.parseInt(token.group().trim());
               if (!seen.add(value)) {
                    throw new IllegalArgumentException("Duplicate node");
               }
               values.add(value);
               position = token.end();
               if (position < traversal.length()) {
                    if (traversal.charAt(position) != '-' || ++position == traversal.length()) {
                         throw new IllegalArgumentException("Invalid separator");
                    }
               }
          }
          int[] result = new int[values.size()];
          for (int i = 0; i < result.length; i++) {
               result[i] = values.get(i);
          }
          return result;
     }

     private static class Node {
          final int value;
          Node left, right;

          Node(int value) {
               this.value = value;
          }
     }

     // The stack holds ancestors; lower tracks the most recent right turn.
     private static Node buildTree(int[] preorder, int[] next, long lower, long upper) {
          Node root = null;
          java.util.Deque<Node> ancestors = new java.util.ArrayDeque<>();
          for (int value : preorder) {
               if (value <= lower || value >= upper) {
                    break;
               }
               Node parent = null;
               while (!ancestors.isEmpty() && value > ancestors.peek().value) {
                    parent = ancestors.pop();
                    lower = parent.value;
               }
               Node node = new Node(value);
               if (parent != null) {
                    parent.right = node;
               } else if (!ancestors.isEmpty()) {
                    ancestors.peek().left = node;
               } else {
                    root = node;
               }
               ancestors.push(node);
               next[0]++;
          }
          return root;
     }

     private static void collectTraversals(Node node, java.util.List<Integer> inorder,
               java.util.List<Integer> postorder) {
          java.util.Deque<Node> stack = new java.util.ArrayDeque<>();
          Node current = node;
          while (current != null || !stack.isEmpty()) {
               while (current != null) {
                    stack.push(current);
                    current = current.left;
               }
               current = stack.pop();
               inorder.add(current.value);
               current = current.right;
          }
          if (node != null) {
               stack.push(node);
          }
          // Reverse root-right-left to obtain left-right-root.
          while (!stack.isEmpty()) {
               current = stack.pop();
               postorder.add(current.value);
               if (current.left != null) {
                    stack.push(current.left);
               }
               if (current.right != null) {
                    stack.push(current.right);
               }
          }
          java.util.Collections.reverse(postorder);
     }

     private static boolean matches(int[] expected, java.util.List<Integer> actual) {
          if (expected.length != actual.size()) {
               return false;
          }
          for (int i = 0; i < expected.length; i++) {
               if (expected[i] != actual.get(i)) {
                    return false;
               }
          }
          return true;
     }
}
