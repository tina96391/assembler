import java.io.*;
import java.util.*;

public class ass {
	static Hashtable< String, String > symTable = new Hashtable< String, String >();
	static Hashtable< String, String > opcodeTable = new Hashtable< String, String >();
	static int error=0;//�p����~�ƶq
	
    public static void main(String[] args)throws FileNotFoundException, IOException {
		try{
			Scanner scanner = new Scanner(System.in);
			insert_opTable(); //���oopcode

			String line = " "; //Ū���{���ɮרC�@��
			String tempstring = "";
			//--------------�ഫloc���ܼ�------------------------
			String temploc=" ";
			String loc="0"; //�N�@�}�l��m�]�m��0
			
			String EndStr="";//��������m
			
			int allLine=0; //�p������渹(�]�t����)
			int countAll=0; //�p���ڵ{���X���(�������ѫ���)
			String[][] mid=new String[1000][1000];//�x�s�����ɰ}�C
			
			FileReader fr=new FileReader("text.txt");
			BufferedReader br=new BufferedReader(fr);
			try{
				while (line != null){
					allLine++; //�}�l�C�@��
					String str="";
					line = br.readLine();
					
					String temp=line;
					char[] tempCh = temp.toCharArray(); //�NŪ�쪺�r������@�Ӧr��
					int count_dot=0;
					boolean checkX=false;
					for(int i=0;i<tempCh.length;i++){
						if(temp.charAt(i)=='.')  //�P�_��.�᭱�N�O���Ѫ���break
							break;
						if(temp.charAt(i)==','){
							count_dot++;
							checkX=true;
						}  //�P�_,���X��(������)
							
						if(count_dot>1){
							System.out.println("Line"+allLine+" ��J�L�h�r��");
							error++;
							break;
						}
						str=str+tempCh[i];  //��.���e���ۦb���_���ܦ��r��
					}
					if(checkX==true){
						for(int i=0;i<str.length();i++){
							if(str.charAt(i)=='X' && str.charAt(i-1)=='X' ){
								System.out.println("Line"+allLine+" indexed addressing�榡���~");
								error++;
							}
								
						}	
					}
					String str1 = str.replace("\t","#");
					String str2 = str1.replace(",","#");
					String str3 = str2.replaceAll("\\s+","#");
						
					int token=0; //�p��token��
					
					boolean dex=true;
					String addressMod= " ";
					//�P�_address mode, true=direct addressing, false=direct addressing
					String[] tokenStr=new String[10]; //�s���C��token��String
					String[] str3Split = str3.split("#");
					for (int i = 0; i < str3Split.length; i++){
						if(str3Split[i] != null && !str3Split[i].isEmpty()){
							tokenStr[token]=str3Split[i];
							//�Y�䤤�@��token�OX�ɴN�Oindexed addressing�A�ç�dex�אּfalse
							if(tokenStr[token].equals("X")){
								dex=false;
								addressMod="indexed";
							}
							token++;
						}
					}
					//����������dex�S���令false�B�b��token�����p�U
					if(dex==true && token>0){
						addressMod="direct";
					}
					
					//--------------�Ϥ�token(���olabel�Bmnemonic�Boperand)-----------------------//	
					String label=" ";
					String mnemonic=" ";
					String operand=" ";
					int count_mn=0;
					
					for(int i=0;i<token;i++){
						if(opcodeTable.containsKey(tokenStr[i])||tokenStr[i].equals("WORD") 
							||tokenStr[i].equals("RESW") 
							||tokenStr[i].equals("RESB") ||tokenStr[i].equals("BYTE")
							||tokenStr[i].equals("START")||tokenStr[i].equals("END")){
								count_mn++;
						}
						if(tokenStr[i].getBytes().length !=tokenStr[i].length()){
							System.out.println("Line"+allLine+" ���i��J����");
							error++;
							break;
						}
					}
					
					if(count_mn==0 && token!=0){
						System.out.println("Line"+allLine+" �S��mnemonic");
						error++;
					}
					if(count_mn>1 && token!=0){
						System.out.println("Line"+allLine+" ���ƭ�mnemonic");
						error++;
					}
						
					for(int i=0;i<token;i++){
						//�Ĥ@�ӬOmnemonic �ĤG��operand �Btoken�u�����
						if(i==0 && opcodeTable.containsKey(tokenStr[i]) && token==2 ){ 
							if(opcodeTable.containsKey(tokenStr[i+1]) 
							||tokenStr[i+1].equals("WORD") ||tokenStr[i+1].equals("RESW") 
							||tokenStr[i+1].equals("RESB") ||tokenStr[i+1].equals("BYTE")
							||tokenStr[i+1].equals("START")||tokenStr[i+1].equals("END") &&error==0){
								error++;
								System.out.println("Line"+allLine+" operand ���i�ϥ�mnemonic");
								//����ϥΨ��mnemonic
							}
							else{
								mnemonic=tokenStr[i]; //����mnemonic
								operand=tokenStr[i+1];
							}
							break;
						}
						//token=2�� �S��opcode�ɷ|���Ϳ��~
						if(token==2 && !opcodeTable.containsKey(tokenStr[i]) && 
							!opcodeTable.containsKey(tokenStr[i+1])){ 
							if(!tokenStr[i].equals("WORD") && !tokenStr[i].equals("RESW") 
							&& !tokenStr[i].equals("RESB") && !tokenStr[i].equals("BYTE")
							&& !tokenStr[i].equals("START")&& !tokenStr[i].equals("END") &&error==0){
								error++;
								System.out.println("Line"+allLine+" operand ���i�ϥ�mnemonic");
								break;
							}
						}
						if(tokenStr[i].equals("X") && dex==false){
							if(token==3){
								mnemonic=tokenStr[0];
								operand= tokenStr[1];
								break;
							}
							if(token==4){
								label=tokenStr[0];
								mnemonic=tokenStr[1];
								operand= tokenStr[2];
								sym_insert(loc,label,allLine+1);
								break;
							}
							else{
								error++;
								System.out.println("Line"+allLine+" ��J���~");
								break;
							}
							
						}
						//RSUB
						if(tokenStr[i].equals("RSUB")){
							if(token==1){
								mnemonic=tokenStr[i];
								break;
							}
							if(token==2){
								label=tokenStr[i-1];
								mnemonic=tokenStr[i];
								sym_insert(loc,label,allLine+1);
							}
							else{
								error++;
								System.out.println("Line"+allLine+" ��J���~");
								break;
							}
							
							break;
						}
						//�Ĥ@�ӬOlabel �ĤG�ӬOmnemonic �ĤT��operand
						if(opcodeTable.containsKey(tokenStr[i]) && i==1 && token==3){
							label=tokenStr[0];
							mnemonic=tokenStr[1];
							operand= tokenStr[2];
							sym_insert(loc,label,allLine+1);
							
							break;
						}
						if(tokenStr[i].equals("WORD") ||tokenStr[i].equals("RESW") 
							||tokenStr[i].equals("RESB") ||tokenStr[i].equals("BYTE")
							||tokenStr[i].equals("START")){
							if(token==3){
								label=tokenStr[0];
								mnemonic=tokenStr[1];
								operand= tokenStr[2];
								sym_insert(loc,label,allLine+1);
								break;
							}
							if(token==2){
								mnemonic=tokenStr[0];
								operand= tokenStr[1];
								break;
							}
							else{
								error++;
								System.out.println("Line"+allLine+" ��J���~");
								break;
							}
						}
						if(tokenStr[i].equals("END")){
							if(token==1){
								error++;
								System.out.println("Line"+allLine+" END�᭱�S��operand");
								mnemonic=tokenStr[0];
								break;
							}
							if(token==3){
								label=tokenStr[0];
								mnemonic=tokenStr[1];
								operand= tokenStr[2];
								sym_insert(loc,label,allLine+1);
								break;
							}
							if(token==2){
								mnemonic=tokenStr[0];
								operand= tokenStr[1];
								break;
							}
							else{
								error++;
								System.out.println("Line"+allLine+" ��J���~");
								break;
							}
						}
					}
					String opcode = opcodeTable.get(mnemonic);
					//--------------�Ϥ�token(���olabel�Bmnemonic�Boperand)-----------------------//
					
					//--------------�s�J������-----------------------//
					if(token>0){
						mid[countAll][0]=String.valueOf(allLine);
						mid[countAll][1]=loc;
						mid[countAll][2]=label;
						mid[countAll][3]=mnemonic;
						mid[countAll][4]=operand;
						mid[countAll][5]=addressMod;
						mid[countAll][6]=opcode;
						countAll++;
					}
					//--------------�s�J������-----------------------//
					
					
					//--------------�ഫloc------------------------
					//���oSTART��m
					if(mnemonic.equals("START")){
						loc = operand;
					}
					//��mnemonic �� WORD �ഫ
					if(opcodeTable.containsKey(mnemonic) || mnemonic.equals("WORD") ){
						int temps=Integer.parseInt(loc, 16); //��loc(�ؼ�)�ন10�i��
						temps=temps+3;
						loc = Integer.toHexString(temps).toUpperCase();
					}
					//BYTE�ഫ
					if(mnemonic.equals("BYTE")){
						int num=operand.length();
						int n_r=(num-3)%2;
						if(operand.charAt(1)!='\'' || operand.charAt(num-1)!='\''){//�ˬd��J�O�_���T
							error++;
							System.out.println("Line"+allLine+" ��J���~ �ʤ�' ");
						}
						if(operand.charAt(0)!='X' && operand.charAt(0)!='C'){//�ˬd��J�O�_���T
							error++;
							System.out.println("Line"+allLine+" BYTE�榡���~");
						}
						else{
							if(operand.charAt(0)=='C'){
								if(num>6){ //�i�H�W�L
									error++;
									System.out.println("Line"+allLine+" ��J���~");
								}
								int temps=Integer.parseInt(loc, 16); //��loc�ন10�i��
								int n=num-3;
								temps=temps+n;
								loc = Integer.toHexString(temps).toUpperCase();
								
							}
							if(operand.charAt(0)=='X' ){
								if(num>9 || n_r!=0){
									error++;
									System.out.println("Line"+allLine+" ��J���~");
								}
								int temps=Integer.parseInt(loc, 16); //��loc�ন10�i��
								int n=(num-3)*1/2;
								temps=temps+n;
								loc = Integer.toHexString(temps).toUpperCase();
							}
							
						}
					}
					//RESW �ഫ
					if(mnemonic.equals("RESW")){
						int temps=Integer.parseInt(loc, 16); //��loc(�ؼ�)�ন10�i��
						int temphex=Integer.valueOf(operand).intValue(); //RESW�᪺�Ʀr�নint
						temphex=3*temphex;
						temps=temps+temphex;
						loc = Integer.toHexString(temps).toUpperCase();
					}
					if(mnemonic.equals("RESB")){
						int temps=Integer.parseInt(loc, 16); //��loc�ন10�i��
						int temphex=Integer.valueOf(operand).intValue(); //RESB�᪺�Ʀr�নint
						temps=temps+temphex;
						loc = Integer.toHexString(temps).toUpperCase();
					}
					//--------------�ഫloc------------------------
					
				}
			}catch(Exception e){}
			br.close();
			
			//--------------��������-----------------------//
			if(!mid[0][3].equals("START")){ //�P�_START
				System.out.println("Line"+mid[countAll-1][0]+" START���~");
				error++;		
			}
			if(!mid[countAll-1][3].equals("END") &&error==0){ //�P�_�̫ᦳ�S��END
				System.out.println("Line"+mid[countAll-1][0]+" �S��END");
				error++;
			}
			//--------------��������-----------------------//
			
				//���o�����ɪ�operand ���ন���T��m�[��opcode
				for(int k=0;k<countAll;k++){
					if(mid[k][3].equals("START")) //Start���ΰ�
						continue;
					if(mid[k][3].equals("END")){ //END���ΰ�
						String temp=symTable.get(mid[k][4]);
						EndStr=changeStrToSix(temp);
						continue;
					}  
						
					if(mid[k][3].equals("RESW") || mid[k][3].equals("RESB"))//RESW���ΰ�
						continue;
					if(mid[k][3].equals("RSUB")){
						if(mid[k][4]!=" "){
							error++;
							System.out.println("Line"+mid[k][0]+" RSUB�ᤣ�঳operand");
						}
						mid[k][6]=mid[k][6]+"0000";
						continue;
					}
					if(mid[k][3].equals("WORD")){
						int temphex_int =Integer.parseInt(mid[k][4]);//�NWORD�᪺�Ʀr�নINT
						String temphex_obcode = Integer.toHexString(temphex_int);//����16�i��
						mid[k][6]=changeStrToSix(temphex_obcode);
						continue;
					}
					if(mid[k][3].equals("BYTE")){
						if(mid[k][4].charAt(0)=='C'){ //���oASCII�X
							mid[k][6]=changeASCII(mid[k][4]);
						}
						if(mid[k][4].charAt(0)=='X'){
							String temp_operand="";
							for (int i=2; i<mid[k][4].length()-1;i++){
								char c = mid[k][4].charAt(i);
								temp_operand=temp_operand+c;
							}
							mid[k][6]=temp_operand;
						}
						continue;
					}
					else{
						if(!symTable.containsKey(mid[k][4]) && error==0){ //Label���w�q
							error++;
							System.out.println("Line"+mid[k][0]+" operand���w�q");
							continue;
						}
						if(mid[k][5].equals("direct")){
							String tempSYM = symTable.get(mid[k][4]);
							mid[k][6]=mid[k][6]+tempSYM;
						}
						if(mid[k][5].equals("indexed")){
							String tempSYM = symTable.get(mid[k][4]);
							int temps=Integer.parseInt(tempSYM, 16); //��tempSYM�ন10�i��
							int temps_pluse=Integer.parseInt("8000", 16);
							temps=temps+temps_pluse;
							tempSYM = Integer.toHexString(temps).toUpperCase();
							mid[k][6]=mid[k][6]+tempSYM;
						}
						continue;
					}
				}
				
				
				//--------------�L�Xobject program-----------------------//
			if(error<=0){
				System.out.println("������");
				for(int k=0;k<countAll;k++){
					for(int i=0;i<7;i++){
						System.out.print(mid[k][i]+"\t");
					}
					System.out.println();
				}
				
				File saveFile=new File("output.txt");
				try{
					FileWriter fwriter=new FileWriter(saveFile);
					//�L�XH����
					String len = getLength(mid[1][1],mid[countAll-1][1]); //���o�{���`����
					len = changeStrToSix(len).toUpperCase(); //�N�`���׫e���������ɹs
					mid[0][2] = changeStrToSix_back(mid[0][2]); //��{���W�٫᭱�ɪťզ�6���
					mid[0][4] = changeStrToSix(mid[0][4]); //�q����LOC�}�l
					fwriter.write("H"+mid[0][2]+mid[0][4]+len);
					fwriter.write("\r\n");
					System.out.println("H"+mid[0][2]+mid[0][4]+len);
					
					//�L�XT����(object codes)
					String TCode=" "; //�x�sT�M�q���@��}�l
					String halfCodes="";  //�x�sT������object code
					int count_codes=0; //�p��s�F�X��object code
					int count_byte=0; //�p��code����
					String[] mod=new String[countAll]; //�x�s�ݭnmod����m
					int needMod=0; //�����ݭnMOD���ƶq
					for(int i=0;i<countAll;i++){
						if(mid[i][6]==null && count_codes==0) //�S��object code
							continue;
						
						if(mid[i][6]!=null && count_codes==0){ //�Ĥ@��object codes (�p��s�F�X��object code����0)
							TCode="T"+changeStrToSix(mid[i][1]);  //���m�MT�[�b�@�_
						}
						if(mid[i][6]!=null && count_codes<10){ //����NULL�B�p��10(�@��u��s10��)
							if(!mid[i][3].equals("RESW") && !mid[i][3].equals("RESB")
								&& !mid[i][3].equals("WORD")&& !mid[i][3].equals("BYTE")
								&& !mid[i][3].equals("RSUB")){
								//�����H�W�h�ݭn�i��ץ�
								int temps=Integer.parseInt(mid[i][1], 16); //���o���ץ���m
								temps=temps+1;
								String str_mod = Integer.toHexString(temps).toUpperCase();
								mod[needMod]=changeStrToSix(str_mod);
								needMod++;
							}
							halfCodes=halfCodes+mid[i][6];
							count_codes++;
							int num=mid[i][6].length();
							num=num*1/2;
							count_byte=count_byte+num;
						}
						if(mid[i][6]!=null && count_codes>=10){ //����NULL���j��10(�@��u��s10��)
							String countHowMany = Integer.toHexString(count_byte).toUpperCase();
							countHowMany=changeStrToTwo(countHowMany);
							fwriter.write(TCode+countHowMany+halfCodes);
							fwriter.write("\r\n");
							System.out.println(TCode+countHowMany+halfCodes);
							TCode=" ";
							halfCodes="";
							count_codes=0;
							count_byte=0;
						}
						if(mid[i][6]==null && count_codes>0){
							String countHowMany = Integer.toHexString(count_byte).toUpperCase();
							countHowMany=changeStrToTwo(countHowMany);
							fwriter.write(TCode+countHowMany+halfCodes);
							fwriter.write("\r\n");
							System.out.println(TCode+countHowMany+halfCodes);
							TCode=" ";
							halfCodes="";
							count_codes=0;
							count_byte=0;
						}
						
					}
					
					//�L�XM����
					for(int i=0;i<needMod;i++){
						System.out.println("M"+mod[i]+"04+"+mid[0][2]);
						fwriter.write("M"+mod[i]+"04+"+mid[0][2]);
						fwriter.write("\r\n");
					}
					
					//�L�XE����
					System.out.println("E"+EndStr);
					fwriter.write("E"+EndStr);
					fwriter.write("\r\n");
					
					fwriter.close();
				}catch(Exception e){}
			}
			//--------------�L�Xobject program-----------------------//
		}catch(Exception e){}
    }

//--------------�[�JsymTable-----------------------//
	public static void sym_insert(String loc,String s,int line) {
		boolean laboo;
		boolean add;
		line--;
		if(opcodeTable.containsKey(s)==true){
			System.out.println("Line"+line+" label����ϥ�mnemonic");
			error++;
		}
		else{
			try {
				laboo=symTable.containsValue(loc);
				add=symTable.containsKey(s);
				if((laboo==false) && (add==false))
					symTable.put(s,loc);
				else{
					if(error==0){
						System.out.println("Line"+line+" label ����");
						error++;
					}
				}
			}catch (Exception ex) {}
		}
	}
//--------------�[�JsymTable-----------------------//

//--------------���oopcode------------------------//
	public static void insert_opTable()throws IOException {
		FileReader opcodeFR=new FileReader("opcode.txt");
		BufferedReader opcodeBR=new BufferedReader(opcodeFR);
		
		String opline = " ";
		String tempstring = "";
		String[] tempArray= new String[2];
		
		while (opline != null){
			int i = 0;
			opline = opcodeBR.readLine();
			tempstring = opline; 
			if(tempstring != null)
				tempArray = tempstring.split("\\s");
			opcodeTable.put(tempArray[i],tempArray[i+1]);
		}
		opcodeBR.close();
	}
//--------------���oopcode------------------------//

//--------------���͵{������------------------------//
	public static String getLength(String start,String end) {
		//�N�Y���ন10�i��
		int int_start=Integer.parseInt(start, 16);
		int int_end=Integer.parseInt(end, 16);
		int len=int_end-int_start;
		String str_len = Integer.toHexString(len);
		return str_len;
	}
//--------------���͵{������------------------------//

//--------------�r���ন6��Ʀr��(�e���[0)------------------------//
	public static String changeStrToSix(String str) {
		int str_length=str.length(); //���o�r�����
		int add=6-str_length; //add���ݭn�ɥR0���ƶq
		for(int i=0;i<add;i++)  //�b�e���[�J0
			str="0"+str;
		return str;
	}
//--------------�r���ন6��Ʀr��(�e���[0)------------------------//

//--------------�r���ন2��Ʀr��(�e���[0)------------------------//
	public static String changeStrToTwo(String str) {
		int str_length=str.length(); //���o�r�����
		int add=2-str_length; //add���ݭn�ɥR0���ƶq
		for(int i=0;i<add;i++)  //�b�e���[�J0
			str="0"+str;
		return str;
	}
//--------------�r���ন2��Ʀr��(�e���[0)------------------------//

//--------------�r���ন6��Ʀr��(�᭱�[�ť�)------------------------//
	public static String changeStrToSix_back(String str) {
		int str_length=str.length(); //���o�r�����
		int add=6-str_length; //add���ݭn�ɥR0���ƶq
		for(int i=0;i<add;i++)  //�b�e���[�J0
			str=str+" ";
		return str;
	}
//--------------�r���ন6��Ʀr��(�᭱�[�ť�)------------------------//

//--------------�r����ASCII(C'XX')------------------------//
	public static String changeASCII(String str) {
		String tempASCII="";
		for (int i=2; i<str.length()-1;i++){
			char c = str.charAt(i);  //���o'�������r��
			int strToASCII = (int)c; //�নASCII
			String temphex_obcode = Integer.toHexString(strToASCII).toUpperCase();//����16�i��
			tempASCII=tempASCII+temphex_obcode;
		}
		return tempASCII;
	}
//--------------�r����ASCII------------------------//

}