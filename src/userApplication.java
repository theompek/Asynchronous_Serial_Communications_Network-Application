/*
                             ������ ����������� � 7� �������
				    	       �������� ��������� ���:8200 
					
� ������� ��� �������� ���� ������ ��� ���� ���������� �� ��� ������� ��� ������������ Eclipce.
�� ��������� ����������� ��� 5 ������� ��� � ���� ����� �������� ��� ��������� � ����� ������� ������ 
��� ���� ��������� ��� �������� ���� �������.��� ����� ������ ����� ������������� ���� �� ������� ����
��� ��� ������� ��� ����������� ����.

����� VirtualModem:� ��������� virtualModem �������� ��� ��������� main ���� ���� ����� ��������� �� 
��������� ����������� ��� �� ���������� �� ��������� ��� ��������.�������� ���� ���� main �������� ��� 
�� ���������� ��������� ��� ������ ��������� ��� ������������.

����� Packages:�������� ��� ��������� receivePackeges � ����� ������� ��� ������ echoPacke ��� ��������
�� ������ ��� �� ���������� �� ��� ������ �� ����� dedomenapackage.txt.

����� ReadPicture:�������� ��� ������ �� ��� ����� ���������� �� �������� ��� ������������ ��� ��� 
����� ��� �� �������������� ��� ������.

����� GpsFunction:� ��������� getCoordinates ��� ���������� ���� ���� ����� �������� �� �������� ��� 
������������ ��� ������� ��� GPS tracking ��� �� ���������� �� ��� ������,�������� ���������� ��������� 
�� ������ ��� ���������� ��� ������������� ���� ���� ��������� ���� �����  String �=..�=..�=... ��� 9 ������.

����� ManageRandomErrors:� ��������� manageErrors ���������� �� �������� ��� �� ������� ��� ���������� ARQ 
�� ��� ������ ��� ���������� ������� �� 16����� �����(���� ��� ������ XOR) ��� �� ��������� �� �� ����� FCS 
��� �� ��� ����� ��� ���������� ��� ������ ���� FALSE,����������� ���������� ��� ���� TRUE.  
*/

//===============================================����� VirtualModem==========================================

package ergasia;

import java.util.concurrent.TimeUnit;

