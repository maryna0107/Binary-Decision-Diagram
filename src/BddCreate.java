import java.util.*;
public class BddCreate {
    static int minNodes = 0;
    public static class BDD {
        public static int size = 0; // size of BDD
        public static int NodesCounter = 3; // number of elements
        public static BDDNode root; // root pointer

        public BDD(int numVariables, int size, BDDNode root) {
            NodesCounter = numVariables;
            this.size = size;
            BDD.root = root;
        }
    }
    static class BDDNode {
        private static HashMap<Character,Character> mapause = new HashMap<>(); // hash map for bddUse
        public static BDDNode one = new BDDNode("1", null, null); // final "one" and "zero" nodes
        public static BDDNode zero = new BDDNode("0", null, null);

        private final String element;
        private BDDNode low;
        private BDDNode high;
        private static Map<String, BDDNode> hashTable = new HashMap<>();

        public BDDNode(String element, BDDNode high, BDDNode low) {
            this.element = element;
            this.high = high;
            this.low = low;
        }
        public BDDNode getHigh() {
            return this.high;
        }

        public BDDNode getLow() {
            return this.low;
        }
    }
    public static BDD BDDCreate(String bFunction, String[] order) { // create function for BDD
        BDD.NodesCounter = 3; // set counter
        String topElement = searchTop(bFunction, order); // check if there are elements from the order in bFunction
        bFunction = simplify(bFunction); // simplify expression

        if (bFunction.equals("0") ||  bFunction.equals("!1") ) { // check if expression is 0 or !1(for example if function was c!c it will be simplified)
            BDD.NodesCounter = 1; // set counter to 1
            if (topElement == null) {
                return null;
            } else {
                BDD.root = BDDNode.zero;
                return new BDD(BDD.NodesCounter, 1, BDD.root);
            }
        }
        if (bFunction.equals("1") ||  bFunction.equals("!0") ) { // analogically
            BDD.NodesCounter = 1;
            if (topElement == null) {
                return null;
            } else {
                BDD.root = BDDNode.one;
                return new BDD(BDD.NodesCounter, 1, BDD.root);
            }
        }
        int numElements = order.length;
        Map<String, BDDNode> elemNodes = new IdentityHashMap<>();
        BDDNode.hashTable.clear(); // clear the hash table before each call to BDDCreate
        BDD.root = create(bFunction, order, elemNodes, numElements + 1);
        return new BDD(BDD.NodesCounter, BDDNode.hashTable.size(), BDD.root); // return BDD structure
    }

    public static BDDNode create(String bFunction, String[] order, Map<String, BDDNode> elemNodes, int i) { // help method that recursively creates BDD
        if (bFunction.equals("0") || bFunction.equals("!1")) { //set to final "one" or "zero" nodes
            return BDDNode.zero;
        } else if (bFunction.equals("1") || bFunction.equals("!0")) {
            return BDDNode.one;
        } else {
            String top = searchTop(bFunction, order);
            if (top == null) {
                return null;
            }
            String key = Integer.toString((top + bFunction).hashCode()); // create a unique key for the node
            if (BDDNode.hashTable.containsKey(key)) { // check if the node has already been created (i-reduction)
                BDD.NodesCounter--;

                return BDDNode.hashTable.get(key); // return the existing node
            }
            BDDNode lowNode, highNode;
            String bFunctionLow, bFunctionHigh;
            if (elemNodes.containsKey(top)) { // if there is an element inside the function
                BDDNode topNode = elemNodes.get(top); // creating low branch of the tree
                bFunctionLow = substitute(bFunction, top, "0"); // change the element to "0" and simplify the expression
                bFunctionLow = simplify(bFunctionLow);

                if (!bFunctionLow.equals("0") && !bFunctionLow.equals("1")) {
                    BDD.NodesCounter++; // update counter
                }
                lowNode = create(bFunctionLow, order, elemNodes, i + 1); // recursion for creating BDD



            } else { // if there is no such element found inside the function

                bFunctionLow = substitute(bFunction, top, "0");  // change the element to "0" and simplify the expression
                bFunctionLow = simplify(bFunctionLow);
                if (!bFunctionLow.equals("0") && !bFunctionLow.equals("1")) {
                    BDD.NodesCounter++;// update counter
                }
                lowNode = create(bFunctionLow, order, elemNodes, i + 1);// recursion for creating BDD

                elemNodes.put(top, lowNode);

            }
            if (elemNodes.containsKey(top)) { // analogically to the first part of this function but now substitution is provided for 1 (building high branch of the tree)
                BDDNode topNode = elemNodes.get(top);
                bFunctionHigh = substitute(bFunction, top, "1");
                bFunctionHigh = simplify(bFunctionHigh);
                if (!bFunctionHigh.equals("0") && !bFunctionHigh.equals("1")) {
                    BDD.NodesCounter++;
                }
                highNode = create(bFunctionHigh, order, elemNodes, i + 1);

                if (lowNode == highNode && lowNode!=BDDNode.zero && lowNode!= BDDNode.one) {
                    BDD.NodesCounter--;
                    return lowNode;
                }
            } else {

                bFunctionHigh = substitute(bFunction, top, "1");
                bFunctionHigh = simplify(bFunctionHigh);
                if (bFunctionHigh.equals("0") || bFunctionHigh.equals("1")) {
                    BDD.NodesCounter++;
                }
                highNode = create(bFunctionHigh, order, elemNodes, i + 1);

                elemNodes.put(top, highNode);
                if (lowNode == highNode && lowNode!=BDDNode.zero && lowNode!= BDDNode.one) {
                    BDD.NodesCounter--;
                    return lowNode;
                }
            }
            BDDNode topNode = new BDDNode(top, highNode, lowNode);
            elemNodes.put(top, topNode);
            BDDNode.hashTable.put(key, topNode); // store the new node in the hash table
            BDD.size = BDDNode.hashTable.size();
            return topNode;
        }
    }


