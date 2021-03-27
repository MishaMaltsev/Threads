/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

//import jdk.nashorn.internal.ir.TemplateLiteral;

/**
 * Class: Performer 
 * Description: Threaded Performer for server tasks.
 */
class Performer {

    private StringList state;
    private Socket conn;

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }

    public JSONObject add(String str) {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "add");
        state.add(str);
        json.put("data", state.toString());
        return json;
    }

    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }

    public void doPerform() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                JSONObject returnMessage = new JSONObject();
   
                int choice = message.getInt("selected");
                    switch (choice) {
                        case (0):
                            //quitting
                            returnMessage = quit();
                            quit = true;
                            break;
                        case (1):
                            String addStr = (String) message.get("data");
                            returnMessage = add(addStr);
                            break;
                        //adding cases for other methods 2 - 5
                        case (2):
                            //pop return message
                            returnMessage = pop();
                            break;
                        case (3):
                            //display
                            returnMessage = display();
                            break;
                        case (4): 
                            //count
                            returnMessage = count();
                            break;
                        case (5):
                            //switch
                            String switchStr = (String) message.get("data");
                            //parse the data
                            String[] splitSwitchStr = switchStr.split(" "); //Assuming split by " ", may be by comma
                            int firstElement = Integer.parseInt(splitSwitchStr[0]), secondElement = Integer.parseInt(splitSwitchStr[1]);
                            returnMessage = switchElements(firstElement, secondElement);
                            break;
                        default:
                            returnMessage = error("Invalid selection: " + choice 
                                    + " is not an option");
                            break;
                    }
                // we are converting the JSON object we have to a byte[]
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.send(out, output);
            }
            // close the resource
            System.out.println("close the resources of client ");
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * pop method - removes the top element of the list and displays it. 
     * If tht list is empty return null
     */
    public JSONObject pop() {
        JSONObject json = new JSONObject();
        json.put("datatype", 2);
        json.put("type", "pop");
        //check for null
        if(this.state == null) {
            json.put("data", "null");
        } else {
            //remove first element, add it to the data to return
            StringList tempList = new StringList();
            //add the first element to what will be getting displayed
            json.put("data", state.get(0));
            //for loop to remake state without first index, starting at i = 1 to avoid 0 index
            for(int i = 1; i < state.size(); i++) {
                tempList.add(state.get(i));
            }

            //set state to templist which has the new correct list
            state = tempList;
        }
        
        return json;
    }

    /**
     * display method - displays the current list
     */
    public JSONObject display() {
        JSONObject json = new JSONObject();
        json.put("datatype", 3);
        json.put("type", "display");
        //enter data from StringList object into data
        json.put("data", state.toString());
        return json;
    }

    /**
     * count method - returns the number of elements in your list and displays the number
     */
    public JSONObject count() {
        int numOfElements = state.size();
        JSONObject json = new JSONObject();
        json.put("datatype", 4);
        json.put("type", "count");
        //enter data
        json.put("data", String.valueOf(numOfElements));
        return json;
    }

    /**
     * Switch<int int> method - switch the elements of the given indexes.
     * If one of the indexes is invalid return "null"
     */
    public JSONObject switchElements(int firstElement, int secondElement) {
        //check for if indexes are valid
        JSONObject json = new JSONObject();
        json.put("datatype", 5);
        json.put("type", "switch");

        if(firstElement < 0 || firstElement > state.size() || secondElement < 0 || secondElement > state.size()) {
            json.put("data", "null");
        } else {
            //perform the switch
            StringList tempList = new StringList();
            int lowerIndex = 0, higherIndex = 0;
            if(firstElement >= secondElement) {
                lowerIndex = secondElement;
                higherIndex = firstElement;
            } else {
                lowerIndex = firstElement;
                higherIndex = secondElement;
            }
            for(int i = 0; i < state.size(); i++) {
                if(i == lowerIndex) { //when at the lower index, set value to higher index
                    tempList.add(state.get(higherIndex));
                } else if (i == higherIndex) { //when at higher index, set value to lower index
                    tempList.add(state.get(lowerIndex));
                } else { //when not at high or low index entered, add elements to make it same as state
                    tempList.add(state.get(i));
                }
            } //end for look creating the tempList holding new state
            
            state = tempList;
            json.put("data", state.toString());
        }
        return json;
    }

    /**
     * quit method
     * 
     * @return
     */
    public JSONObject quit() {
        JSONObject json = new JSONObject();
        json.put("datatype", 0);
        json.put("type", "quit");
        json.put("data", "Bye!");
        return json;
    }
}
