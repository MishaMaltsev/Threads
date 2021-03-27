package taskone;

import java.util.List;
import java.util.ArrayList;

class StringList {
    
    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    }

    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    }

    public int size() {
        return strings.size();
    }

    public String toString() {
        return strings.toString();
    }

    /**
     * adding a getter at specific index
     */
    public String get(int index) {
        if(index >= 0 && index < strings.size()) {
            return strings.get(index);
        } else { //out of linked list bounds
            return null;
        }
    }
}