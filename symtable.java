import java.io.*;
import java.util.*;
import java.util.Hashtable;

public class symtable {
	static Hashtable< String, String > hash = new Hashtable< String, String >();
	static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.print("�[�J�п�Ji  �j�M�п�Js:");
		char in = scanner.next().charAt(0);
		while(true){
			if(in == 'i'){
				insert();
			}
			if(in=='s'){
				search();
			}
			if(in!='i' && in!='s'){
				System.out.println("error");
			}
			System.out.print("�[�J�п�Ji  �j�M�п�Js:");
			in = scanner.next().charAt(0);
		}
    }
	public static void insert() {
		Scanner in = new Scanner(System.in);
		boolean laboo;
		boolean add;
		int num=0;
		try {
			System.out.print("�n��J�X��(n):");
			num = scanner.nextInt();
			System.out.println("Lable address(�ΪŮ�j�}):");
			while(num>0){
				int i = 0;
				String lab = in.nextLine();
				String[] tempArray= new String[2];
				tempArray = lab.split("\\s");
				laboo=hash.containsKey(tempArray[0]);
				add=hash.containsValue(tempArray[1]);
				if((laboo==false)&&(add==false))
					hash.put(tempArray[i],tempArray[i+1]);
				else
					System.out.println("�w���ۦP��");
				num--;
			}
		}
		catch (Exception ex) {
		}
	}
	public static void search() {
		String opcode = scanner.next();
		String code = hash.get(opcode);
		System.out.println(code);
	}
}