package ergasia;
import ithakimodem.*;

import java.io.FileOutputStream;


public class Packages {
	
public void receivePackeges(Modem modem,String packageCode){
	
  FileOutputStream packageText=null;
  String START_CODE="PSTART";
  String STOP_CODE="PSTOP";
  int input_read;
  int count=0;
  
//��������� ��� ���������� ������ ���� server
modem.write( packageCode.getBytes());
			
 try{
	   packageText= new FileOutputStream("dedomenapackage.txt",true);
	  }catch (Exception x) {
		System.out.println("It appears a Exception");
       }
	
//�������� ��� ��� ���� ��� ���������,������ ��� ����������
//��� ���������� "PSTART"
while(true)
{
	input_read=modem.read();

	if(START_CODE.charAt(count)==(char) input_read)
	{
	  if(count==5)
	   {
	    count=0;	
	    break;
	   }
		  
	 count++; 	
    }
	 else{
	      count=0;
	     }
}
	
//��� ������ ��������� �������� ���� ��� ������
//��� ��� ���������� ��� ���������� ��� �����
for(int i=0;i<5;i++){
try {
 packageText.write(START_CODE.charAt(i));
 	 
    }catch (Exception x) {}
}
	
//���� �������� ���� ��� ������ �� �������� ��� ���������� ��� ��� server
//����� �� ����������� �� string ������:"PSTOP"
while(true)
{
input_read=modem.read();
 
 //����� �� �������� ��� ������
try {
	 packageText.write(input_read);
    }catch (Exception x) {
	   	System.out.println("It appears a Exception");
         break;
       }
	
//�� ����� ��� ���������� ������ �������
if(STOP_CODE.charAt(count)==(char) input_read){
	if(count==4) break;
	count++; 	
    }
     else{
          count=0;
         }
		
}//����� while
	
//��� ����� ��� ��������� ������ ��� ������
try {
	 packageText.write((char) 13);
  	}catch (Exception x) {}

}//����� receivePackeges

}
