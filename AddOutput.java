import java.util.ArrayList;
import java.util.ListIterator;

public class AddOutput {
	//------------------------------------------------------------------
	private static ArrayList<String> operator = new ArrayList<String>();
    private static ArrayList<String> operand1 = new ArrayList<String>();
    private static ArrayList<String> operand2 = new ArrayList<String>();
    private static ArrayList<String> result = new ArrayList<String>();
    private static ListIterator<String> o_rator = operator.listIterator();
    private static ListIterator<String> o_rand_1 = operand1.listIterator();
    private static ListIterator<String> o_rand_2 = operand2.listIterator();
    private static ListIterator<String> o_res = result.listIterator();
    
    
    //---------------------------------------------------------------------
    
    
    
    
    

  //-------------------------------Add Functions----------------------------------------
    public static void addPrevious(String oper, String op1, String op2, String res){//Add to previous index, all positions. Problems with size()-1 being out of bounds?
    	int place = operator.size() - 1;
    	operator.add(place, oper);
    	operand1.add(place, op1);
    	operand2.add(place, op2);
    	result.add(place, res);
    }
    public static void addPrevious(String oper, String op1, String op2){		//truncated add to previous index. Problems?
    	int place = operator.size() - 1;
    	operator.add(place, oper);
    	operand1.add(place, op1);
    	operand2.add(place, op2);
    }
    public static void addPlace(int place, String oper, String op1, String op2, String res){	//add to index
    	operator.add(place, oper);
    	operand1.add(place, op1);
    	operand2.add(place, op2);
    	result.add(place, res);
    }
    public static void add(String oper, String op1, String op2, String res){	//add strings to all places
    	operator.add(oper);
    	operand1.add(op1);
    	operand2.add(op2);
    	result.add(res);
    }
    public static void add(String oper, String op1, String op2){		//add to first three positions in array
    	operator.add(oper);
    	operand1.add(op1);
    	operand2.add(op2);
    }
    public static void addResult(String res){			//add to result array only
    	result.add(res);
    }
    
    //------------------------------------------------------------------------
    

}