import ithakimodem.*;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class virtualModem {
	 public static void main(String[] param) {
	 (new virtualModem()).demo();
	 }
	 
public void demo() {
	 
 Modem modem;
 modem=new Modem();
 modem.setSpeed(80000);  //80Kbps speed
 modem.setTimeout(2000);
 modem.open("ithaki");
 
//----���������� ��� ��� ����������� ----
//---------��������� ��� ������----------
 long total_time = 0;
  	
 long start_time_package=0;
 int end_time_package=0;

 int number_of_package=0;
//----------------------------------------
 
//---------�� ������� ��� ��� �����--------     
String ECHO_code=new String("E5715\r");
String IMAGE_code=new String("M0627\r");
String IMAGE_ERROR_code=new String("G7174\r");
String GPS_code=new String("P9776");//<--� ������� ����� �� /r
String ACK_code=new String("Q0009\r");
String NACK_code=new String("R3843\r");
String path_code="R=1020099";  //���������� R=�PPPPLL
String str; //��� ��� ��������� �=������������ ��� ���������� � GpsFunction()
//-----------------------------------------	
 


//===========������� ������ ��� ��� ��������� ��� echo_package========
System.out.println("EchoPackage code starts");

BufferedWriter time_to_receive_packages=null;

//���������� ���� ������� ��� ��� ���������� ��� ����� ������ echo_package
try{   
	  time_to_receive_packages =new BufferedWriter(new FileWriter("time_of_packages.txt"));
	  time_to_receive_packages.write("Start to measure the time\r");   
      }catch(IOException e)
	    { e.printStackTrace(); }

//���� �������� ������ ��� ��� �� ������
total_time=System.nanoTime(); 

//������� ��������� ��� ������� ��� 200 ������
while(number_of_package<200){
 number_of_package++;	
 start_time_package=System.nanoTime(); //���� �������� ������ ��������� ���� �������
 
 (new Packages()).receivePackeges(modem,ECHO_code); //�������� ������� <-- <--
 
//������� ������ ��� ��������� ��� ��� nanoseconds �� milliseconds
end_time_package=(int) TimeUnit.MILLISECONDS.convert((System.nanoTime() - start_time_package), TimeUnit.NANOSECONDS);

//����� �� ��� ������ .txt �� ����� ��� ��� �������� ��� �������
try
  { 
	time_to_receive_packages.write(Integer.toString(end_time_package));
	time_to_receive_packages.write("\r");
  }catch(IOException e)
    { e.printStackTrace(); }
	

 }//����� while
//������� ������ ��� ��� �������� ���������� ��� ���������(200 ������)
total_time=TimeUnit.MILLISECONDS.convert((System.nanoTime() - total_time), TimeUnit.NANOSECONDS);


//K�������� �� ������
try
 { 
  time_to_receive_packages.write("Total time for the receipt of packets=");
  time_to_receive_packages.write(Integer.toString((int) total_time)+"\r");	
  time_to_receive_packages.write("The measurement finished\r");
  time_to_receive_packages.close();	
 }catch(IOException e)
  { e.printStackTrace(); }

System.out.println("EchoPackage code ends");
//============================Echo Package End==============================


//=======�������� ������� ��� ��� server=======
System.out.println("Image code starts");

(new ReadPicture()).readPicture(modem,IMAGE_code,"CameraImageClear.jpeg"); //Clear

(new ReadPicture()).readPicture(modem,IMAGE_ERROR_code,"CameraImageErrors.jpeg"); //With errors

System.out.println("Image code ends");
//=============================================


//=======================GPS=============================
//===�������� ��� �������������,��������� �������� ���===
//=======�������� 9 ������������� ��� ��� ��������=======
//==============��� ������� ��� ���������================
System.out.println("GPS code starts");

str=(new GpsFunction()).getCoordinates(modem,GPS_code+path_code+"\r"); //<-- ������ �������������

String PxxxT=GPS_code;

//������������� ��� ������ ��� ������ �=..�=..�=..
PxxxT+=str;

//���� ��� ������� ��� ���������
(new ReadPicture()).readPicture(modem,PxxxT,"ImageOfGpsTracking.jpeg");

	
System.out.println("GPS code ends");	 
//===================����� GPS code======================



//==============���������� ARQ �� ��������=============
System.out.println("AQR code stars");

int[] repeats=new int[100]; //������� ���� ����� ������������ ��� ������ ������� ��� ������ ��� 
                            //����������� �����������,�������� ������� ����� 100 �����������  
int repeat_count=0;
number_of_package=0;

BufferedWriter time_to_receive_AQR_packages=null;

//���������� ���� ������� ��� ��� ���������� ��� ����� ������ 
try{   
	time_to_receive_AQR_packages =new BufferedWriter(new FileWriter("time_AQR_packages.txt"));
	time_to_receive_AQR_packages.write("Start to measure the time\r");   
    }catch(IOException e)
	    { e.printStackTrace(); }

//���� �������� ������ ��� ���������� ��� ��� �������� ���� ��� �������(200)	
total_time=System.nanoTime(); 

//������� ��������� ��� ������� ��� 200 ������
while(number_of_package<200){ 

number_of_package++;
start_time_package=System.nanoTime(); //���� �������� ������ ��������� ���� �������

if(!(new ManageRandomErrors()).manageErrors(modem,ACK_code)) //�������� ������� <-- <--
{	
	repeat_count=1;
 while(!(new ManageRandomErrors()).manageErrors(modem,NACK_code))
   {//��������� ������� ����� �� ����� �����
	 
	 repeat_count++; //����������� ����������� 
   }
 
repeats[repeat_count]++; //���������� �����������
}else{
	  repeats[0]++;
     }

//������� ������ ��� ��������� ��� ��� nanoseconds �� milliseconds
end_time_package=(int) TimeUnit.MILLISECONDS.convert((System.nanoTime() - start_time_package), TimeUnit.NANOSECONDS);

//����� �� ��� ������ .txt �� ����� ��� ��� �������� ��� �������
try
{ 
	time_to_receive_AQR_packages.write(Integer.toString(end_time_package));
	time_to_receive_AQR_packages.write("\r");
}catch(IOException e)
  { e.printStackTrace(); }
	
	
}//����� while

//������� ������ ��� ��� �������� ���������� ��� ���������(200 ������)
total_time=TimeUnit.MILLISECONDS.convert((System.nanoTime() - total_time), TimeUnit.NANOSECONDS);


try
{ 
time_to_receive_AQR_packages.write("The measurement finished\r");
time_to_receive_AQR_packages.write("Start mesuere the number of package that was repeated\r");
}catch(IOException e)
{ e.printStackTrace(); }

int a=0;

//��� ��� ������ repeats �������� ��� ������ ��� ����������� ���
//��� ������� ��� ������ ��� ����������� ���������
for(int i=0;i<100;i++)
{
  if(repeats[i]!=0)
  { 
	 a+=repeats[i];
   try
    { 
	 time_to_receive_AQR_packages.write(Integer.toString(i)); 
	 time_to_receive_AQR_packages.write(" "); 		
	 time_to_receive_AQR_packages.write(Integer.toString(repeats[i]));
	 time_to_receive_AQR_packages.write("\r");
	}catch(IOException e)
	  { e.printStackTrace(); }
	  	 
  }
	
}

//K�������� �� ������
try
{ 
time_to_receive_AQR_packages.write("Total time for the receipt of packets=");
time_to_receive_AQR_packages.write(Integer.toString((int) total_time)+"\r");
time_to_receive_AQR_packages.write("The measurement of packages finished\r");
time_to_receive_AQR_packages.close();	
}catch(IOException e)
{ e.printStackTrace(); }

System.out.println("AQR code ends");
//================����� ������ AQR===============
	
  }
}