    private static String searchTop(String bFunction, String[] order) { // search element from order inside the function
        for (String element : order) {
            if (bFunction.contains(element) || bFunction.contains("!" + element)) {
                return element;
            }

        }
        return null;
    }

    private static String substitute(String bFunction, String element, String value) { // provide substitution for 1 or 0 depending on if it is high or low functions(from create)
        String newFunction = bFunction.replace(element, value);
        newFunction = newFunction.replace("!" + element, value.equals("0") ? "1" : "0");
        return newFunction;
    }

    public static String simplify(String bFunction) { // function that simplify boolean functions
        String[] elements = bFunction.split("\\+"); // split with "+"
        for (int i = 0; i < elements.length; i++) { // replace all !1 and !0 to 0 and 1
            // check if element contains !0 or !1 and change accordingly
            if (elements[i].contains("!0")) {
                elements[i] = elements[i].replace("!0", "1");
            }
            if (elements[i].contains("!1")) {
                elements[i] = elements[i].replace("!1", "0");
            }
        }


        for (int i = 0; i < elements.length; i++) {
            String element = elements[i];
            if (i < elements.length - 1) {
                String nextElement = elements[i + 1];
                //a+a=a
                if (element.equals(nextElement)) {
                    elements[i] = element.replaceAll("\\d", "");
                    elements[i + 1] = "";
                }
                if (i > 0 && element.equals(elements[i - 1])) {
                    elements[i] = "";
                }
                if (element.equals(nextElement + "'") || element.equals("'" + nextElement)) {
                    elements[i] = "1";
                    elements[i + 1] = "";
                }
            }
            //a1=a
            if(element.contains("1") && !element.equals("1"))
            {
                elements[i] = element.replace("1", "");
                if (elements[i].isEmpty()) { // if element contains only 1, return 1(a+1=1)
                    return "1";
                }
            }
            // a+1=1
            if(element.equals("1"))
            {
                return "1";
            }
            //a0=0
            if(element.contains("0"))
            {
                elements[i] = "";
            }
            // a!a=0
            if((element.contains("a!a") && !element.contains("!a!a")) || ((element.contains("t!t") || element.contains("!tt")) && !element.contains("!t!t")) || ((element.contains("s!s") || element.contains("!ss")) && !element.contains("!s!s"))|| ((element.contains("r!r") || element.contains("!rr")) && !element.contains("!r!r"))||  ((element.contains("q!q") || element.contains("!qq")) && !element.contains("!q!q"))|| ((element.contains("p!p") || element.contains("!pp")) && !element.contains("!p!p")) || ((element.contains("o!o") || element.contains("!oo")) && !element.contains("!o!o")) || ((element.contains("n!n") || element.contains("!nn")) && !element.contains("!n!n")) || (element.contains("b!b") && !element.contains("!b!b")) || (element.contains("c!c") && !element.contains("!c!c")) || (element.contains("d!d") && !element.contains("!d!d")) || (element.contains("e!e") && !element.contains("!e!e")) || (element.contains("f!f") && !element.contains("!f!f"))|| (element.contains("g!g") && !element.contains("!g!g"))|| (element.contains("h!h") && !element.contains("!h!h")) || (element.contains("i!i") && !element.contains("!i!i")) || (element.contains("j!j") && !element.contains("!j!j"))|| element.contains("k!k") || (element.contains("l!l") && !element.contains("!l!l")) || (element.contains("m!m") && !element.contains("!m!m")) || (element.contains("!aa")&& !element.contains("!a!a")) || (element.contains("!bb")&& !element.contains("!b!b")) || (element.contains("!cc")&& !element.contains("!c!c")) || (element.contains("!dd") && !element.contains("!d!d")) ||( element.contains("!ee") && !element.contains("!e!e")) || (element.contains("!ff") && !element.contains("!f!f"))|| (element.contains("!gg")&& !element.contains("!g!g")) || (element.contains("!hh") && !element.contains("!h!h")) ||( element.contains("!ii") && !element.contains("!i!i")) || (element.contains("!jj") && !element.contains("!j!j")) || (element.contains("!kk") && !element.contains("!k!k")) || (element.contains("!ll")&& !element.contains("!l!l")) || (element.contains("!mm") && !element.contains("!m!m")) )
            {
                elements[i] = "";
            }
            if (Arrays.stream(elements).allMatch(e -> e.equals("0")) || Arrays.stream(elements).allMatch(e -> e.equals("!1"))) {
                return "0";
            }
            if (Arrays.stream(elements).allMatch(e -> e.equals("1")) || Arrays.stream(elements).allMatch(e -> e.equals("!0"))) {
                return "1";
            }

        }


        String newBFunction = String.join("+", elements).replaceAll("\\++", "+");
        if (newBFunction.startsWith("+")) {
            newBFunction = newBFunction.substring(1);
        }
        if (newBFunction.endsWith("+")) {
            newBFunction = newBFunction.substring(0, newBFunction.length() - 1);
        }

        if (newBFunction.isEmpty()) {
            return "0";
        } else {

            return newBFunction;
        }
    }

