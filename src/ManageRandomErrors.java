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