//===============================================����� ������ VirtualModem===============================================


//=====================================================����� Packages================================================

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


//==============================================����� ������ Packages=====================================================


//===============================================����� ReadPicture==========================================

package ergasia;
import ithakimodem.*;

//import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class ReadPicture {
	
	
public void readPicture(Modem modem,String pictureCode,String image_name){
	//��������� ��� ���������� ������ ���� server
	 modem.write( pictureCode.getBytes());
	     
int input_read=0;
int count=0;
byte[] beginning = { (byte)0xFF , (byte)0xD8 };
byte[] termination = { (byte)0xFF , (byte)0xD9 }; 
		     

FileOutputStream image = null;
		
//���� ��� �� ����� 2 byte ��� ���� �� ����� �����������
//������ ��� ������������ �� ����
  while(true)
 { 
	    	 
 //�� ��� ��� ��� ��������� 0xFF ������������ ���� ���������
 //���� ������� ���������� ���������,������ �� ������ �� ����������
 //��� ������ ����� ����� � ���� ��� ���������.� ������� �������
 //��� ��� ��������� ��� ������ 2 ���������� 0xFF ���� �����
 if((byte) input_read!=beginning[0]) input_read = modem.read();
	 
   if( (byte) input_read==beginning[0] )//��� ����� ���������
    {
		//������� ��� ��� ������� ���������
	 input_read = modem.read();
	 
	  if((byte) input_read==beginning[1] )//��� �������-������� ���������
	  {
		  System.out.println("The start of the recording of the image");
			 
			  
		  try{//����������� �� �������� �� ������ �� �������
			   image = new FileOutputStream(image_name,true);
			   image.write(beginning[0]);
               image.write(input_read);
			   break;
	    	   }catch (Exception x) {
	               System.out.println("Exception");
			   break;
               }
			  
	  }
		
    }
		  
 }
		 
//����������� �� �������� ����� �� ������ �� byte ������
while(true)
 {
  input_read = modem.read();
		
   try {//����������
        image.write(input_read);
	   }catch (Exception x) {
	     break;
	    }			
	
	//������� ����� ��� �� ������ �� byte ����������
	 if((byte) input_read==termination[0] ) count=1; //�� ����� �� �����
	
	if(count==1){//�� ������� ������ ��� �� ������� byte ���� ������� ��������� ��� while()
	    count=2;
	    }else if((count==2)&&((byte) input_read==termination[1] )){//�� ����� �� ������� ����������
		 break;
	    }
	     else count=0; //���� ����������
		}
	System.out.println("End of the recording");
	 //����� ����� ���������
		
	 }//����� ReadPicture 

		 
}


