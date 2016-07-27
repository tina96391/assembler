import java.io.*;
import java.util.*;

public class ass {
	static Hashtable< String, String > symTable = new Hashtable< String, String >();
	static Hashtable< String, String > opcodeTable = new Hashtable< String, String >();
	static int error=0;//計算錯誤數量
	
    public static void main(String[] args)throws FileNotFoundException, IOException {
		try{
			Scanner scanner = new Scanner(System.in);
			insert_opTable(); //取得opcode

			String line = " "; //讀取程式檔案每一行
			String tempstring = "";
			//--------------轉換loc用變數------------------------
			String temploc=" ";
			String loc="0"; //將一開始位置設置為0
			
			String EndStr="";//結束的位置
			
			int allLine=0; //計算全部行號(包含註解)
			int countAll=0; //計算實際程式碼行數(扣掉註解後行數)
			String[][] mid=new String[1000][1000];//儲存中間檔陣列
			
			FileReader fr=new FileReader("text.txt");
			BufferedReader br=new BufferedReader(fr);
			try{
				while (line != null){
					allLine++; //開始每一行
					String str="";
					line = br.readLine();
					
					String temp=line;
					char[] tempCh = temp.toCharArray(); //將讀到的字串切成一個字元
					int count_dot=0;
					boolean checkX=false;
					for(int i=0;i<tempCh.length;i++){
						if(temp.charAt(i)=='.')  //判斷到.後面就是註解直接break
							break;
						if(temp.charAt(i)==','){
							count_dot++;
							checkX=true;
						}  //判斷,有幾個(除錯用)
							
						if(count_dot>1){
							System.out.println("Line"+allLine+" 輸入過多逗號");
							error++;
							break;
						}
						str=str+tempCh[i];  //把.之前的自在接起來變成字串
					}
					if(checkX==true){
						for(int i=0;i<str.length();i++){
							if(str.charAt(i)=='X' && str.charAt(i-1)=='X' ){
								System.out.println("Line"+allLine+" indexed addressing格式錯誤");
								error++;
							}
								
						}	
					}
					String str1 = str.replace("\t","#");
					String str2 = str1.replace(",","#");
					String str3 = str2.replaceAll("\\s+","#");
						
					int token=0; //計算token數
					
					boolean dex=true;
					String addressMod= " ";
					//判斷address mode, true=direct addressing, false=direct addressing
					String[] tokenStr=new String[10]; //存取每個token的String
					String[] str3Split = str3.split("#");
					for (int i = 0; i < str3Split.length; i++){
						if(str3Split[i] != null && !str3Split[i].isEmpty()){
							tokenStr[token]=str3Split[i];
							//若其中一個token是X時就是indexed addressing，並把dex改為false
							if(tokenStr[token].equals("X")){
								dex=false;
								addressMod="indexed";
							}
							token++;
						}
					}
					//全部取完後dex沒有改成false且在有token的情況下
					if(dex==true && token>0){
						addressMod="direct";
					}
					
					//--------------區分token(取得label、mnemonic、operand)-----------------------//	
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
							System.out.println("Line"+allLine+" 不可輸入中文");
							error++;
							break;
						}
					}
					
					if(count_mn==0 && token!=0){
						System.out.println("Line"+allLine+" 沒有mnemonic");
						error++;
					}
					if(count_mn>1 && token!=0){
						System.out.println("Line"+allLine+" 有數個mnemonic");
						error++;
					}
						
					for(int i=0;i<token;i++){
						//第一個是mnemonic 第二個operand 且token只有兩個
						if(i==0 && opcodeTable.containsKey(tokenStr[i]) && token==2 ){ 
							if(opcodeTable.containsKey(tokenStr[i+1]) 
							||tokenStr[i+1].equals("WORD") ||tokenStr[i+1].equals("RESW") 
							||tokenStr[i+1].equals("RESB") ||tokenStr[i+1].equals("BYTE")
							||tokenStr[i+1].equals("START")||tokenStr[i+1].equals("END") &&error==0){
								error++;
								System.out.println("Line"+allLine+" operand 不可使用mnemonic");
								//不能使用兩個mnemonic
							}
							else{
								mnemonic=tokenStr[i]; //紀錄mnemonic
								operand=tokenStr[i+1];
							}
							break;
						}
						//token=2時 沒有opcode時會產生錯誤
						if(token==2 && !opcodeTable.containsKey(tokenStr[i]) && 
							!opcodeTable.containsKey(tokenStr[i+1])){ 
							if(!tokenStr[i].equals("WORD") && !tokenStr[i].equals("RESW") 
							&& !tokenStr[i].equals("RESB") && !tokenStr[i].equals("BYTE")
							&& !tokenStr[i].equals("START")&& !tokenStr[i].equals("END") &&error==0){
								error++;
								System.out.println("Line"+allLine+" operand 不可使用mnemonic");
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
								System.out.println("Line"+allLine+" 輸入錯誤");
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
								System.out.println("Line"+allLine+" 輸入錯誤");
								break;
							}
							
							break;
						}
						//第一個是label 第二個是mnemonic 第三個operand
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
								System.out.println("Line"+allLine+" 輸入錯誤");
								break;
							}
						}
						if(tokenStr[i].equals("END")){
							if(token==1){
								error++;
								System.out.println("Line"+allLine+" END後面沒有operand");
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
								System.out.println("Line"+allLine+" 輸入錯誤");
								break;
							}
						}
					}
					String opcode = opcodeTable.get(mnemonic);
					//--------------區分token(取得label、mnemonic、operand)-----------------------//
					
