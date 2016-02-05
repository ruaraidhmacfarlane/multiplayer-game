package lib;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

// Represents a location in the World (a vertex in the graph).
class Vertex
{
    public String name;             // Vertex name
    public String msg = "";         // Message about this location
    public Map<String,Edge> routes; // Association between direction
				     // (e.g. "north") and a path
				     // (Edge)
    public List<String> things;     
    				// The things (e.g. players) at
				     // this location

    public Vertex(String nm){
		name = nm; 
		routes = new HashMap<String,Edge>(); // Not synchronised
		things = new Vector<String>();       // Synchronised
    }

    public String toString(){
		String summary = "\n";
		summary += msg + "\n";
		Iterator iter = routes.keySet().iterator();
		String direction;
		while (iter.hasNext()){
		    direction = (String)iter.next();
		    summary += "To the " + direction + " there is " + ((Edge)routes.get(direction)).view + "\n";
		}
		iter = things.iterator();
		if (iter.hasNext()){
		    summary += "You can see: ";
		    do {
				summary += iter.next() + " ";
		    } while (iter.hasNext());
		}
		summary += "\n\n";
		return summary;
    }
}

