import java.io.*;
import java.util.*;
import java.util.Hashtable;

public class opcodeTable {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Hashtable< String, String > hash = new Hashtable< String, String >();
        BufferedReader br = new BufferedReader(new FileReader ("opcode.txt"));
		Scanner scanner = new Scanner(System.in);
		
        String line = " ";
		String tempstring = "";
        String[] tempArray= new String[2];
        
		//讀取後分割放入hash中
        while (line != null){
			int i = 0;
            line = br.readLine();
			tempstring = line; 
			if(tempstring != null)
				tempArray = tempstring.split("\\s");
			hash.put(tempArray[i],tempArray[i+1]);
        }
		
		while (true){
			String opcode = scanner.next();
			String code = hash.get(opcode);
			System.out.println(code);
		}

    }
}