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
//======================απομόνωνσης των συντεταγμένων=====================
	
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
