/*
                             Δικτυα Υπολογιστών Ι 7ο εξάμηνο
				    	       Θεοφάνης Μπεκιάρης ΑΕΜ:8200 
					
Ο κώδικας της εργασίας έχει γραφεί και έχει εκτελεστεί με την βοήθεια του προγράμματος Eclipce.
Το πρόγραμμα αποτελείται απο 5 κλάσεις και η κάθε κλάση περιέχει μία συνάρτηση η οποία εκτελεί κώδικα 
για κάθε περίπτωση που ζητείται στην εργασία.Στο παρόν αρχείο έχουν συμπεριληφθεί όλες οι κλάσεις μαζί
για τις ανάγκες της παρουσίασης τους.

Κλάση VirtualModem:Η συνάρτηση virtualModem περιέχει την συνάρτηση main μέσα στην οποία καλούνται οι 
υπόλοιπες συναρτήσεις για να εκτελέσουν τα ζητούμενα της εργασίας.Επιπλέον μέσα στην main γίνονται και 
οι κατάλληλες μετρήσεις των χρόνων εκτέλεσης των προγραμμάτων.

Κλάση Packages:Περιέχει την συνάρτηση receivePackeges η οποία στέλνει τον κωδικό echoPacke και διαβάζει
τα πακέτα και τα αποθηκεύει σε ένα αρχείο με όνομα dedomenapackage.txt.

Κλάση ReadPicture:Περιέχει τον κώδικα με τον οποίο διαβάζουμε τα δεδομένα που αποστέλονται απο την 
Ιθάκη για να δημιουργήσουμε μία εικόνα.

Κλάση GpsFunction:Η συνάρτηση getCoordinates που περιέχεται μέσα στην κλάση λαμβάνει τα δεδομένα που 
αντιστοιχούν στο κομμάτι του GPS tracking και τα αποθηκεύει σε ενα αρχείο,επιπλέον υπολογίζει κατάλληλα 
τα σημεία και επιστρέφει τις συντεταγμένες ετσι όπως ζητούνται στην μορφή  String Τ=..Τ=..Τ=... για 9 σημεία.

Κλάση ManageRandomErrors:Η συνάτρηση manageErrors αποθηκεύει τα δεδομένα για το κομμάτι του μηχανισμού ARQ 
σε ένα αρχείο και ταυτόχρονα ελέγχει το 16ψήφιο πεδίο(μέσω της πράξης XOR) και το συγκρίνει με το πεδίο FCS 
και αν δεν είναι ίσα επιστρέφει την λογική τιμή FALSE,διαφορετικά επιστρέφει την τιμή TRUE.  
*/

//===============================================Κλάση VirtualModem==========================================

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
 
//----Μεταβλητές για τις απαραίτητες ----
//---------μετρήσεις των χρόνων----------
 long total_time = 0;
  	
 long start_time_package=0;
 int end_time_package=0;

 int number_of_package=0;
//----------------------------------------
 
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

//===============================================Τελος κλάσης VirtualModem===============================================


//=====================================================Κλάση Packages================================================

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
  
//Στέλνουμε τον αντίστοιχο κωδικό στον server
modem.write( packageCode.getBytes());
			
 try{
	   packageText= new FileOutputStream("dedomenapackage.txt",true);
	  }catch (Exception x) {
		System.out.println("It appears a Exception");
       }
	
//Ψάχνουμε για την αρχη των δεδομένων,δηλαδή την ακολουθεία
//των χαρακτήρων "PSTART"
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
	
//Για λόγους πληρότητα γραφουμε μέσα στο αρχείο
//και την ακολουθεία των χαρακτήρων της αρχής
for(int i=0;i<5;i++){
try {
 packageText.write(START_CODE.charAt(i));
 	 
    }catch (Exception x) {}
}
	
//Τώρα γράφουμε μέσα στο αρχείο τα δεδομένα που λαμβάνουμε απο τον server
//μέχρι να εντοπίσουμε το string τέλους:"PSTOP"
while(true)
{
input_read=modem.read();
 
 //Γράψε τα δεδομένα στο αρχείο
try {
	 packageText.write(input_read);
    }catch (Exception x) {
	   	System.out.println("It appears a Exception");
         break;
       }
	
//Αν βρείς την ακολουθεία τέλους σταμάτα
if(STOP_CODE.charAt(count)==(char) input_read){
	if(count==4) break;
	count++; 	
    }
     else{
          count=0;
         }
		
}//τελος while
	