//===============================================����� ������ ReadPicture==========================================


//===============================================����� GpsFunction==========================================

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
//======================���������� ��� �������������=====================
	
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

//===============================================����� ������ GpsFunction==========================================


//===============================================����� ManageRandomErrors==========================================
package ergasia;
import ithakimodem.*;

import java.io.FileOutputStream;




public class ManageRandomErrors {
	
public boolean manageErrors(Modem modem,String ErrorCode){
	
	FileOutputStream text = null;
	String START_CODE="PSTART";
	String STOP_CODE="PSTOP";
	int input_read;
	int varXor=0;
	int count=0;
	int available=3;
	int xorCount=0;
	int valueFCS=0;
	int mul_factor=100;
	
	//��������� ��� ���������� ������ ���� server
	modem.write( ErrorCode.getBytes());
		
	try{
		text= new FileOutputStream("dedomenaErros.txt",true);
		
	}catch (Exception x) {
		System.out.println("It appears a Exception");
         }
	
	
	
		
	//�������� ��� ��� ���� ��� ���������,������ ��� ����������
	//��� ���������� "PSTART"
	while(true)
	{
		input_read=modem.read();
	//	System.out.println((char) input_read);
		if(START_CODE.charAt(count)==(char) input_read){
			if(count==5){
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
   	 text.write(START_CODE.charAt(i));
   	 
 	    }catch (Exception x) {
 	    	
            }
	}
	
	//���� �������� ���� ��� ������ �� �������� ��� ���������� ��� ��� server
	//����� �� ����������� �� string ������;"PSTOP"
	while(true)
	{
	input_read=modem.read();
	
	
	//System.out.println("char : "+(char) input_read +" ASCII: "+input_read);
	
	//����� �� �������� ��� ������
	try {
    	 text.write(input_read);
    	 
  	    }catch (Exception x) {
  	    	System.out.println("It appears a Exception");
             break;
             }
			 
	//������������ ��� ������ ��� ��� ����� XOR ������ 
	//��� ��������� ��� ���������� ����� �������
	if((char) input_read=='<' ) xorCount=1;
	if((char) input_read=='>' ) xorCount=3;
	
	switch(xorCount){
	case 1:
	 xorCount=2;
	 break;
	case 2:
     varXor=varXor^input_read; //XOR
     break;
	case 3:
	 available--;
	 break;
	 default:
	 //break;
	}
	
	//������������ ��� ���� FCS
	if(available==0||available==-1||available==-2||available==-3)
	{
	 xorCount=0;
	 valueFCS+=(input_read-48)*mul_factor;
	 mul_factor=mul_factor/10;
	 available--;
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
   	 text.write((char) 13);
   	  	    }catch (Exception x) {}
	
	if(varXor==valueFCS) return (true);
	
	return (false);
}
	
}

//===========================================����� ������ ManageRandomErrors==========================================