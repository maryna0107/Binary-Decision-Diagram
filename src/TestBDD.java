public class TestBDD {

    static String bFunctionShow = "abc+!abc"; // mini function and order to show the correctness of the code
    static String[] orderShow = {"a", "b", "c"};
    private static int callFunctions = 1; // calling tests(the number can be changed)
    static GenerateDNF generateDNF = new GenerateDNF();

    static String[] order = {"a","b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"};

    static String[] DNF  = generateDNF.generate(100, order.length, callFunctions); // random generated functions

    public static boolean compareUseAndSetInputs(String function) { // function to check the correctness of the BDDUse
        // Generate the expected outputs using set inputs
        int n = order.length;
        int totalInputs = (int) Math.pow(2, n);
        StringBuilder expectedOutputs = new StringBuilder();
        for (int i = 0; i < totalInputs; i++) {
            String binary = Integer.toBinaryString(i);
            while (binary.length() < n) {
                binary = "0" + binary;
            }
            String inputs = binary;
            String output = GenerateDNF.setInputs(function, order, inputs);
            expectedOutputs.append(inputs).append(" -> ").append(output);
            if (i < totalInputs - 1) {
                expectedOutputs.append(", ");
            }
        }

        // Generate the actual outputs using generateUseInputs
        String actualOutputs = GenerateDNF.generateUseInputs(BddCreate.BDD.root, order);

        // Compare expected and actual outputs
        if (!actualOutputs.equals(expectedOutputs.toString())) {
            System.out.println("Output does not match expected output.");
            return false;
        }

        System.out.println("All outputs match expected outputs.");
        return true;
    }





    public static void main(String[] args) {
        BddCreate BDD = new BddCreate();
        System.out.println("\nSHOW FUNCTION\n");

        BDD.BDDCreate(bFunctionShow, orderShow);
        System.out.println("Please notice that printed BDD is already contains reductions\n");
        BDD.printBDDSideways(BddCreate.BDD.root, 0);
        System.out.println("\nCurrent percent of reduction: " + BddCreate.percentOfReduction(orderShow, BddCreate.BDD.NodesCounter));
        System.out.println("\nUse outputs: \n");
        System.out.println(GenerateDNF.generateUseInputs(BddCreate.BDD.root, orderShow));
        System.out.println("\nExpected outputs: \n");
        System.out.print("000 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "000") + ", ");
        System.out.print("001 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "001") + ", ");
        System.out.print("010 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "010") + ", ");
        System.out.print("011 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "011") + ", ");
        System.out.print("100 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "100") + ", ");
        System.out.print("101 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "101") + ", ");
        System.out.print("110 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "110") + ", ");
        System.out.println("111 -> " + GenerateDNF.setInputs(bFunctionShow, orderShow, "111") + " ");
        System.out.print("\nCompare outputs result: ");
        compareUseAndSetInputs(bFunctionShow);
        System.out.print("\nBest order percentage: ");
        BDD.bestOrder(bFunctionShow);
        System.out.println(BddCreate.percentOfReduction(orderShow, BddCreate.minNodes));



        System.out.println("\nBDD CREATE TEST\n");
        for(int i=0; i< callFunctions; i++) // reduced nodes percentage
        {
            BDD.BDDCreate(DNF[i], order);
            System.out.println("Percent of reduced nodes: " + BddCreate.percentOfReduction(order, BddCreate.BDD.NodesCounter));
            if(BddCreate.percentOfReduction(order, BddCreate.BDD.NodesCounter) == 100)
            {
                System.out.println(DNF[i]);
            }
        }

        for(int i=0; i< callFunctions; i++) // loop for measuring memory for BDDCreate
        {
            BDD.BDDCreate(DNF[i], order);
        }
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Used memory is bytes: " + ((memory)));



        long startTime = System.currentTimeMillis();
        for(int i=0; i< callFunctions; i++) // loop for measuring time for BDDCreate
        {
            BDD.BDDCreate(DNF[i], order);
        }

         long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("used time in ms: " + elapsedTime);


        System.out.println("\nBDD USE TEST\n");


        int n = order.length;
        long startTime1 = System.currentTimeMillis();
        for(int i=0; i< callFunctions; i++) //measuring time of BDDUse
        {
            BddCreate.BDDCreate(DNF[i], order);
            GenerateDNF.generateUseInputs(BddCreate.BDD.root, order);
//            compareUseAndSetInputs(DNF[i]);
        }
        long stopTime1 = System.currentTimeMillis();
        long elapsedTime1 = stopTime1 - startTime1;
        System.out.println("Used time in ms: " + elapsedTime1);
        for(int i=0; i< callFunctions; i++) // comparing outputs of BDDUse and expected ones
        {
            BddCreate.BDDCreate(DNF[i], order);
            GenerateDNF.generateUseInputs(BddCreate.BDD.root, order);
            compareUseAndSetInputs(DNF[i]);
        }


        System.out.println("\nBEST ORDER TEST\n");

        for(int i=0; i< callFunctions; i++) // loop for measuring memory for bestOrder
        {
            BDD.bestOrder(DNF[i]);
        }
        Runtime runtime6 = Runtime.getRuntime();
        runtime6.gc();
        long memory6 = runtime6.totalMemory() - runtime6.freeMemory();


        System.out.println("Used memory is bytes: " + ((memory6)));


        long startTime9 = System.currentTimeMillis();
        for(int i=0; i< callFunctions; i++) // loop for measuring time for BDDCreate
        {
            BDD.bestOrder(DNF[i]);
        }

         long stopTime9 = System.currentTimeMillis();
        long elapsedTime9 = stopTime9 - startTime9;
        System.out.println("Used time in ms: " + elapsedTime9);

        for(int i=0; i< callFunctions; i++) // reduced nodes percentage
        {
            BDD.bestOrder(DNF[i]);
            System.out.println("Best percent of reduced nodes: " + BddCreate.percentOfReduction(order, BddCreate.minNodes));

        }



    }
}
