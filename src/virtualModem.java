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
 modem.setSpeed(80000);    //80000 the best
 modem.setTimeout(2000);
 modem.open("ithaki");
 
 long total_time = 0;
  	
 long start_time_package=0;
 int end_time_package=0;

 int number_of_package=0;
 
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