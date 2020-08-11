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