					//--------------存入中間檔-----------------------//
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
					//--------------存入中間檔-----------------------//
					
					
					//--------------轉換loc------------------------
					//取得START位置
					if(mnemonic.equals("START")){
						loc = operand;
					}
					//有mnemonic 及 WORD 轉換
					if(opcodeTable.containsKey(mnemonic) || mnemonic.equals("WORD") ){
						int temps=Integer.parseInt(loc, 16); //把loc(目標)轉成10進位
						temps=temps+3;
						loc = Integer.toHexString(temps).toUpperCase();
					}
					//BYTE轉換
					if(mnemonic.equals("BYTE")){
						int num=operand.length();
						int n_r=(num-3)%2;
						if(operand.charAt(1)!='\'' || operand.charAt(num-1)!='\''){//檢查輸入是否正確
							error++;
							System.out.println("Line"+allLine+" 輸入錯誤 缺少' ");
						}
						if(operand.charAt(0)!='X' && operand.charAt(0)!='C'){//檢查輸入是否正確
							error++;
							System.out.println("Line"+allLine+" BYTE格式錯誤");
						}
						else{
							if(operand.charAt(0)=='C'){
								if(num>6){ //可以超過
									error++;
									System.out.println("Line"+allLine+" 輸入錯誤");
								}
								int temps=Integer.parseInt(loc, 16); //把loc轉成10進位
								int n=num-3;
								temps=temps+n;
								loc = Integer.toHexString(temps).toUpperCase();
								
							}
							if(operand.charAt(0)=='X' ){
								if(num>9 || n_r!=0){
									error++;
									System.out.println("Line"+allLine+" 輸入錯誤");
								}
								int temps=Integer.parseInt(loc, 16); //把loc轉成10進位
								int n=(num-3)*1/2;
								temps=temps+n;
								loc = Integer.toHexString(temps).toUpperCase();
							}
							
						}
					}
					//RESW 轉換
					if(mnemonic.equals("RESW")){
						int temps=Integer.parseInt(loc, 16); //把loc(目標)轉成10進位
						int temphex=Integer.valueOf(operand).intValue(); //RESW後的數字轉成int
						temphex=3*temphex;
						temps=temps+temphex;
						loc = Integer.toHexString(temps).toUpperCase();
					}
					if(mnemonic.equals("RESB")){
						int temps=Integer.parseInt(loc, 16); //把loc轉成10進位
						int temphex=Integer.valueOf(operand).intValue(); //RESB後的數字轉成int
						temps=temps+temphex;
						loc = Integer.toHexString(temps).toUpperCase();
					}
					//--------------轉換loc------------------------
					
				}
			}catch(Exception e){}
			br.close();
			
			//--------------部分除錯-----------------------//
			if(!mid[0][3].equals("START")){ //判斷START
				System.out.println("Line"+mid[countAll-1][0]+" START錯誤");
				error++;		
			}
			if(!mid[countAll-1][3].equals("END") &&error==0){ //判斷最後有沒有END
				System.out.println("Line"+mid[countAll-1][0]+" 沒有END");
				error++;
			}
			//--------------部分除錯-----------------------//
			
				//取得中間檔的operand 並轉成正確位置加到opcode
				for(int k=0;k<countAll;k++){
					if(mid[k][3].equals("START")) //Start不用動
						continue;
					if(mid[k][3].equals("END")){ //END不用動
						String temp=symTable.get(mid[k][4]);
						EndStr=changeStrToSix(temp);
						continue;
					}  
						
					if(mid[k][3].equals("RESW") || mid[k][3].equals("RESB"))//RESW不用動
						continue;
					if(mid[k][3].equals("RSUB")){
						if(mid[k][4]!=" "){
							error++;
							System.out.println("Line"+mid[k][0]+" RSUB後不能有operand");
						}
						mid[k][6]=mid[k][6]+"0000";
						continue;
					}
					if(mid[k][3].equals("WORD")){
						int temphex_int =Integer.parseInt(mid[k][4]);//將WORD後的數字轉成INT
						String temphex_obcode = Integer.toHexString(temphex_int);//換成16進位
						mid[k][6]=changeStrToSix(temphex_obcode);
						continue;
					}
					if(mid[k][3].equals("BYTE")){
						if(mid[k][4].charAt(0)=='C'){ //取得ASCII碼
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
						if(!symTable.containsKey(mid[k][4]) && error==0){ //Label未定義
							error++;
							System.out.println("Line"+mid[k][0]+" operand未定義");
							continue;
						}
						if(mid[k][5].equals("direct")){
							String tempSYM = symTable.get(mid[k][4]);
							mid[k][6]=mid[k][6]+tempSYM;
						}
						if(mid[k][5].equals("indexed")){
							String tempSYM = symTable.get(mid[k][4]);
							int temps=Integer.parseInt(tempSYM, 16); //把tempSYM轉成10進位
							int temps_pluse=Integer.parseInt("8000", 16);
							temps=temps+temps_pluse;
							tempSYM = Integer.toHexString(temps).toUpperCase();
							mid[k][6]=mid[k][6]+tempSYM;
						}
						continue;
					}
				}
				
				
				//--------------印出object program-----------------------//
			if(error<=0){
				System.out.println("中間檔");
				for(int k=0;k<countAll;k++){
					for(int i=0;i<7;i++){
						System.out.print(mid[k][i]+"\t");
					}
					System.out.println();
				}
				
				File saveFile=new File("output.txt");
				try{
					FileWriter fwriter=new FileWriter(saveFile);
					//印出H部分
					String len = getLength(mid[1][1],mid[countAll-1][1]); //取得程式總長度
					len = changeStrToSix(len).toUpperCase(); //將總長度前面不夠的補零
					mid[0][2] = changeStrToSix_back(mid[0][2]); //把程式名稱後面補空白成6位數
					mid[0][4] = changeStrToSix(mid[0][4]); //從哪個LOC開始
					fwriter.write("H"+mid[0][2]+mid[0][4]+len);
					fwriter.write("\r\n");
					System.out.println("H"+mid[0][2]+mid[0][4]+len);
					
					//印出T部分(object codes)
					String TCode=" "; //儲存T和從哪一行開始
					String halfCodes="";  //儲存T中間的object code
					int count_codes=0; //計算存了幾個object code
					int count_byte=0; //計算code長度
					String[] mod=new String[countAll]; //儲存需要mod的位置
					int needMod=0; //紀錄需要MOD的數量
					for(int i=0;i<countAll;i++){
						if(mid[i][6]==null && count_codes==0) //沒有object code
							continue;
						
						if(mid[i][6]!=null && count_codes==0){ //第一個object codes (計算存了幾個object code的為0)
							TCode="T"+changeStrToSix(mid[i][1]);  //把位置和T加在一起
						}
						if(mid[i][6]!=null && count_codes<10){ //不為NULL且小於10(一行只能存10個)
							if(!mid[i][3].equals("RESW") && !mid[i][3].equals("RESB")
								&& !mid[i][3].equals("WORD")&& !mid[i][3].equals("BYTE")
								&& !mid[i][3].equals("RSUB")){
								//不為以上則需要進行修正
								int temps=Integer.parseInt(mid[i][1], 16); //取得須修正位置
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
						if(mid[i][6]!=null && count_codes>=10){ //不為NULL但大於10(一行只能存10個)
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
					
					//印出M部分
					for(int i=0;i<needMod;i++){
						System.out.println("M"+mod[i]+"04+"+mid[0][2]);
						fwriter.write("M"+mod[i]+"04+"+mid[0][2]);
						fwriter.write("\r\n");
					}
					
					//印出E部分
					System.out.println("E"+EndStr);
					fwriter.write("E"+EndStr);
					fwriter.write("\r\n");
					
					fwriter.close();
				}catch(Exception e){}
			}
			//--------------印出object program-----------------------//
		}catch(Exception e){}
    }

//--------------加入symTable-----------------------//
	public static void sym_insert(String loc,String s,int line) {
		boolean laboo;
		boolean add;
		line--;
		if(opcodeTable.containsKey(s)==true){
			System.out.println("Line"+line+" label不能使用mnemonic");
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
						System.out.println("Line"+line+" label 重複");
						error++;
					}
				}
			}catch (Exception ex) {}
		}
	}