    public static String[] permutation(String[] order) { // function to provide linear permutations
        String firstElement = order[0];
        for (int i = 1; i < order.length; i++) {
            order[i - 1] = order[i];
        }
        order[order.length - 1] = firstElement;
        return order;
    }

    public static BDD bestOrder(String bFunction) { // function to find out the best order of "order" input (it goes through linear permutations of input order so for order length of n it will create BDD n times and compare count of nodes)
        BDD.NodesCounter = 3;
        String[] currentOrder = Arrays.copyOf(TestBDD.order, TestBDD.order.length); // copy an array of given order
        BDDCreate(bFunction, currentOrder);
        permutation(currentOrder); // provide permutation
        minNodes = BDD.NodesCounter; // store current number of nodes as minimum
        BDDNode bestBDDNode = BDD.root;

        while (!Arrays.equals(currentOrder, TestBDD.order)){ // while permutations don`t return to the given one count nodes
            BDD.NodesCounter = 3;
            BDDCreate(bFunction, currentOrder);
            permutation(currentOrder); // provide permutation
            int currentNodes = BDD.NodesCounter;

            if (currentNodes < minNodes) { // compare current number of nodes to the min one
                minNodes = currentNodes;
                bestBDDNode = BDD.root;
            }
        }

        BDD bestBDD = new BDD(BDD.NodesCounter, BDDNode.hashTable.size(), bestBDDNode);
        return bestBDD;
    }


    static char BDDUse(BDDNode currentNode, String input) { // function to use bdd for given inputs
        Arrays.sort(TestBDD.order);
        if (currentNode == null) { // check if root or current node is not null
            return (char)(-1);
        }

        Map<Character, Boolean> variableValues = new HashMap<>(); // hash map to handle reduced nodes
        for (int i = 0; i < input.length(); i++) {
            variableValues.put((char) ('a' + i), input.charAt(i) == '1');
        }

        try {
            while (!(currentNode.element.equals("1") || (currentNode.element.equals("0")))) // if current input number is 0 go to low child and if 1 go to high child
            {
                if (variableValues.get(currentNode.element.charAt(0))) {
                    currentNode = currentNode.high;
                } else {
                    currentNode = currentNode.low;
                }
            }
        } catch (NullPointerException e) {
            return (char)(-1);
        }
        return currentNode.element.charAt(0);

    }




    public static int countMaxNodes(String[] order) { // count all possible nodes in the bdd
        int numVariables = order.length;
        int maxNodes = 1;
        int count =1;
        for (int i = 0; i < numVariables; i++) {
            count += Math.pow(2, i);
            maxNodes = maxNodes+count;
        }
        return maxNodes;
    }
    public static float percentOfReduction(String[] order, int nodes) // count percent of reduction
    {
        float percent;
        int max = countMaxNodes(order);
        percent = (100-(((float)nodes/max)*100));
        return percent;
    }

    public static void printBDDSideways(BDDNode node, int indent) { // function to print BDD tree (see in testBDD)
        if (node == null) {
            return;
        }
        printBDDSideways(node.getHigh(), indent + 4);
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.println(node.element);
        printBDDSideways(node.getLow(), indent + 4);
    }
}