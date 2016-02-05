package lib;

// Represents an path in the World (an edge in a graph).
class Edge
{
    public Vertex dest;   // Your destination if you walk down this path
    public String view;   // What you see if you look down this path
    
    public Edge(Vertex d, String v)
    {
        dest = d;
		view = v;
    }
}