//Στο τέλος των δεδομένων ξεκίνα νέα γραμμή
try {
	 packageText.write((char) 13);
  	}catch (Exception x) {}

}//Τέλος receivePackeges

}


//==============================================Τέλος κλάσης Packages=====================================================


//===============================================Κλάση ReadPicture==========================================

package ergasia;
import ithakimodem.*;

//import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class ReadPicture {
	
	
public void readPicture(Modem modem,String pictureCode,String image_name){
	//Στέλνουμε τον αντίστοιχο κωδικό στον server
	 modem.write( pictureCode.getBytes());
	     
int input_read=0;
int count=0;
byte[] beginning = { (byte)0xFF , (byte)0xD8 };
byte[] termination = { (byte)0xFF , (byte)0xD9 }; 
		     

FileOutputStream image = null;
		
//Ψαξε για τα πρώτα 2 byte και οταν τα βρείς δημιουργησε
//αρχείο και αποθηκευσέτα σε αυτο
  while(true)
 { 
	    	 
 //Αν δεν έχω τον χαρακτήρα 0xFF αποθηκευμένο στην μεταβλητή
 //τότε διάβασε καινούργιο χαρακτήρα,αλλίως θα πρέπει να συνεχίσεις
 //για έλεγχο μήπως είναι η αρχή των δεδομένων.Ο έλεγχος γίνεται
 //για την περίπτωση που εχουμε 2 χαρακτήρες 0xFF στην σειρά
 if((byte) input_read!=beginning[0]) input_read = modem.read();
	 
   if( (byte) input_read==beginning[0] )//Τον πρώτο χαρακτήρα
    {
		//Διαβασε και τον επόμενο χαρακτήρα
	 input_read = modem.read();
	 
	  if((byte) input_read==beginning[1] )//Τον επόμενο-δέυτερο χαρακτήρα
	  {
		  System.out.println("The start of the recording of the image");
			 
			  
		  try{//Αποθήκευσαι τα δεδομένα σε αρχείο με εικόνας
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
		 
//Αποθήκευσαι τα δεδομένα μέχρι να βρούμε τα byte τέλους
while(true)
 {
  input_read = modem.read();
		
   try {//Αποθήκευση
        image.write(input_read);
	   }catch (Exception x) {
	     break;
	    }			
	
	//Έλεγχος μέχρι για να βρούμε τα byte συνεχόμενα
	 if((byte) input_read==termination[0] ) count=1; //Αν βρείς το πρώτο
	
	if(count==1){//Θα κάνουμε έλεγχο για το δεύτερο byte στην επόμενη επανάληψη της while()
	    count=2;
	    }else if((count==2)&&((byte) input_read==termination[1] )){//Αν βρείς το δεύτερο συνεχόμενα
		 break;
	    }
	     else count=0; //Ξανά προσπάθησε
		}
	System.out.println("End of the recording");
	 //Τέλος λήψης δεδομένων
		
	 }//Τέλος ReadPicture 

		 
}


//===============================================Τέλος κλάσης ReadPicture==========================================


//===============================================Κλάση GpsFunction==========================================

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
	
	//-------Μεταβλητές για το κομμάτι της αναζήτης των GPGGA πακέτων----------
	String GPGGA="$GPGGA";
	boolean read_time=false;       //Μεταβλητή για να ξεκινήσει η ανάγνωση της ώρας
	boolean access_latitude=false; //Για να διαβάσουμε το γεωγραφικο πλατος
	boolean access_longitude=false;//Για να διαβάσουμε το γεωγραφικο μήκος
	int mult_value=1000;           
	int time_value=0;              //Η ώρα του τρέχοντως πακέτου
	int previous_package_time=0;   //Η ώρα του πρηγούμενου πακέτου
	int space_count=0;             
	int gpg_count=0;
	int latitude_count=0;
	int longitude_count=0;
	int repeat_string_count=0; //Μεταβλητή που ορίζει των αριθμό των σημείων (ειναι ορισμένο για 9)
    int high_value=0;          //Τα πρώτα 4 ψηφία των συντεταγμένων
    int low_value=0;           //Τα υπολοιπα 4 ψηφία για τα οποία κάνουμε και πολλαπλασιασμο με το 0.006
	String str1="0";
	String str2="0";
	String str3="T=";
	String str4="0";
	String string_with_code="0";
	String previous_string="0";
	//-------------------------------------------------------------------------
	
	modem.write( GpsCode.getBytes());//Στέλνουμε τον αντίστοιχο κωδικό στον server
	
	//Δημιούργησε ένα αρχείο για τα δεδεμένα
	try{
		text= new FileOutputStream("GPStrackingdedomena.txt",true);
		
	}catch (Exception x) {
		System.out.println("It appears a Exception");
         }
	
	//Ψάχνουμε για την αρχη των δεδομένων,δηλαδή την ακολουθεία
	//των χαρακτήρων "START ITHAKI GPS TRACKING"
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
	//Για λόγους απλά πληρότητα γραφουμε μέσα στο αρχείο
	//και την ακολουθεία των χαρακτήρων της αρχής
	for(int i=0;i<23;i++){
	try {
   	 text.write(START_CODE.charAt(i));
   	 
 	    }catch (Exception x) {
 	    	
            }
	}
	
//Τώρα γράφουμε μέσα στο αρχείο τα δεδομένα που λαμβάνουμε απο τον server
//μέχρι να εντοπίσουμε το string τέλους:"STOP ITHAKI GPS TRACKING"
//Ταυτόχρονα με την εγραφή έχει δημιουργηθεί και αλγόριθμος ο οποίος
//διαβάζει τα δεδομένα που του έρχονται και εντοπίζει την ακολουθεία των 
//χαρακτήρων $GPGGA και μετά διαβάζει σε αυτά την ώρα και τις συντεταγμένες
//και αν ο χρόνος μεταξύ διαδοχικων πακέτων είναι μεγαλύτερος των 4sec και οι 
//συντεταγμένες τους είναι διαφορετικές τότε περνάει τις συντεταγμένες απο αυτα
//στην μεταβλητή string_with_code για να δημιουργήσει τελικά ενα string της 
//μορφής T=.....T=....T=.... το οποίο το επιστρέφει η συνάρτηση μας getCoordinates()
  while(true)
  {
	input_read=modem.read();
	
	//Γράψε τα δεδομένα στο αρχείο
	try {
    	 text.write(input_read);
    	 
  	    }catch (Exception x) {
  	    	System.out.println("It appears a Exception");
             break;
             }
	
//=========Κώδικας για την αναζήτη πακέτων GPGGA και κατάλληλης ==========
//======================απομόνωσης των συντεταγμένων=====================
	
//Βρες την ακολουθεία χαρακτήρων "GPGGA"
if((GPGGA.charAt(gpg_count)==input_read)&&(repeat_string_count<9))
{
	
  gpg_count++;
  if(gpg_count==6){      //Οταν βρόυμε την ακολουθεία "GPGGA"
	  read_time=true;    //Δινουμέ προσβαση στην παρακάτω if για να γίνει υπολογισμός της ώρας
	  gpg_count=0;       //Αρχικοποιούμε τον μετρητή για να ψάξει επόμενα πακέτα

  }	
    }else{
	gpg_count=0;
    }

//Διάβασε την ώρα
if(read_time&&(repeat_string_count<9))
{
	space_count++;
	
    //Θα πάρουμε τα 3 τελευταία ψηφία της ώρας,ειναι αρκετά αφου 
	//δεν πρόκειται να έχουν τα πακέτα μεγαλύτερη χρονική διαφορά απο 10λεπτά
	//Το πρώτο ψηφίο δείχωει τα λεπτά και τα αλλα 2 τα δεδυτερόλεπτα
	//αρα η μεγιστη τιμή της μεταβλητής ειναι 959(=9:59=10λεπτά)
	if((space_count==6)||(space_count==7)||(space_count==8))
	 { 
	  mult_value=mult_value/10;
	  time_value+=(input_read-48)*mult_value; //Μετατρέπουμε απο κώδικα ASCII σε 3ψηφιο αριθμό 	  
	 
	  
	   if(space_count==8)//Όταν τελικά πάρουμε την ώρα του πακέτου
	   {
		  read_time=false; 
		  space_count=0;
		  
		  if((time_value-previous_package_time)>=4){//Τα ιχνη πρέπει να απέχουν 4sec
			  access_latitude=true; 
			  mult_value=1000;//Για τον υπολογισμό των συντεταγμένων
			  previous_package_time=time_value;
			  time_value=0;
			 
		  }else if((time_value-previous_package_time)<=-4)//Αυτο είναι για την περίπτωση αλλαγής ώρας
		      {                                          //απο την μεγιστη τιμή της που ειναι 959 στο μηδεν
			   int x=960;
			   x+=time_value;
			   if((x-previous_package_time)>=4){
				   access_latitude=true;
				   mult_value=1000;
				   previous_package_time=time_value;
				   time_value=0;
			       }
		      }else{
		          mult_value=1000;//Οταν θα ξανα γίνει μετρηση χρόνου
		          time_value=0;
		      }
	   }
		  
     }		
}
//Γεωγραφικο πλατος			
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

//Γεωγραφικό μηκος
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
	   if(repeat_string_count==0)//Αν ειναι η πρώτη φορά δημιουργησε την μεταβλητη
	      { 
		    string_with_code=str3;
	        previous_string=str3;
	        repeat_string_count++;
	      }
	   else if(!previous_string.equals(str3))//Αν δεν εχουμε πάρει πάλι τον ιδιο κωδικό
	  	  { 		                         //συμπλήρωσε τον κωδικό στην πλήρη μεταβλητή
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
	//Αν βρείς την ακολουθεία τέλους σταμάτα
	if(STOP_CODE.charAt(count)==(char) input_read){
		if(count==23) break;
		count++; 	
	    }
	     else{
	          count=0;
	         }
	
	}//τελος while
    
	//Στο τέλος των δεδομένων ξεκίνα νέα γραμμή
	try {
   	 text.write((char) 13);
   	  	    }catch (Exception x) {}

return (string_with_code+"\r");
}
	
}

//===============================================Τέλος κλάσης GpsFunction==========================================


//===============================================Κλάση ManageRandomErrors==========================================
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
	
	//Στέλνουμε τον αντίστοιχο κωδικό στον server
	modem.write( ErrorCode.getBytes());
		
	try{
		text= new FileOutputStream("dedomenaErros.txt",true);
		
	}catch (Exception x) {
		System.out.println("It appears a Exception");
         }
	
	
	
		
	//Ψάχνουμε για την αρχη των δεδομένων,δηλαδή την ακολουθεία
	//των χαρακτήρων "PSTART"
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
	//Για λόγους πληρότητα γραφουμε μέσα στο αρχείο
	//και την ακολουθεία των χαρακτήρων της αρχής
	for(int i=0;i<5;i++){
	try {
   	 text.write(START_CODE.charAt(i));
   	 
 	    }catch (Exception x) {
 	    	
            }
	}
	
	//Τώρα γράφουμε μέσα στο αρχείο τα δεδομένα που λαμβάνουμε απο τον server
	//μέχρι να εντοπίσουμε το string τέλους;"PSTOP"
	while(true)
	{
	input_read=modem.read();
	
	
	//System.out.println("char : "+(char) input_read +" ASCII: "+input_read);
	
	//Γράψε τα δεδομένα στο αρχείο
	try {
    	 text.write(input_read);
    	 
  	    }catch (Exception x) {
  	    	System.out.println("It appears a Exception");
             break;
             }
			 
	//Υπολογίζουμε των αριθμό απο την πράξη XOR μεταξύ 
	//των στοιχείων που βρίσκονται εντός αγκυλών
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
	
	//Υπολογίζουμε την τιμή FCS
	if(available==0||available==-1||available==-2||available==-3)
	{
	 xorCount=0;
	 valueFCS+=(input_read-48)*mul_factor;
	 mul_factor=mul_factor/10;
	 available--;
	}
	
	//Αν βρείς την ακολουθεία τέλους σταμάτα
	if(STOP_CODE.charAt(count)==(char) input_read){
		if(count==4) break;
		count++; 	
	    }
	     else{
	          count=0;
	         }
	
	}//τελος while
	
	//Στο τέλος των δεδομένων ξεκίνα νέα γραμμή
	try {
   	 text.write((char) 13);
   	  	    }catch (Exception x) {}
	
	if(varXor==valueFCS) return (true);
	
	return (false);
}
	
}

//===========================================Τέλος κλάσης ManageRandomErrors==========================================