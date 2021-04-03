// MessageReceiver.java - PARTIAL IMPLEMENTATION
import java.util.*;

/**
 * This class implements the receiver side of the data link layer.
 * <P>
 * The source code supplied here only contains a partial implementation.
 * Your completed version must be submitted for assessment.
 * <P>
 * You only need to finish the implementation of the receiveMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes. 
 * Only this file will be processed when your work is marked.
 * @author Rojan Giri 
 * @version 5 
 */

public class MessageReceiver
{
    // Fields ----------------------------------------------------------

    private int mtu;                      // maximum transfer unit (frame length limit)
    private FrameReceiver physicalLayer;  // physical layer object
    private TerminalStream terminal;      // terminal stream manager
    private char firstchar;
    private int framecharChecker;
    private int CheckSumError;
    private int sum;
    private int charcounter;

    // DO NOT ADD ANY MORE INSTANCE VARIABLES
    // but it's okay to define constants here

    // Constructor -----------------------------------------------------

    /**
     * MessageReceiver constructor - DO NOT ALTER ANY PART OF THIS
     * Create and initialize new MessageReceiver.
     * @param mtu the maximum transfer unit (MTU)
     * (the length of a frame must not exceed the MTU)
     * @throws ProtocolException if error detected
     */

    public MessageReceiver(int mtu) throws ProtocolException
    {
        // Initialize fields
        // Create physical layer and terminal stream manager

        this.mtu = mtu;
        this.physicalLayer = new FrameReceiver();
        this.terminal = new TerminalStream("MessageReceiver");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
    }

    // Methods ---------------------------------------------------------

    /**
     * Receive a single message - THIS IS THE ONLY METHOD YOU NEED TO MODIFY
     * @return the message received, or null if the end of the input
     * stream has been reached.  See receiveFrame documentation for
     * further explanation of how the end of the input stream is
     * detected and handled.
     * @throws ProtocolException immediately without attempting to
     * receive any further frames if any error is detected, such as
     * a corrupt frame, even if the end of the input stream has also
     * been reached (signalling an error takes precedence over
     * signalling the end of the input stream)
     */

    public String receiveMessage() throws ProtocolException
    {
        String message = "";    // whole of message as a single string
        // initialise to empty string

        // Report action to terminal
        // Note the terminal messages aren't part of the protocol,
        // they're just included to help with testing and debugging

        terminal.printlnDiag("  receiveMessage starting");

        // YOUR CODE SHOULD START HERE ---------------------------------
        // No changes are needed to the statements above

        // The following block of statements shows how the frame receiver
        // is invoked.  At the moment it just sets the message equal to
        // the first frame.  This is of course incorrect!  receiveMessage
        // should invoke receiveFrame separately for each frame of the
        // message in turn until the final frame in that message has been
        // obtained.  The message segments should be extracted and joined
        // together to recreate the original message string.  One whole
        // message should is processed by a single execution of receiveMessage
        // and returned as a single string.
        // 
        // See the coursework specification and other class documentation
        // for further info.
        // YOUR CODE SHOULD FINISH HERE --------------------------------

        boolean finish= false;
        ArrayList<String> a = new ArrayList<>();
        String frame = physicalLayer.receiveFrame();
        message = frame;
      
        if(message ==  null){
            terminal.printlnDiag("receiveMessage returning null (end of input stream)");
            message = null;
        }

        else{
            char lastSymbol = message.charAt(message.length() -1);
            char firstSymbol =  message.charAt(0);
            if(lastSymbol != '>'){
                throw new ProtocolException("For input string: "+lastSymbol+"");
            }
            else if(firstSymbol != '<'){
                throw new ProtocolException("For input string: "+firstSymbol+"");
            }
            String getmessage= chopFrame(message);
            message = getmessage;

            while(!finish){
                if(firstchar == 'E' && framecharChecker == charcounter && CheckSumError == sum){
                    a.add(message);
                    String joinArray = String.join("", a);
                    message = joinArray;
                    finish = true; 

                }
                else if (firstchar == 'D' && framecharChecker == charcounter && CheckSumError == sum){
                    a.add(message);
                    getmessage = physicalLayer.receiveFrame();
                    message = getmessage;
                    String getchopmessage= chopFrame(message);
                    message = getchopmessage;

                }
                else if(framecharChecker != charcounter){     
                    throw new ProtocolException("The calculated length of the message differs from that recorded");            
                }
                else if(CheckSumError != sum){     
                    throw new ProtocolException("Checksum calculated differs from that recorded");            
                }
            }
            terminal.printlnDiag("  receiveMessage returning \"" + message + "\"");
        }
        return message;
    }

    // end of method receiveMessage
    // You may add private methods if you wish

    /**
     * Method chopFrame
     * This mehod chop the frame in each steps and al callz other method to 
     * count or to gte index of the frame. it kepps chopping the 
     * frame until only the message is left. 
     * @param String s (message) 
     * @return String s (message)
     */
    private String chopFrame(String s){
        framecharChecker= getCouterindex(s);
        // above gets the index of the count of char
        s = s.substring(1);
        s = s.substring(0, s.length()-1);
        //above removes the first (<) and last(>) char
        firstchar = s.charAt(0);
        // above get the first characeter which can be either D or E
        CheckSumError= getcheckSum(s);
        // above get the last two digit from the frame which are the check sum of the frame 
        s = s.substring(0, s.length()-2);
        // above removes the checksum value from the frame.
        sum= checkSum(s);
        //above calculated the check sum of the remianing frame
        s = s.substring(5);
        s = s.substring(0, s.length()-1); 
        // above remove the remining front charcaters and back char to get on the message
        charcounter= count(s);
        //above counts the meesage
        return s;
        //return the message after remove all the frames format. 

    }

    /**
     * Method count
     * this method count the chrecter in a message and return it back
     * @param String of s
     * @return count
     */
    private int count(String s){
        int count = 0 ;
        for(int i = 0 ; i <s.length();i++){
            count++;
        }
        return count; 
    }

    /**
     * Method getCouterindex
     * this method get the index if the counter from the frame for example <E-02-Hi-79>
     * counter will be 02
     * @param String of S
     * @return i
     */
    private int  getCouterindex(String S){
        String string = S.substring(3, 5);
        int i = Integer.parseInt(string); 
        return i; 
    }

    /**
     * Method getcheckSum
     *
     *this method get the index if the  checksum from the frame for example <E-02-Hi-79>
     * counter will be 79 and converts the string to an int. 
     * @param String of S
     * @return i
     */
    private int  getcheckSum(String S){
        String string = S.substring(S.length()-2);
        int i = Integer.parseInt(string);
        return i; 
    }

    /**
     * This method takes a string and coverts them into ASCII values, replcaes the square brackets and then 
     * split the values and add them in to an array of sumofchar. 
     * It will iterate though the array and change the string value into integers values.
     * it will sum all the values in the array ad provide only the last two digit number. 
     * @param String charvalues which are the "E-02-Hi-"
     * @returns the sum in the form of string fr the last two digit format.
     */
    private int checkSum(String charvalue){
        int sum=0;
        String Stringsum="";
        try {
            byte[] ascii = charvalue.getBytes("US-ASCII");
            charvalue= Arrays.toString(ascii); 
            charvalue= charvalue.replaceAll("^.|.$", "");
            String[] sumofchar = charvalue.replaceAll("[$,]", "").split(" ");
            for (String number : sumofchar) {
                sum += Integer.parseInt(number);
            }
            Stringsum = String.format("%02d", (sum)%100);
        }
        catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        } 
        int i = Integer.parseInt(Stringsum);
        return i;
    }

}
// end of class MessageReceiver
