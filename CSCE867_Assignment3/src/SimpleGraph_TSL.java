import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Test;

public class SimpleGraph_TSL {

	// constructor methods to simplify test cases
	private SingleGraph makeSingleGraph(String id) {
		SingleGraph sg = new SingleGraph(id);
		return sg;
	}
	private SingleGraph makeSingleGraph(String id, boolean strict, boolean auto_c) {
		SingleGraph sg = new SingleGraph(id, strict, auto_c);
		return sg;
	}
	private SingleGraph makeSingleGraph(String id, boolean strict, boolean auto_c, int nodes, int edges) {
		SingleGraph sg = new SingleGraph(id, strict, auto_c, nodes, edges);
		return sg;
	}

	@Test
	public void StrictByMethodTest() {
		// test case 1
		// start with graph set strict = false
		SingleGraph sg = makeSingleGraph("StrictByMethod", false, false);
		assertFalse("graph should not have strict set", sg.isStrict());
		sg.setStrict(true);
		assertTrue("graph should have strict set", sg.isStrict());
	}
	@Test
	public void AutoCreateByMethod() {
		// test case 2
		SingleGraph sg = makeSingleGraph("AutoCreateByMethod", false,false);
		assertFalse("graph should not have auto create set", sg.isAutoCreationEnabled());
		sg.setAutoCreate(true);
		assertTrue("graph should have auto create set", sg.isAutoCreationEnabled());
	}
	// ignoring the fact that there is a second write() method
	// that allows defining a FileSink object to dump to
	@Test
	public void WriteGraph() {
		// test case 3
		SingleGraph sg = makeSingleGraph("WriteGraph");
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addEdge("Edge1", "Node1", "Node2");
		try {
			sg.write("graph_output.dgsz"); // valid file types, jpg,dgsz,tex,gml,dot
		} catch (IOException e) {
			fail("write graph failed with IO Exception");
		}
		File outfile = new File("graph_output.dgsz");
		if(outfile.exists() && outfile.isFile() && outfile.canRead()) { 
		    assertTrue("graph_output.dgsz is 0 length",outfile.length() > 0);
		    outfile.delete();
		}
		else fail("graph_output.dgsz not created, not a file, or not readable on write()");
	}
	
