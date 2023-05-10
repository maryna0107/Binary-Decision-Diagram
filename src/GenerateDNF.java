import java.util.*;

public class GenerateDNF {

    public static String generateUseInputs(BddCreate.BDDNode bdd, String[] order) { // generates all possible use inputs
        int numVariables = order.length;
        int numInputs = 1 << numVariables; // equivalent to 2^n

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numInputs; i++) {
            String binary = Integer.toBinaryString(i);
            while (binary.length() < numVariables) {
                binary = "0" + binary;
            }
            String input = binary;
            char output = BddCreate.BDDUse(BddCreate.BDD.root, input);
            sb.append(input).append(" -> ").append(output);
            if (i < numInputs - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }





    public static String simplifyBooleanFunction(String booleanFunction) { // help function for setInputs that calculates bfunction for given inputs to check BDDUse
        // split function into individual elements
        String[] elements = booleanFunction.split("\\+");

        // loop through each element and simplify
        for (int i = 0; i < elements.length; i++) {
            // check if element contains !0 or !1 and change accordingly
            if (elements[i].contains("!0")) {
                elements[i] = elements[i].replace("!0", "1");
            }
            if (elements[i].contains("!1")) {
                elements[i] = elements[i].replace("!1", "0");
            }

            // simplify element to 1, 0, or 01 if applicable
            if (elements[i].matches("^1+$")) {
                elements[i] = "1";
            } else if (elements[i].matches("^0+$")) {
                elements[i] = "0";
            } else if (elements[i].matches("^(?=.*[0])(?=.*[1])[01]+$")) {
                elements[i] = "01";
            }
        }

        // sort the elements by putting 0 first and then 1
        Arrays.sort(elements, new Comparator<String>() {
            public int compare(String s1, String s2) {
                if (s1.startsWith("0") && s2.startsWith("1")) {
                    return -1;
                } else if (s1.startsWith("1") && s2.startsWith("0")) {
                    return 1;
                } else {
                    return s1.compareTo(s2);
                }
            }
        });

        // remove duplicate elements
        Set<String> uniqueElements = new LinkedHashSet<>(Arrays.asList(elements));

        // combine unique elements with "+"
        StringBuilder sb = new StringBuilder();
        for (String element : uniqueElements) {
            sb.append(element).append("+");
        }
        // remove last "+" character and return simplified function
        return sb.toString().substring(0, sb.length() - 1);
    }

    public static String simplifyZeroAndOne(String function) { // another help method(gives final results)
        String[] terms = function.split("\\+");
        for (int i = 0; i < terms.length; i++) {
            if (terms[i].equals("1")) {
                // replace term with "1"
                return "1";
            }
            if (terms[i].contains("0")) {
                // replace term with empty string
                terms[i] = "";
            }
        }

        // join non-null terms with "+"
        String result = String.join("+", Arrays.stream(terms).filter(t -> t != null).toArray(String[]::new));
        if(result.equals(""))
        {
            return "0";
        }
        // return "0" if result is empty, else return result
        return result.equals("+") ? "0" : result;
    }







    public static String setInputs(String bfunction, String[] order, String inputs) { // function that provides as final results to check BDDUse
        Arrays.sort(TestBDD.order);
        if (bfunction == null || order == null || inputs == null || order.length != inputs.length()) {
            return "-1";
        }
        StringBuilder sb = new StringBuilder(bfunction);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            for (int j = 0; j < order.length; j++) {
                if (order[j].charAt(0) == c) {
                    char values = inputs.charAt(j);
                    sb.setCharAt(i, values);
                    break;
                }
            }
        }
        return simplifyZeroAndOne(simplifyBooleanFunction(sb.toString()));
    }








    public static String[] generate(int numTerms, int letters, int n) { // generates random DNF
        String[] DNF = new String[n];
        for (int k = 0; k < n; k++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numTerms; i++) {
                StringBuilder term = new StringBuilder();
                int numLetters = (int) (Math.random() * letters) + 1;
                for (int j = 0; j < numLetters; j++) {
                    char var = (char) ('a' + (int) (Math.random() * letters));
                    if (Math.random() < 0.5) {
                        term.append(var);
                    } else {
                        term.append("!" + var);
                    }
                }
                sb.append(term).append("+");
            }
            // Remove the trailing '+' character
            sb.deleteCharAt(sb.length() - 1);
            DNF[k] = sb.toString();
        }
        return DNF;
    }
}