//--------------加入symTable-----------------------//

//--------------取得opcode------------------------//
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
//--------------取得opcode------------------------//

//--------------產生程式長度------------------------//
	public static String getLength(String start,String end) {
		//將頭尾轉成10進位
		int int_start=Integer.parseInt(start, 16);
		int int_end=Integer.parseInt(end, 16);
		int len=int_end-int_start;
		String str_len = Integer.toHexString(len);
		return str_len;
	}
//--------------產生程式長度------------------------//

//--------------字串轉成6位數字串(前面加0)------------------------//
	public static String changeStrToSix(String str) {
		int str_length=str.length(); //取得字串長度
		int add=6-str_length; //add為需要補充0的數量
		for(int i=0;i<add;i++)  //在前面加入0
			str="0"+str;
		return str;
	}
//--------------字串轉成6位數字串(前面加0)------------------------//

//--------------字串轉成2位數字串(前面加0)------------------------//
	public static String changeStrToTwo(String str) {
		int str_length=str.length(); //取得字串長度
		int add=2-str_length; //add為需要補充0的數量
		for(int i=0;i<add;i++)  //在前面加入0
			str="0"+str;
		return str;
	}
//--------------字串轉成2位數字串(前面加0)------------------------//

//--------------字串轉成6位數字串(後面加空白)------------------------//
	public static String changeStrToSix_back(String str) {
		int str_length=str.length(); //取得字串長度
		int add=6-str_length; //add為需要補充0的數量
		for(int i=0;i<add;i++)  //在前面加入0
			str=str+" ";
		return str;
	}
//--------------字串轉成6位數字串(後面加空白)------------------------//

//--------------字串轉ASCII(C'XX')------------------------//
	public static String changeASCII(String str) {
		String tempASCII="";
		for (int i=2; i<str.length()-1;i++){
			char c = str.charAt(i);  //取得'中間的字元
			int strToASCII = (int)c; //轉成ASCII
			String temphex_obcode = Integer.toHexString(strToASCII).toUpperCase();//換成16進位
			tempASCII=tempASCII+temphex_obcode;
		}
		return tempASCII;
	}
//--------------字串轉ASCII------------------------//

}