	@Test
	public void AttributeString() {
		// test case 4
		SingleGraph sg = makeSingleGraph("StringAttr");
		String attrib = "String Attribute Value";
		String key = "KeyString";
		sg.addAttribute(key, attrib);
		assertTrue("hasAttribute for String fails on key: "+key,sg.hasAttribute(key));
		assertTrue("hasAttribute fails String class type",sg.hasAttribute(key,String.class));
	}
	@Test
	public void AttributeDouble() {
		// test case 5
		SingleGraph sg = makeSingleGraph("StringAttr");
		double attrib = 0.23456789;
		String key = "KeyString";
		sg.addAttribute(key, attrib);
		assertTrue("hasNumber fails on double attribute", sg.hasNumber(key));
	}
	// docs imply this should work with ArrayList or Vector but
	// only test done in actual code is to check for Object[] array
	@Test
	public void AttributeArray() {
		// test case 6
		SingleGraph sg = makeSingleGraph("ArrayAttr");
		Object[] array = new Object[2];
		array[0] = new String("String1");
		array[1] = new String("String2");
		String key = "KeyString";
		sg.addAttribute(key, array);
		assertTrue("hasArray fails on key: "+key,sg.hasArray(key));
		assertTrue("hasAttribute fails on ArrayList by key",sg.hasAttribute(key));
		assertTrue("hasAttribute fails on ArrayList class",sg.hasAttribute(key,Object.class));
	}
	@Test
	public void AttributeHash() {
		// test case 7
		SingleGraph sg = makeSingleGraph("HashAttr");
		HashMap<Integer,String> hm = new HashMap<Integer,String>();
		hm.put(new Integer(1), "String1");
		hm.put(new Integer(2), "String2");
		String key = "KeyString";
		sg.addAttribute(key, hm);
		assertTrue("hasHash fails on HashMap",sg.hasHash(key));
	}
	// method is a misnomer, hasVector() expects to find an ArrayList
	@Test
	public void AttributeVector() {
		// test case 8
		SingleGraph sg = makeSingleGraph("VectorAttr");
		ArrayList<String> vect = new ArrayList<String>();
		vect.add("String1");
		vect.add("String");
		String key = "KeyString";
		sg.addAttribute(key,vect);
		assertTrue(sg.hasVector(key));
	}
	// verify 0-length attribute key is invalid
	@Test
	public void LabelGraphEmpty() {
		// test case 9
		SingleGraph sg = makeSingleGraph("hasLableForAttr");
		String key = "";
		String value = "KeyValue";
		try {
			sg.addAttribute(key, value);
		    fail("empty attribute key accepted by add"); // should never hit this
		} catch(StringIndexOutOfBoundsException e) {
			assertTrue(true); // caught exception on empty key label
		}
		catch(Exception e) {
			fail("wrong exception returned on empty key/label "+e.getMessage());
		}
	}
	@Test
	public void CreateOneEdgeNoNodeStrict() {
		// test case 10
		SingleGraph sg = makeSingleGraph("StrictEdgeCreateError");
		try {
		    sg.addEdge("ErrorEdge", "Node1", "Node2");
		    fail("could add edge w/no nodes & strict set");
		} catch (ElementNotFoundException e) {
			assertTrue(true);
		}
		catch(Exception e) {
			fail("Wrong exception returned on edge add w/o nodes strict"+e.getMessage());
		}
	}
	@Test
	public void CreateEdgeByNodeId() {
		// test case 11
		SingleGraph sg = makeSingleGraph("EdgeByNodeId");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addEdge("Edge1", node1.getId(), node2.getId());
		assertTrue("wrong edge returned or edge ID wrong by ID",sg.getEdge("Edge1").getId().equals("Edge1"));
	}
	@Test
	public void CreateEdgeByNode() {
		// test case 12
		SingleGraph sg = makeSingleGraph("EdgeByNode");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addEdge("Edge1", node1, node2);
		assertTrue("wrong edge returned or edge ID wrong by Node",sg.getEdge("Edge1").getId().equals("Edge1"));
	}
	@Test
	public void CreateEdgeByIdx() {
		// test case 13
		SingleGraph sg = makeSingleGraph("EdgeByNodeIdx");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addEdge("Edge1", node1.getIndex(), node2.getIndex());
		assertTrue("wrong edge returned or edge ID wrong by IDX",sg.getEdge("Edge1").getId().equals("Edge1"));
	}
	//
	// test directed edge adds
	//
	@Test
	public void CreateEdgeByNodeId_Dir() {
		// test case 14
		SingleGraph sg = makeSingleGraph("EdgeByNodeId_Directed");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addEdge("Edge1", node1.getId(), node2.getId(), true);
		assertTrue("edge not directed by ID",sg.getEdge("Edge1").isDirected());
		assertTrue("wrong directed edge returned or edge ID wrong by ID",sg.getEdge("Edge1").getId().equals("Edge1"));
	}
	@Test
	public void CreateEdgeByNode_Dir() {
		// test case 15
		SingleGraph sg = makeSingleGraph("EdgeByNode_Directed");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addEdge("Edge1", node1, node2, true);
		assertTrue("edge not directed by Node",sg.getEdge("Edge1").isDirected());
		assertTrue("wrong directed edge returned or edge ID wrong by Node",sg.getEdge("Edge1").getId().equals("Edge1"));
	}
	@Test
	public void CreateEdgeByNodeIdx_Dir() {
		// test case 16
		SingleGraph sg = makeSingleGraph("EdgeByNodeIdx_Directed");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addEdge("Edge1", node1.getIndex(), node2.getIndex(), true);
		assertTrue("edge not directed",sg.getEdge("Edge1").isDirected());
		assertTrue("wrong directed edge returned or edge ID wrong by IDX",sg.getEdge("Edge1").getId().equals("Edge1"));
	}
	//
	// test has* methods starting with hasLabel() which is a
	// misnomer b/c it checks if a particular attribute key
	// exists (a label) not if the graph is labeled
	@Test
	public void hasLabel() {
		// test case 17
		SingleGraph sg = makeSingleGraph("hasLabel");
		String key = "KeyString";
		String value = "KeyValue";
		sg.addAttribute(key,value);
		assertTrue("getLabel could not find attribute with key "+key,sg.hasLabel(key));
	}
}

