package ergasia;
import ithakimodem.*;

import java.io.FileOutputStream;

//import ithakimodem.*;


public class GpsFunction {
	
public String getCoordinates(Modem modem,String GpsCode){
	
	FileOutputStream text = null;
	int input_read;
	String START_CODE="START ITHAKI GPS TRACKING";
	String STOP_CODE="STOP ITHAKI GPS TRACKING";
	int count=0;
	
	//-------���������� ��� �� ������� ��� �������� ��� GPGGA �������----------
	String GPGGA="$GPGGA";
	boolean read_time=false;       //��������� ��� �� ��������� � �������� ��� ����
	boolean access_latitude=false; //��� �� ���������� �� ���������� ������
	boolean access_longitude=false;//��� �� ���������� �� ���������� �����
	int mult_value=1000;           
	int time_value=0;              //� ��� ��� ��������� �������
	int previous_package_time=0;   //� ��� ��� ����������� �������
	int space_count=0;             
	int gpg_count=0;
	int latitude_count=0;
	int longitude_count=0;
	int repeat_string_count=0; //��������� ��� ������ ��� ������ ��� ������� (����� �������� ��� 9)
    int high_value=0;          //�� ����� 4 ����� ��� �������������
    int low_value=0;           //�� �������� 4 ����� ��� �� ����� ������� ��� �������������� �� �� 0.006
	String str1="0";
	String str2="0";
	String str3="T=";
	String str4="0";
	String string_with_code="0";
	String previous_string="0";
	//-------------------------------------------------------------------------
	
	modem.write( GpsCode.getBytes());//��������� ��� ���������� ������ ���� server
	
	//����������� ��� ������ ��� �� ��������
	try{
		text= new FileOutputStream("GPStrackingdedomena.txt",true);
		
	}catch (Exception x) {
		System.out.println("It appears a Exception");
         }
	
	//�������� ��� ��� ���� ��� ���������,������ ��� ����������
	//��� ���������� "START ITHAKI GPS TRACKING"
	while(true)
	{
		input_read=modem.read();
		if(START_CODE.charAt(count)==(char) input_read){
			if(count==24){
		 	 count=0;	
			 break;
			 }
		count++; 	
	    }
		 else{
		      count=0;
		    }
	}
	//��� ������ ���� ��������� �������� ���� ��� ������
	//��� ��� ���������� ��� ���������� ��� �����
	for(int i=0;i<23;i++){
	try {
   	 text.write(START_CODE.charAt(i));
   	 
 	    }catch (Exception x) {
 	    	
            }
	}
	
//���� �������� ���� ��� ������ �� �������� ��� ���������� ��� ��� server
//����� �� ����������� �� string ������:"STOP ITHAKI GPS TRACKING"
//���������� �� ��� ������ ���� ������������ ��� ���������� � ������
//�������� �� �������� ��� ��� �������� ��� ��������� ��� ���������� ��� 
//���������� $GPGGA ��� ���� �������� �� ���� ��� ��� ��� ��� �������������
//��� �� � ������ ������ ���������� ������� ����� ����������� ��� 4sec ��� �� 
//������������� ���� ����� ������������ ���� ������� ��� ������������� ��� ����
//���� ��������� string_with_code ��� �� ������������ ������ ��� string ��� 
//������ T=.....T=....T=.... �� ����� �� ���������� � ��������� ��� getCoordinates()
  while(true)
  {
	input_read=modem.read();
	
	//����� �� �������� ��� ������
	try {
    	 text.write(input_read);
    	 
  	    }catch (Exception x) {
  	    	System.out.println("It appears a Exception");
             break;
             }
	
//=========������� ��� ��� ������� ������� GPGGA ��� ���������� ==========
//======================����������� ��� �������������=====================
	
//���� ��� ���������� ���������� "GPGGA"
if((GPGGA.charAt(gpg_count)==input_read)&&(repeat_string_count<9))
{
	
  gpg_count++;
  if(gpg_count==6){      //���� ������ ��� ���������� "GPGGA"
	  read_time=true;    //������� �������� ���� �������� if ��� �� ����� ����������� ��� ����
	  gpg_count=0;       //������������� ��� ������� ��� �� ����� ������� ������

  }	
    }else{
	gpg_count=0;
    }

//������� ��� ���
if(read_time&&(repeat_string_count<9))
{
	space_count++;
	
    //�� ������� �� 3 ��������� ����� ��� ����,����� ������ ���� 
	//��� ��������� �� ����� �� ������ ���������� ������� ������� ��� 10�����
	//�� ����� ����� ������� �� ����� ��� �� ���� 2 �� �������������
	//��� � ������� ���� ��� ���������� ����� 959(=9:59=10�����)
	if((space_count==6)||(space_count==7)||(space_count==8))
	 { 
	  mult_value=mult_value/10;
	  time_value+=(input_read-48)*mult_value; //������������ ��� ������ ASCII �� 3����� ������ 	  
	 
	  
	   if(space_count==8)//���� ������ ������� ��� ��� ��� �������
	   {
		  read_time=false; 
		  space_count=0;
		  
		  if((time_value-previous_package_time)>=4){//�� ���� ������ �� ������� 4sec
			  access_latitude=true; 
			  mult_value=1000;//��� ��� ���������� ��� �������������
			  previous_package_time=time_value;
			  time_value=0;
			 
		  }else if((time_value-previous_package_time)<=-4)//���� ����� ��� ��� ��������� ������� ����
		      {                                          //��� ��� ������� ���� ��� ��� ����� 959 ��� �����
			   int x=960;
			   x+=time_value;
			   if((x-previous_package_time)>=4){
				   access_latitude=true;
				   mult_value=1000;
				   previous_package_time=time_value;
				   time_value=0;
			       }
		      }else{
		          mult_value=1000;//���� �� ���� ����� ������� ������
		          time_value=0;
		      }
	   }
		  
     }		
}
//���������� ������			
if(access_latitude&&(repeat_string_count<9)){
	latitude_count++;

 if((latitude_count>=7)&&(latitude_count<=10))	 
 {
	 high_value+=(input_read-48)*mult_value;
	 mult_value=mult_value/10;
	 
	 if(latitude_count==10) 
		 {		  
		  str1=Integer.toString(high_value);
		  mult_value=1000;
		  high_value=0;
		 }
 }
 

 if((latitude_count>=12)&&(latitude_count<=15))	 
 {
 	 low_value+=(input_read-48)*mult_value;	 
 	 mult_value=mult_value/10;
	 
 
	 if(latitude_count==15)
	 {
				
	   double a=low_value*0.006;
	   low_value=(int) Math.round(a);
	   str2=Integer.toString(low_value);
	   str1+=str2;
	   access_latitude=false;
	   access_longitude=true;
	   mult_value=1000;
	   latitude_count=0;
	   low_value=0;
	 }
 
 }
	
}//end if() latitude

//���������� �����
if(access_longitude&&(repeat_string_count<9)){
longitude_count++;

 if((longitude_count>=6)&&(longitude_count<=9))	 
 {
	 high_value+=(input_read-48)*mult_value;
	 mult_value=mult_value/10;
	 
	 if(longitude_count==9) 
		 {
		  mult_value=1000;
		  str3+=Integer.toString(high_value);
		 
		  high_value=0;
		 }
 }
	
 if((longitude_count>=11)&&(longitude_count<=14))	 
 {
 	 low_value+=(input_read-48)*mult_value;	 
 	 mult_value=mult_value/10;
	 
 
	 if(longitude_count==14)
	 {
				
	   double a=low_value*0.006; 
	   low_value=(int) Math.round(a);
	   
	   str4=Integer.toString(low_value);
	   str3+=str4;
	   str3+=str1;
	   if(repeat_string_count==0)//�� ����� � ����� ���� ����������� ��� ���������
	      { 
		    string_with_code=str3;
	        previous_string=str3;
	        repeat_string_count++;
	      }
	   else if(!previous_string.equals(str3))//�� ��� ������ ����� ���� ��� ���� ������
	  	  { 		                         //���������� ��� ������ ���� ����� ���������
		   previous_string=str3;
		   string_with_code+=str3;
		   repeat_string_count++;
		   }
	   
	   str3="T=";
	   access_longitude=false;
	   mult_value=1000;
	   longitude_count=0;
	   low_value=0;
	   
	 }
 
 }
	
}//end if() longitude
	
	
//===================================================	
	//�� ����� ��� ���������� ������ �������
	if(STOP_CODE.charAt(count)==(char) input_read){
		if(count==23) break;
		count++; 	
	    }
	     else{
	          count=0;
	         }
	
	}//����� while
    
	//��� ����� ��� ��������� ������ ��� ������
	try {
   	 text.write((char) 13);
   	  	    }catch (Exception x) {}

return (string_with_code+"\r");
}
	
	
	
	

}
