import java.util.*;
// MessageSender.java - PARTIAL IMPLEMENTATION

/**
 * Author(Rojan Giri "rg427")
 * version(4)
 * This class implements the sender side of the data link layer.
 * <P>
 * The source code supplied here only contains a partial implementation. 
 * Your completed version must be submitted for assessment.
 * <P>
 * You only need to finish the implementation of the sendMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes. 
 * Only this file will be processed when your work is marked.
 */

public class MessageSender
{
    // Fields ----------------------------------------------------------

    private int mtu;                    // maximum transfer unit (frame length limit)
    private FrameSender physicalLayer;  // physical layer object
    private TerminalStream terminal;    // terminal stream manager

    // DO NOT ADD ANY MORE INSTANCE VARIABLES
    // but it's okay to define constants here

    // Constructor -----------------------------------------------------

    /**
     * MessageSender constructor - DO NOT ALTER ANY PART OF THIS
     * Create and initialize new MessageSender.
     * @param mtu the maximum transfer unit (MTU)
     * (the length of a frame must not exceed the MTU)
     * @throws ProtocolException if error detected
     */

    public MessageSender(int mtu) throws ProtocolException
    {
        // Initialize fields
        // Create physical layer and terminal stream manager

        this.mtu = mtu;
        this.physicalLayer = new FrameSender();
        this.terminal = new TerminalStream("MessageSender");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
    }

    // Methods ---------------------------------------------------------

    /**
     * Send a single message - THIS IS THE ONLY METHOD YOU NEED TO MODIFY
     * @param message the message to be sent.  The message can be any
     * length and may be empty but the string reference should not
     * be null.
     * @throws ProtocolException immediately without attempting to
     * send any further frames if, and only if, the physical layer
     * throws an exception or the given message can't be sent
     * without breaking the rules of the protocol (including the MTU)
     */

    public void sendMessage(String message) throws ProtocolException
    {
        // Report action to terminal
        // Note the terminal messages aren't part of the protocol,
        // they're just included to help with testing and debugging

        terminal.printlnDiag("  sendMessage starting (message = \"" + message + "\")");

        // YOUR CODE SHOULD START HERE ---------------------------------
        // No changes are needed to the statements above
        ArrayList<String> a = new ArrayList<>();
        int subStringIndex = 0;
        int check = 1;
        int mturange=mtu-10;
        int subStringSize;
        int countSentence= count(message);
        if(countSentence % mturange == 0){
            subStringSize = (Integer)((message.length()/mturange));
        }
        else{
            subStringSize = (Integer)((message.length()/mturange + 1));
        }
        
        for(int j = 0; j < subStringSize; j++)
        {
            if(message.length() > mturange){
                a.add(message.substring(0, mturange));
                subStringIndex++;
                message = message.substring(mturange);

            } else 
                a.add(message);
        }

        for(int i = 0; i < a.size(); i++){
            if(a.size() == check){
                int c= count(a.get(i));
                String countFormat = String.format("%02d", c);
                String checksum1 =checkSum("E"+"-"+countFormat+"-"+a.get(i)+"-");
                message= "<"+"E"+"-"+countFormat+"-"+a.get(i)+"-"+checksum1+">";
                physicalLayer.sendFrame(message);
            } else if(a.size() != check && a.size() > 1){
                int c= count(a.get(i));
                String checksum2 =checkSum("D"+"-"+c+"-"+a.get(i)+"-");
                message= "<"+"D"+"-"+c+"-"+a.get(i)+"-"+checksum2+">";
                physicalLayer.sendFrame(message);
            } else {
                int c= count(a.get(i));
                String countFormat = String.format("%02d", c);
                String checksum3 = checkSum("E"+"-"+countFormat+"-"+a.get(i)+"-");
                message= "<"+"E"+"-"+countFormat+"-"+a.get(i)+"-"+checksum3+">" ;
                physicalLayer.sendFrame(message);
            }
            check++;
        }
        // The following statement shows how the frame sender is invoked.
        // At the moment it just passes a fixed string.
        // sendMessage should split large messages into several smaller
        // segments.  Each segment must be encoded as a frame in the
        // format specified.  sendFrame will need to be called separately
        // for each frame in turn.  See the coursework specification
        // and other class documentation for further info.

        // YOUR CODE SHOULD FINISH HERE --------------------------------
        // No changes are needed to the statements below

        // Report completion of task

        terminal.printlnDiag("  sendMessage finished");

    } // end of method sendMessage

     /**
     * This method will take a string,which will the the sub string of the message and then 
     * count the charecter of that sub string including thw white space. incrments the count each time, 
     * while iterating though the substring
     * @param substring s of message 
     * @returns count 
     */

    
    private int count(String s)
    {
        int count = 0;
        for(int i = 0; i < s.length(); i++) {    
            count++;         
        }
        return count;
    }

     /**
     * This method takes a string and coverts them into ASCII values, replcaes the square brackets and then 
     * split the values and add them in to an array of sumofchar. 
     * It will iterate though the array and change the string value into integers values.
     * it will sum all the values in the array ad provide only the last two digit number. 
     * @param String charvalues which are the "E-02-Hi-"
     * @returns the sum in the form of string fr the last two digit format.
     */
    private String checkSum(String charvalue){
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
        return Stringsum;
    }
    // You may add private methods if you wish

} // end of class MessageSender
