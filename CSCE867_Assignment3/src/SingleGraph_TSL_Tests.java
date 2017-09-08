import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Node;
import org.graphstream.graph.NullAttributeException;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.junit.Test;

public class SingleGraph_TSL_Tests {

	// constructor methods to simplify test cases
	private SingleGraph makeSingleGraph(String id) {
		SingleGraph sg = new SingleGraph(id);
		return sg;
	}
	private SingleGraph makeSingleGraph(String id, boolean strict, boolean auto_c) {
		SingleGraph sg = new SingleGraph(id, strict, auto_c);
		return sg;
	}
	// singleton tests
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
		assertTrue("hasVector did not find ArrayList attr",sg.hasVector(key));
	}
	// verify 0-length attribute key is invalid
	@Test
	public void LabelGraphEmpty() {
		// test case 9
		SingleGraph sg = makeSingleGraph("KeyLabelGraphEmpty");
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
	// test "has" methods starting with hasLabel() which is a
	// misnomer b/c it checks if a particular attribute key
	// exists (a label) not if the graph is labeled
	@Test
	public void hasLabel() {
		// test case 17
		SingleGraph sg = makeSingleGraph("hasLabel");
		String key = "KeyString";
		String value = "KeyValue";
		sg.addAttribute(key,value);
		assertTrue("hasLabel could not find attribute with key "+key,sg.hasLabel(key));
	}
	@Test
	public void hasNumber() {
		// test case 18
		SingleGraph sg = makeSingleGraph("hasNumber");
		String key = "KeyString";
		double value = 1.2345;
		sg.addAttribute(key,value);
		assertTrue("hasNumber could not find attribute with key "+key,sg.hasNumber(key));
	}
	@Test
	public void hasHash() {
		// test case 19
		SingleGraph sg = makeSingleGraph("hasHash");
		String key = "KeyString";
		HashMap<Integer,String> hm = new HashMap<Integer,String>();
		hm.put(new Integer(1), "String1");
		hm.put(new Integer(2), "String2");
		sg.addAttribute(key,hm);
		assertTrue("hasHash could not find attribute with key "+key,sg.hasHash(key));
	}
	@Test
	public void hasAttribute() {
		// test case 20
		SingleGraph sg = makeSingleGraph("hasAttribute");
		String key = "KeyString";
		String value = "KeyValue";
		sg.addAttribute(key,value);
		assertTrue("hasAttribute could not find attribute with key "+key,sg.hasAttribute(key));
	}
	@Test
	public void hasVector() {
		// test case 21
		SingleGraph sg = makeSingleGraph("hasVector");
		String key = "KeyString";
		ArrayList<String> value = new ArrayList<String>();
		value.add("String1");
		value.add("String");
		sg.addAttribute(key,value);
		assertTrue("hasVector could not find attribute with key "+key,sg.hasVector(key));
	}
	@Test
	public void hasArray() {
		// test case 22
		SingleGraph sg = makeSingleGraph("hasArray");
		Object[] array = new Object[2];
		array[0] = new String("String1");
		array[1] = new String("String2");
		String key = "KeyString";
		sg.addAttribute(key, array);
		assertTrue("hasArray could not find attribute with key "+key,sg.hasArray(key));
	}
	@Test
	public void getNodeBySet() {
		// test case 23
		SingleGraph sg = makeSingleGraph("getNodeBySet");
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addNode("Node3");
		sg.addNode("Node4");
		Collection<Node> nodeset = sg.getNodeSet();
		assertFalse("getNodeBySet node set empty",nodeset.isEmpty());
		int node_cnt = 0;
		Iterator<Node> iter = nodeset.iterator();
		while(iter.hasNext()) {
			Object node = iter.next();
			assertTrue("getNodeBySet non-node in set",node instanceof Node);
			node_cnt++;
		}
		assertTrue("getNodeBySet wrong # of nodes",node_cnt == 4);
	}
	@Test
	public void getEdgesBySet() {
		// test case 24
		SingleGraph sg = makeSingleGraph("getEdgesBySet");
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addNode("Node3");
		sg.addNode("Node4");
		sg.addEdge("Edge1", "Node1", "Node2");
		sg.addEdge("Edge2", "Node2", "Node3");
		sg.addEdge("Edge3", "Node3", "Node4");
		sg.addEdge("Edge4", "Node4", "Node1");
		Collection<Edge> edge_set = sg.getEdgeSet();
		assertFalse("getEdgesBySet edge set empty",edge_set.isEmpty());
		int edge_cnt = 0;
		Iterator<Edge> iter = edge_set.iterator();
		while(iter.hasNext()) {
			Object edge = iter.next();
			assertTrue("getEdgesBySet set element not an Edge object", edge instanceof Edge);
			edge_cnt++;
		}
		assertTrue("getEdgesBySet incorrect number of edges in set", edge_cnt == 4);
	}
	@Test
	public void RemoveNodeById() {
		// test case 25
		SingleGraph sg = makeSingleGraph("RemoveNodeById");
		Node node1 = sg.addNode("Node1");
		sg.removeNode(node1.getId());
		assertTrue("RemoveNodeById incorrect node count after rmv",sg.getNodeCount() == 0);
	}
	@Test
	public void RemoveNodeByIdx() {
		// test case 26
		SingleGraph sg = makeSingleGraph("RemoveNodeByIdx");
		Node node1 = sg.addNode("Node1");
		sg.removeNode(node1.getIndex());
		assertTrue("RemoveNodeByIdx incorrect node count after rmv",sg.getNodeCount() == 0);
	}
	@Test
	public void RemoveNodeByNode() {
		// test case 27
		SingleGraph sg = makeSingleGraph("RemoveNodeByNode");
		Node node1 = sg.addNode("Node1");
		sg.removeNode(node1);
		assertTrue("RemoveNodeById incorrect node count after rmv",sg.getNodeCount() == 0);
	}
	@Test
	public void RemoveEdgeById() {
		// test case 28
		SingleGraph sg = makeSingleGraph("ArrayAttr");
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addNode("Node3");
		sg.addNode("Node4");
		Edge edge1 = sg.addEdge("Edge1", "Node1", "Node2");
		sg.addEdge("Edge2", "Node2", "Node3");
		sg.addEdge("Edge3", "Node3", "Node4");
		sg.addEdge("Edge4", "Node4", "Node1");
		assertTrue("RemoveEdgeById wrong # of starting edges",sg.getEdgeCount() == 4);
		sg.removeEdge(edge1.getId());
		assertTrue("RemoveEdgeById wrong # of edges after rmv",sg.getEdgeCount() == 3);
	}
	@Test
	public void RemoveEdgeByIdx() {
		// test case 29
		SingleGraph sg = makeSingleGraph("RemoveEdgeByIdx");
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addNode("Node3");
		sg.addNode("Node4");
		Edge edge1 = sg.addEdge("Edge1", "Node1", "Node2");
		sg.addEdge("Edge2", "Node2", "Node3");
		sg.addEdge("Edge3", "Node3", "Node4");
		sg.addEdge("Edge4", "Node4", "Node1");
		assertTrue("RemoveEdgeByIdx wrong # of starting edges",sg.getEdgeCount() == 4);
		sg.removeEdge(edge1.getIndex());
		assertTrue("RemoveEdgeByIdx wrong # of edges after rmv",sg.getEdgeCount() == 3);
	}
	@Test
	public void RemoveEdgeByEdge() {
		// test case 30
		SingleGraph sg = makeSingleGraph("RemoveEdgeByEdge");
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addNode("Node3");
		sg.addNode("Node4");
		Edge edge1 = sg.addEdge("Edge1", "Node1", "Node2");
		sg.addEdge("Edge2", "Node2", "Node3");
		sg.addEdge("Edge3", "Node3", "Node4");
		sg.addEdge("Edge4", "Node4", "Node1");
		assertTrue("RemoveEdgeByEdge wrong # of starting edges",sg.getEdgeCount() == 4);
		sg.removeEdge(edge1);
		assertTrue("RemoveEdgeByEdge wrong # of edges after rmv",sg.getEdgeCount() == 3);
	}
	@Test
	public void RemoveEdgeByNodeIds() {
		// test case 31
		SingleGraph sg = makeSingleGraph("RemoveEdgeByNodeIds");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addNode("Node3");
		sg.addNode("Node4");
		Edge edge1 = sg.addEdge("Edge1", "Node1", "Node2");
		sg.addEdge("Edge2", "Node2", "Node3");
		sg.addEdge("Edge3", "Node3", "Node4");
		sg.addEdge("Edge4", "Node4", "Node1");
		assertTrue("RemoveEdgeByNodeIds wrong # of starting edges",sg.getEdgeCount() == 4);
		sg.removeEdge(node1.getId(),node2.getId());
		assertTrue("RemoveEdgeNodeIds wrong # of edges after rmv",sg.getEdgeCount() == 3);
		Edge edge_tmp = sg.getEdge(edge1.getId());
		assertTrue("non-null edge returned on getEdge after remove",edge_tmp == null);
	}
	@Test
	public void RemoveEdgeByNodes() {
		// test case 32
		SingleGraph sg = makeSingleGraph("RemoveEdgeByNodes");
		Node node1 = sg.addNode("Node1");
		Node node2 = sg.addNode("Node2");
		sg.addNode("Node3");
		sg.addNode("Node4");
		Edge edge1 = sg.addEdge("Edge1", "Node1", "Node2");
		sg.addEdge("Edge2", "Node2", "Node3");
		sg.addEdge("Edge3", "Node3", "Node4");
		sg.addEdge("Edge4", "Node4", "Node1");
		assertTrue("RemoveEdgeByNodeIds wrong # of starting edges",sg.getEdgeCount() == 4);
		sg.removeEdge(node1,node2);
		assertTrue("RemoveEdgeNodeIds wrong # of edges after rmv",sg.getEdgeCount() == 3);
		Edge edge_tmp = sg.getEdge(edge1.getId());
		assertTrue("non-null edge returned on getEdge after remove",edge_tmp == null);
	}
	@Test
	public void RmvGraphAttribute() {
		// test case 33
		SingleGraph sg = makeSingleGraph("RmvGraphAttr");
		String key = "KeyString";
		String value = "AttributeValue";
		sg.addAttribute(key, value);
		assertTrue("RmvGraphAttribute wrong # of attrs after add",sg.getAttributeCount() == 1);
		sg.removeAttribute(key);
		assertTrue("RmvGraphAttribute wrong # of attrs after rmv",sg.getAttributeCount() == 0);
	}
	@Test
	public void ChgGraphAttribute() {
		// test case 34
		SingleGraph sg = makeSingleGraph("ChgGraphAttr");
		String key = "KeyString";
		String value = "AttributeValue";
		String changed_value = "OtherAttributeValue";
		sg.addAttribute(key, value);
		assertTrue("RmvGraphAttribute wrong # of attrs after add",sg.getAttributeCount() == 1);
		sg.changeAttribute(key, changed_value);
		assertTrue("RmvGraphAttribute wrong attr value after chg",((String)sg.getAttribute(key)).equals(changed_value));
	}
	@Test
	public void NullAttrsAreErrors() {
		// test case 35
		SingleGraph sg = makeSingleGraph("addNodeAttr");
		String key = "AttrKey";
		assertFalse("null attrs are errors unexpectedly true on init",sg.nullAttributesAreErrors());
		sg.setNullAttributesAreErrors(true);
		assertTrue("null attrs are errors should be true",sg.nullAttributesAreErrors());
		String badval = null;
		try {
		    badval = sg.getAttribute(key);
		} catch (NullAttributeException e) {
			assertTrue("value "+badval+" returned on key "+key, true);
		} catch (Exception e) {
			fail("unexpected exception for null attrs are errors test: "+e.getMessage());
		}
	}
	@Test
	public void ClearGraph() {
		SingleGraph sg = makeSingleGraph("ClearGraphTest", true, false);
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addEdge("EdgeDirected1", "Node1", "Node2", true);
		assertTrue("wrong # of nodes before clear",sg.getNodeCount() == 2);
		assertTrue("wrong # of edges before clear", sg.getEdgeCount() == 1);
		sg.clear();
		assertTrue("wrong # of nodes after clear",sg.getNodeCount() == 0);
		assertTrue("wrong # of edges after clear", sg.getEdgeCount() == 0);
	}
	@Test
	public void DisplayGraph() {
		SingleGraph sg = makeSingleGraph("ClearGraphTest", true, false);
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addEdge("EdgeDirected1", "Node1", "Node2", true);
		assertTrue("wrong # of nodes before clear",sg.getNodeCount() == 2);
		assertTrue("wrong # of edges before clear", sg.getEdgeCount() == 1);
		Viewer view = sg.display();
		assertTrue("null viewer returned by display",view != null);
	}
	@Test
	public void TestCase38() {
		SingleGraph sg = makeSingleGraph("TestCase38", true, false);
		sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addEdge("EdgeDirected1", "Node1", "Node2", true);
		Iterator<Node> node_iter = sg.getNodeIterator();
		Iterator<Edge> edge_iter = sg.getEdgeIterator();
		String key = "attrib_key";
		String nvalue = "node_attrib_value";
		String n2value = "node_chgd_value";
		String evalue = "edge_attrib_value";
		String e2value = "edge_chgd_value";
		while(node_iter.hasNext()) {
			Node node = node_iter.next();
			node.addAttribute(key, nvalue);
			assertTrue("attr label not found",node.hasLabel(key));
			node.changeAttribute(key, n2value);
			node.clearAttributes();
		}
		while(edge_iter.hasNext()){
			Edge edge = edge_iter.next();
			edge.addAttribute(key, evalue);
			assertTrue("attr label not found",edge.hasLabel(key));
			assertTrue("edge not directed",edge.isDirected());
			edge.changeAttribute(key, e2value);
			edge.clearAttributes();
		}
	}
	@Test
	public void TestCase39() {
		SingleGraph sg = makeSingleGraph("TestCase39", true, false);
		Node node1 = sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addEdge("EdgeDirected1", "Node1", "Node2", false);
		Iterator<Node> node_iter = sg.getNodeIterator();
		Iterator<Edge> edge_iter = sg.getEdgeIterator();
		String key = "attrib_key";
		String nvalue = "node_attrib_value";
		String evalue = "edge_attrib_value";
		while(node_iter.hasNext()) {
			Node node = node_iter.next();
			node.addAttribute(key, nvalue);
			assertTrue("attr label not found",node.hasLabel(key));
		}
		while(edge_iter.hasNext()){
			Edge edge = edge_iter.next();
			edge.addAttribute(key, evalue);
			assertTrue("attr label not found",edge.hasLabel(key));
			assertFalse("edge not directed",edge.isDirected());
		}
		assertTrue("attr label not found", node1.hasLabel(key));
	}
	@Test
	public void TestCase40() {
		SingleGraph sg = makeSingleGraph("TestCase40", false, true);
		Node node1 = sg.addNode("Node1");
		// auto-create-node
		sg.addEdge("EdgeDirected1", "Node1", "Node2", true);
		Iterator<Node> node_iter = sg.getNodeIterator();
		Iterator<Edge> edge_iter = sg.getEdgeIterator();
		String key = "attrib_key";
		String nvalue = "node_attrib_value";
		String evalue = "edge_attrib_value";
		while(node_iter.hasNext()) {
			Node node = node_iter.next();
			node.addAttribute(key, nvalue);
			assertTrue("attr label not found",node.hasLabel(key));
		}
		while(edge_iter.hasNext()){
			Edge edge = edge_iter.next();
			edge.addAttribute(key, evalue);
			assertTrue("attr label not found",edge.hasLabel(key));
			assertTrue("edge not directed",edge.isDirected());
		}
		assertTrue("attr label not found", node1.hasLabel(key));
	}
	@Test
	public void TestCase41() {
		SingleGraph sg = makeSingleGraph("TestCase41", false, true);
		Node node1 = sg.addNode("Node1");
		// auto-create-node
		sg.addEdge("EdgeDirected1", "Node1", "Node2", false);
		Iterator<Node> node_iter = sg.getNodeIterator();
		Iterator<Edge> edge_iter = sg.getEdgeIterator();
		String key = "attrib_key";
		String nvalue = "node_attrib_value";
		String evalue = "edge_attrib_value";
		while(node_iter.hasNext()) {
			Node node = node_iter.next();
			node.addAttribute(key, nvalue);
			assertTrue("attr label not found",node.hasLabel(key));
		}
		while(edge_iter.hasNext()){
			Edge edge = edge_iter.next();
			edge.addAttribute(key, evalue);
			assertTrue("attr label not found",edge.hasLabel(key));
			assertFalse("edge not directed",edge.isDirected());
		}
		assertTrue("attr label not found", node1.hasLabel(key));
	}
	@Test
	public void TestCase42() {
		SingleGraph sg = makeSingleGraph("TestCase42", false, false);
		Node node1 = sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addEdge("EdgeDirected1", "Node1", "Node2", true);
		Iterator<Node> node_iter = sg.getNodeIterator();
		Iterator<Edge> edge_iter = sg.getEdgeIterator();
		String key = "attrib_key";
		String nvalue = "node_attrib_value";
		String evalue = "edge_attrib_value";
		while(node_iter.hasNext()) {
			Node node = node_iter.next();
			node.addAttribute(key, nvalue);
			assertTrue("attr label not found",node.hasLabel(key));
		}
		while(edge_iter.hasNext()){
			Edge edge = edge_iter.next();
			edge.addAttribute(key, evalue);
			assertTrue("attr label not found",edge.hasLabel(key));
			assertTrue("edge not directed",edge.isDirected());
		}
		assertTrue("attr label not found", node1.hasLabel(key));
	}
	@Test
	public void TestCase43() {
		SingleGraph sg = makeSingleGraph("TestCase43", false, false);
		Node node1 = sg.addNode("Node1");
		sg.addNode("Node2");
		sg.addEdge("EdgeDirected1", "Node1", "Node2", false);
		Iterator<Node> node_iter = sg.getNodeIterator();
		Iterator<Edge> edge_iter = sg.getEdgeIterator();
		String key = "attrib_key";
		String nvalue = "node_attrib_value";
		String evalue = "edge_attrib_value";
		while(node_iter.hasNext()) {
			Node node = node_iter.next();
			node.addAttribute(key, nvalue);
			assertTrue("attr label not found",node.hasLabel(key));
		}
		while(edge_iter.hasNext()){
			Edge edge = edge_iter.next();
			edge.addAttribute(key, evalue);
			assertTrue("attr label not found",edge.hasLabel(key));
			assertFalse("edge not directed",edge.isDirected());
		}
		assertTrue("attr label not found", node1.hasLabel(key));
	}
}
