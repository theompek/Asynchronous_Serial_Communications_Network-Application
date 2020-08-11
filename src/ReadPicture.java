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

