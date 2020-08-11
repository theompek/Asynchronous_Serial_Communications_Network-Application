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
 
//---------Οι κωδικοί απο την Ιθάκη--------     
String ECHO_code=new String("E5715\r");
String IMAGE_code=new String("M0627\r");
String IMAGE_ERROR_code=new String("G7174\r");
String GPS_code=new String("P9776");//<--Ο κωδικός χωρίς το /r
String ACK_code=new String("Q0009\r");
String NACK_code=new String("R3843\r");
String path_code="R=1020099";  //Παράμετρος R=ΧPPPPLL
String str; //Για την παράμετρο Τ=ΑΑΒΒΓΓΔΔΕΕΖΖ που επιστρέφει η GpsFunction()
//-----------------------------------------	
 


//===========Κομμάτι κώδικα για την περίπτωση των echo_package========
System.out.println("EchoPackage code starts");

BufferedWriter time_to_receive_packages=null;

//Δημιουργία ενός αρχείου για την αποθήκευση των τιμών χρόνου echo_package
try{   
	  time_to_receive_packages =new BufferedWriter(new FileWriter("time_of_packages.txt"));
	  time_to_receive_packages.write("Start to measure the time\r");   
      }catch(IOException e)
	    { e.printStackTrace(); }

//Αρχή μέτρησης χρόνου για ολα τα πακέτα
total_time=System.nanoTime(); 

//Κάνουμε καταγραφή των πακέτων για 200 πακέτα
while(number_of_package<200){
 number_of_package++;	
 start_time_package=System.nanoTime(); //Αρχή μέτρησης χρόνου παραλάβης κάθε πακέτου
 
 (new Packages()).receivePackeges(modem,ECHO_code); //Παραλαβή πακέτων <-- <--
 
//Μέτρηση χρόνου και μετατροπή του απο nanoseconds σε milliseconds
end_time_package=(int) TimeUnit.MILLISECONDS.convert((System.nanoTime() - start_time_package), TimeUnit.NANOSECONDS);

//Γράψε σε ένα αρχείο .txt το χρόνο για την αποστολή του πακέτου
try
  { 
	time_to_receive_packages.write(Integer.toString(end_time_package));
	time_to_receive_packages.write("\r");
  }catch(IOException e)
    { e.printStackTrace(); }
	

 }//τελος while
//Μέτρηση χρόνου για την διάρκεια καταγραφής των δεδομένων(200 πακέτα)
total_time=TimeUnit.MILLISECONDS.convert((System.nanoTime() - total_time), TimeUnit.NANOSECONDS);


//Kλείνουμε το αρχείο
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


//=======Παραλαβή εικόνων απο τον server=======
System.out.println("Image code starts");

(new ReadPicture()).readPicture(modem,IMAGE_code,"CameraImageClear.jpeg"); //Clear

(new ReadPicture()).readPicture(modem,IMAGE_ERROR_code,"CameraImageErrors.jpeg"); //With errors

System.out.println("Image code ends");
//=============================================


//=======================GPS=============================
//===Παραλαβή των συντεταγμένων,κατάλληλη ανάγνωση και===
//=======αποστολή 9 συντεταγμένων για την παραλαβή=======
//==============της εικόνας των στιγμάτων================
System.out.println("GPS code starts");

str=(new GpsFunction()).getCoordinates(modem,GPS_code+path_code+"\r"); //<-- Πακέτα συντεταγμένων

String PxxxT=GPS_code;

//Συμπληρώνουμε τον κωδικό της μορφης Τ=..Τ=..Τ=..
PxxxT+=str;

//Λήψη της εικόνας των στιγμάτων
(new ReadPicture()).readPicture(modem,PxxxT,"ImageOfGpsTracking.jpeg");

	
System.out.println("GPS code ends");	 
//===================Τέλος GPS code======================



//==============Μηχανισμός ARQ σε σφάλματα=============
System.out.println("AQR code stars");

int[] repeats=new int[100]; //Πίνακας στον οποίο αποθηκεύουμε τον αριθμό πακέτων που εκάναν τις 
                            //αντίστοιχες επαναλήψεις,θεωρούμε μέγιστο μέχρι 100 επαναλήξεις  
int repeat_count=0;
number_of_package=0;

BufferedWriter time_to_receive_AQR_packages=null;

//Δημιουργία ενός αρχείου για την αποθήκευση των τιμών χρόνου 
try{   
	time_to_receive_AQR_packages =new BufferedWriter(new FileWriter("time_AQR_packages.txt"));
	time_to_receive_AQR_packages.write("Start to measure the time\r");   
    }catch(IOException e)
	    { e.printStackTrace(); }

//Αρχή μέτρησης χρόνου που απαιτείται για την παραλαβή όλων των πακέτων(200)	
total_time=System.nanoTime(); 

//Κάνουμε καταγραφή των πακέτων για 200 πακέτα
while(number_of_package<200){ 

number_of_package++;
start_time_package=System.nanoTime(); //Αρχή μέτρησης χρόνου παραλάβης κάθε πακέτου

if(!(new ManageRandomErrors()).manageErrors(modem,ACK_code)) //Παραλαβή πακέτων <-- <--
{	
	repeat_count=1;
 while(!(new ManageRandomErrors()).manageErrors(modem,NACK_code))
   {//Επανάληψη πακέτου μέρχι να έρθει σωστά
	 
	 repeat_count++; //Καταμέτρηση επαναλήψεων 
   }
 
repeats[repeat_count]++; //Αποθήκευση επαναλήξεων
}else{
	  repeats[0]++;
     }

//Μέτρηση χρόνου και μετατροπή του απο nanoseconds σε milliseconds
end_time_package=(int) TimeUnit.MILLISECONDS.convert((System.nanoTime() - start_time_package), TimeUnit.NANOSECONDS);

//Γράψε σε ένα αρχείο .txt το χρόνο για την αποστολή του πακέτου
try
{ 
	time_to_receive_AQR_packages.write(Integer.toString(end_time_package));
	time_to_receive_AQR_packages.write("\r");
}catch(IOException e)
  { e.printStackTrace(); }
	
	
}//τελος while

//Μέτρηση χρόνου για την διάρκεια καταγραφής των δεδομένων(200 πακέτα)
total_time=TimeUnit.MILLISECONDS.convert((System.nanoTime() - total_time), TimeUnit.NANOSECONDS);


try
{ 
time_to_receive_AQR_packages.write("The measurement finished\r");
time_to_receive_AQR_packages.write("Start mesuere the number of package that was repeated\r");
}catch(IOException e)
{ e.printStackTrace(); }

int a=0;

//Απο τον πίνακα repeats πέρνουμε των αριθμό των επαναλήψεων και
//των πακέτων που έκαναν τις αντίστοιχες επαλήψεις
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

//Kλείνουμε το αρχείο
try
{ 
time_to_receive_AQR_packages.write("Total time for the receipt of packets=");
time_to_receive_AQR_packages.write(Integer.toString((int) total_time)+"\r");
time_to_receive_AQR_packages.write("The measurement of packages finished\r");
time_to_receive_AQR_packages.close();	
}catch(IOException e)
{ e.printStackTrace(); }

System.out.println("AQR code ends");
//================Τέλος κώδικα AQR===============
	


	
	}
}