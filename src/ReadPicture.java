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

