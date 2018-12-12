package com.xilinx.rapidwright.linkblaze;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.xilinx.rapidwright.design.Cell;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.design.DesignTools;
import com.xilinx.rapidwright.design.Module;
import com.xilinx.rapidwright.design.ModuleImpls;
import com.xilinx.rapidwright.design.ModuleInst;
import com.xilinx.rapidwright.design.Net;
import com.xilinx.rapidwright.design.NetType;
import com.xilinx.rapidwright.design.PinType;
import com.xilinx.rapidwright.design.SiteInst;
import com.xilinx.rapidwright.design.SitePinInst;
import com.xilinx.rapidwright.design.Port;
import com.xilinx.rapidwright.design.PortType;
import com.xilinx.rapidwright.design.Unisim;
import com.xilinx.rapidwright.design.tools.LUTTools;
import com.xilinx.rapidwright.device.Site;
import com.xilinx.rapidwright.device.SiteTypeEnum;
import com.xilinx.rapidwright.device.Tile;
import com.xilinx.rapidwright.edif.EDIFCell;
import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFDirection;
import com.xilinx.rapidwright.edif.EDIFLibrary;
import com.xilinx.rapidwright.edif.EDIFNet;
import com.xilinx.rapidwright.edif.EDIFNetlist;
import com.xilinx.rapidwright.edif.EDIFParser;
import com.xilinx.rapidwright.edif.EDIFPort;
import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFTools;
import com.xilinx.rapidwright.linkblaze.LinkBlazeResources.NodeType;
import com.xilinx.rapidwright.placer.handplacer.HandPlacer;
/*import com.xilinx.rapidwright.unisim.Unisim;
import com.xilinx.rapidwright.unisim.UnisimManager;
import com.xilinx.rapidwright.util.DesignTools;*/

public class LinkBlazeGenerator {

	private static Map<String, Integer> nodeTileMap;
	private static Map<String, Integer> isNodeAdded;
	private static Map<String, Module> nodeTypeMap;
	private static String topNode;
	private static String bottomNode;
	private static Design d1;
	private static final String DEVICE_NAME = "xcvu9p-flgb2104-2-i";
	private static final String DESIGN_NAME = "result";//"design1";
	//private static final String DCP_FOLDER_PATH = "/home/jmandebi/node_stitching/archive/november07_2018/";
	private static final String DCP_FOLDER_PATH = "/home/jmandebi/node_stitching/";
	// private static final String dcpFolderPath =
	// "/home/jmandebi/node_stitching/Not_routing/test/";
	// private static final String dcpFolderPath =
	// "/home/jmandebi/interactive_bash_scripts/";

	private static final String PCI_MASTER_DCP = "pciMasterRouted.dcp";
	private static final String REGULAR_MASTER_DCP = "regularMasterRouted.dcp";
	private static final String SLAVE_DCP = "slaveRouted.dcp";
	private static final String SLR_CROSS_DCP = "slrCrossRouted.dcp";
	private static final int CLOCK_REGION_HEIGHT = 60;
	private static int rapidWrightTokenTransferDnCounter;
	private static int rapidWrightTokenTransferUpCounter;
	private static int rapidWrightTokenToRtr;
	/**
	 * Used to bring nodes closer because Vivado tend to place each of them at
	 * the bottom of a FSR. This way we might globally free some space on the
	 * FPGA
	 */
	private static final int CLOSENESS_THRESHOLD = 0;// 25
	private static final int NODE2_7_CLOSENESS_THRESHOLD = 4;// 25
	
	/**
	 * Used to select whether or not unconnected internal ports must be connected to 
	 * the ground or exported as ports on the top module 
	 */
	private static final int CONNECT_GROUND = 1;

	/**
	 * Function that initialize all the data structures used to automatically
	 * generate LinkBlaze
	 */
	public static void initializeLinkBlazeGenerator() {
		d1 = new Design(DESIGN_NAME, DEVICE_NAME);
		nodeTileMap = new HashMap<String, Integer>();
		/*
		 * The layout stored in "nodeTileMap" only works we nodes built for pblocks 
		 * ranges as described below. Using different pblock ranges will imply updating
		 * the values stored in nodeTileMap: not doing so will cause placement failures
		 * 
		 * Slave node built on node 7 with the pblock range SLICE_X96Y424:SLICE_X105Y475
		 *Master node built on node 10 with the pblock range SLICE_X96Y664:SLICE_X105Y715 
		 *PCI Master node built on node 2 with the pblock range SLICE_X96Y137:SLICE_X105Y188
		 *   */
		
		 nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[0], -1
				* (CLOCK_REGION_HEIGHT * 10) + (CLOSENESS_THRESHOLD));
		 nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[1], -1
				* (CLOCK_REGION_HEIGHT * 4) + (NODE2_7_CLOSENESS_THRESHOLD));
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[2], 0
				* (CLOCK_REGION_HEIGHT * 1)-2);
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[3], 0);
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[5], -1
				* (CLOCK_REGION_HEIGHT * 5) - (NODE2_7_CLOSENESS_THRESHOLD));
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[6], 1
				* (CLOCK_REGION_HEIGHT * 1) );
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[7], 0
				* (CLOCK_REGION_HEIGHT * 1) - (NODE2_7_CLOSENESS_THRESHOLD+6)  );
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[8],
				CLOCK_REGION_HEIGHT * 5);
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[10], 0
				* (CLOCK_REGION_HEIGHT * 10));
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[11], 1
				* (CLOCK_REGION_HEIGHT * 5) );
		nodeTileMap.put(LinkBlazeResources.NODE_ARRAY[12],
				1 * (CLOCK_REGION_HEIGHT * 2) );
		
		
		
		

		// 0 means that the node isn't yet added into the design, otherwise put
		// 1
		isNodeAdded = new HashMap<String, Integer>();
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[0], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[1], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[2], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[3], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[5], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[6], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[7], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[8], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[10], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[11], 0);
		isNodeAdded.put(LinkBlazeResources.NODE_ARRAY[12], 0);

		/*
		 * nodeTypeMap = new HashMap<String,Module>(); Module master =
		 * createModule("/home/jmandebi/node_stitching/master_routed.dcp");
		 * Module slave =
		 * createModule("/home/jmandebi/node_stitching/slave_routed.dcp");
		 * 
		 * nodeTypeMap.put("master", master); nodeTypeMap.put("slave", slave);
		 */

	}

	public static Module createModule(String dcFileName) {
		Design d1 = Design.readCheckpoint(dcFileName);
		String metadata = dcFileName.replace(".dcp", "_0_metadata.txt");
		Module m1 = new Module(d1,metadata );
        
		return m1;
	}

	/**
	 * Function that uniquefies the name of each cell
	 */
	public static void uniquefy() {
		Set<String> uniqifiedNetlists = new HashSet<>();
		for (ModuleInst mi : d1.getModuleInsts()) {
           
			EDIFNetlist n = mi.getModule().getNetlist();
			String modName = mi.getName();
			if (uniqifiedNetlists.contains(modName))
				continue;
			uniqifiedNetlists.add(modName);

			EDIFCellInst block = d1.getNetlist()
					.getCellInstFromHierName(mi.getName());
			String blockCellName = n.getName();

			// Uniqueify cell names to avoid Op_math collisions
			String newCellName = blockCellName + "_" + modName;
			n.changeTopName(newCellName);
			EDIFCell c = block.getCellType();
			EDIFLibrary l = block.getCellType().getLibrary();
			l.removeCell(c);
			block.getCellType().rename(newCellName);
			l.addCell(c);

			// Make all cells unique by name before they get consolidated
			for (EDIFLibrary lib : n.getLibraries()) {
				if (lib.getName().equals(
						EDIFTools.EDIF_LIBRARY_HDI_PRIMITIVES_NAME))
					continue;
				lib.uniqueifyCellsWithPrefix(newCellName + "_");
			}
			d1.getNetlist().migrateCellAndSubCells(n.getTopCell());
			block.setCellType(n.getTopCell());
		}
		// Make sure each module instance has an entry in the EDIFLibrary
		EDIFCell top = d1.getNetlist().getTopCell();
		EDIFLibrary work = d1.getNetlist().getLibrary(
				EDIFTools.EDIF_LIBRARY_WORK_NAME);
		work.addCell(top);

		for (ModuleInst mi : d1.getModuleInsts()) {
			EDIFCellInst inst = EDIFTools.getEDIFCellInst(
					d1.getNetlist(), mi.getName());
			if (inst == null)
				throw new RuntimeException(
						"ERROR: Couldn't update EDIF cell instance.");
			String netlistName = mi.getModule().getNetlist().getName();
			String cellName = netlistName + "_" + netlistName;

			EDIFCell cellType = work.getCell(netlistName + "_" + netlistName);
			if (cellType == null)
				throw new RuntimeException(
						"ERROR: Couldn't update EDIF cell type " + netlistName
								+ ", no cell in work called " + cellName + "\n"
								+ work.getCells());
			inst.setCellType(cellType);
		}
	}

	/**
	 * Place the node whose name is specified on the corresponding clock region
	 * on the FPGA
	 * 
	 * @param nodeName
	 *            name of the node
	 */
	public static void placeNodeOnTile(String nodeName) {

		ModuleInst mi = d1.getModuleInst(nodeName);
		SiteTypeEnum sType = SiteTypeEnum.SLICEL;
		Tile t = mi.getLowerLeftTile(sType);
		int tileOffset = nodeTileMap.get(nodeName);

		Tile targetTile = t.getTileXYNeighbor(0, tileOffset);
		mi.placeMINearTile(targetTile, sType);
	}

	/**
	 * Function that generate the LinkBlaze interconnect based on user inputs
	 * */
	public static void generateLinkBlaze(String linkBlazeTopology) {
		// add the PCI master to the design(the PCI master node is the only node
		// always present)
		addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[2],
				NodeType.PCIMASTER);

		// if DDR0 is present, we add node0 and node1
		if (linkBlazeTopology.charAt(3) == '1') {
			topNode = LinkBlazeResources.NODE_ARRAY[1];
			bottomNode = LinkBlazeResources.NODE_ARRAY[0];
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[0],
					NodeType.MASTER);
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[1],
					NodeType.SLAVE);
		}

		/*
		 * if DDR2 is present, I only care about adding node5 and node6 (the PCI
		 * master is already added to the design)
		 */
		if (linkBlazeTopology.charAt(2) == '1') {
			topNode = LinkBlazeResources.NODE_ARRAY[6];
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[3],
					NodeType.SLRCROSS);
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[5],
					NodeType.MASTER);
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[6],
					NodeType.SLAVE);
		}

		/*
		 * if DDR1 is present, I need to place node7 and node 10 and check if
		 * node6 is already there to allow the PCI master to access the DDR
		 */
		if (linkBlazeTopology.charAt(1) == '1') {
			topNode = LinkBlazeResources.NODE_ARRAY[10];
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[7],
					NodeType.SLAVE);
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[8],
					NodeType.SLRCROSS);
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[10],
					NodeType.MASTER);

			if (isNodeAdded.get(LinkBlazeResources.NODE_ARRAY[6]) == 0)// if
																		// node6
																		// isn't
																		// yet
																		// placed,
																		// I
																		// need
																		// to
																		// place
																		// node6
																		// and
																		// node5
			{
				addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[3],
						NodeType.SLRCROSS);
				addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[5],
						NodeType.MASTER);
				addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[6],
						NodeType.SLAVE);
			}
		}
		/*
		 * if DDR3 is present, I need to introduce node12 and node 11 in the
		 * design. To allow the PCI master to access the DDR, I check if node 10
		 * is present (if not present, DDR1 wasn't used), if not I also check
		 * node6 (to check if DDR2 was already in there)
		 */
		if (linkBlazeTopology.charAt(0) == '1') {
			topNode = LinkBlazeResources.NODE_ARRAY[12];
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[11],
					NodeType.SLAVE);
			addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[12],
					NodeType.MASTER);

			if (isNodeAdded.get(LinkBlazeResources.NODE_ARRAY[10]) == 0)// if
																		// node10
																		// isn't
																		// yet
																		// placed,
																		// I
																		// need
																		// to
																		// place
																		// node10
																		// and
																		// node7
			{
				addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[7],
						NodeType.SLAVE);
				addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[8],
						NodeType.SLRCROSS);
				addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[10],
						NodeType.MASTER);

				if (isNodeAdded.get(LinkBlazeResources.NODE_ARRAY[6]) == 0)// if
																			// node6
																			// isn't
																			// yet
																			// placed,
																			// I
																			// need
																			// to
																			// place
																			// node6
																			// and
																			// node5
				{
					addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[3],
							NodeType.SLRCROSS);
					addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[5],
							NodeType.MASTER);
					addNodeToDesign(d1, LinkBlazeResources.NODE_ARRAY[6],
							NodeType.SLAVE);
				}
			}
		}
		uniquefy();

		/* Now we use "isNodeAdded" to connect nodes in the right way */

		// so we have DDR0 in the design
		if (isNodeAdded.get(LinkBlazeResources.NODE_ARRAY[1]) == 1) {
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[1],LinkBlazeResources.NODE_ARRAY[2]);
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[2],LinkBlazeResources.NODE_ARRAY[0]);
			
			/* INTIAL WAY OF CONNECTING NODES BEFORE SWAPPING node2-node1 and node6-node7
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[2],LinkBlazeResources.NODE_ARRAY[1]);
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[1],LinkBlazeResources.NODE_ARRAY[0]);*/
		}
		// so we have DDR2 in the design
		if (isNodeAdded.get(LinkBlazeResources.NODE_ARRAY[6]) == 1) {
			
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[7],LinkBlazeResources.NODE_ARRAY[5]);
			slrCrossing(d1, LinkBlazeResources.NODE_ARRAY[5],LinkBlazeResources.NODE_ARRAY[1]);
			
			/* INTIAL WAY OF CONNECTING NODES BEFORE SWAPPING node2-node1 and node6-node7
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[6],LinkBlazeResources.NODE_ARRAY[5]);
			slrCrossing(d1, LinkBlazeResources.NODE_ARRAY[5],LinkBlazeResources.NODE_ARRAY[2]);*/
		}
		// so we have DDR1 in the design
		if (isNodeAdded.get(LinkBlazeResources.NODE_ARRAY[7]) == 1) {
			slrCrossing(d1, LinkBlazeResources.NODE_ARRAY[10],LinkBlazeResources.NODE_ARRAY[6]);
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[6],LinkBlazeResources.NODE_ARRAY[7]);
			
			/* INTIAL WAY OF CONNECTING NODES BEFORE SWAPPING node2-node1 and node6-node7
			slrCrossing(d1, LinkBlazeResources.NODE_ARRAY[10],LinkBlazeResources.NODE_ARRAY[7]);
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[7],LinkBlazeResources.NODE_ARRAY[6]);*/
		}
		// so we have DDR3 in the design
		if (isNodeAdded.get(LinkBlazeResources.NODE_ARRAY[11]) == 1) {
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[12],LinkBlazeResources.NODE_ARRAY[11]);
			connectNode(d1, LinkBlazeResources.NODE_ARRAY[11],LinkBlazeResources.NODE_ARRAY[10]);
		}

		connectRemainingPorts(d1);
									
		/* Now we place node in clock regions */
		for (Map.Entry<String, Integer> entry : isNodeAdded.entrySet()) {
			if (entry.getValue() == 1)
				placeNodeOnTile(entry.getKey());
		}

		updateLUTEquation(d1);
        
		d1.setDesignOutOfContext(true); //d1.setDesignAsOutOfContext();
		d1.setAutoIOBuffers(false);//d1.disableAutoIOBuffers();
		d1.getNetlist().exportEDIF("result.edif");
		 //HandPlacer.openDesign(d1);
		d1.writeCheckpoint("result.dcp");
	}// end of the function generateLinkBlaze()

	/**
	 * Function used to add a node (PCI master, master, or slave) to the design
	 * 
	 * @param d1
	 * @param nodeName
	 *            name that should be given to the cell created in the design
	 * @param nodeType
	 *            type of node (PCI master, master, or slave)
	 */
	public static void addNodeToDesign(Design d1, String nodeName,
			NodeType nodeType) {
		//System.out.println(">>nodeName:"+nodeName);
		// We add the PCI master to the design
		if (nodeType == NodeType.PCIMASTER) {
			String name = new String(nodeName);
			Module pciMaster = createModule(DCP_FOLDER_PATH + PCI_MASTER_DCP);
			ModuleInst pciMi = d1.createModuleInst(name, pciMaster);
			EDIFCell top = d1.getNetlist().getTopCell();
			pciMi.getModule().getNetlist().getTopCell()
					.createCellInst(pciMi.getName(), top);
			isNodeAdded.put(name, 1);
			
		} else
		// We add a regular master to the design
		if (nodeType == NodeType.MASTER) {
			String name = new String(nodeName);
			Module master = createModule(DCP_FOLDER_PATH + REGULAR_MASTER_DCP);
			ModuleInst mi = d1.createModuleInst(name, master);
			EDIFCell top = d1.getNetlist().getTopCell();
			mi.getModule().getNetlist().getTopCell()
					.createCellInst(mi.getName(), top);
			isNodeAdded.put(name, 1);
		} else
		// We add a slave to the design
		if (nodeType == NodeType.SLAVE) {
			String name = new String(nodeName);
			Module slave = createModule(DCP_FOLDER_PATH + SLAVE_DCP);
			ModuleInst mi = d1.createModuleInst(name, slave);
			EDIFCell top = d1.getNetlist().getTopCell();
			mi.getModule().getNetlist().getTopCell()
					.createCellInst(mi.getName(), top);
			isNodeAdded.put(name, 1);
		} else
		// We add an slr crossing node to the design to the design
		if (nodeType == NodeType.SLRCROSS) {
			String name = new String(nodeName);
			Module slrcross = createModule(DCP_FOLDER_PATH + SLR_CROSS_DCP);
			ModuleInst mi = d1.createModuleInst(name, slrcross);
			EDIFCell top = d1.getNetlist().getTopCell();
			mi.getModule().getNetlist().getTopCell()
					.createCellInst(mi.getName(), top);
			isNodeAdded.put(name, 1);
		}

	}

	/**
	 * Function for slr crossing between 2 nodes in different SLRs
	 * 
	 * @param d1
	 *            Design object that will be modified
	 * @param topSlrNode
	 *            node on top slr
	 * @param bottomSlrNode
	 *            node on the bottom slr
	 */
	public static void slrCrossing(Design d1, String topSlrNode,
			String bottomSlrNode) {
		EDIFCell topCell = d1.getTopEDIFCell();
		EDIFCellInst topSlrCell = topCell.getCellInst(topSlrNode);
		EDIFCellInst bottomSlrCell = topCell.getCellInst(bottomSlrNode);
		EDIFCellInst slrCrossingCell;
		int count = 1;

		if (topSlrNode.equals(LinkBlazeResources.NODE_ARRAY[5]))//crossing from SLR1 to SLR0 on the XCVU9p-flgb2104-2-i
			slrCrossingCell = topCell.getCellInst(LinkBlazeResources.NODE_ARRAY[3]);
		else
			slrCrossingCell = topCell.getCellInst(LinkBlazeResources.NODE_ARRAY[8]);

		for (int i = 0; i < LinkBlazeResources.SLR_CROSS_NODE_PORTS.length; i++) {
			// Connection with the node on top of the slr crossing node
			if (count <= LinkBlazeResources.PORT_COUNT_PER_INTERFACE) {
				if (slrCrossingCell.getPort(
						LinkBlazeResources.SLR_CROSS_NODE_PORTS[i]).getWidth() == 1) // "valid"
																						// port
				{
					String netName = topSlrCell.getName() + "_"
							+ slrCrossingCell.getName() + "_"
							+ LinkBlazeResources.SLR_CROSS_NODE_PORTS[i];
					EDIFNet net = topCell.createNet(netName);
					
					net.createPortInst(
							LinkBlazeResources.PORT_FROM_CROSSING_NODES[i],
							topSlrCell);
					net.createPortInst(
							LinkBlazeResources.SLR_CROSS_NODE_PORTS[i],
							slrCrossingCell);
				} else {
					for (int j = 0; j < slrCrossingCell.getPort(
							LinkBlazeResources.SLR_CROSS_NODE_PORTS[i])
							.getWidth(); j++) {
						String netName = topSlrCell.getName() + "_"
								+ slrCrossingCell.getName() + "_"
								+ LinkBlazeResources.SLR_CROSS_NODE_PORTS[i]
								+ j;
						EDIFNet net = topCell.createNet(netName);
						net.createPortInst(
								LinkBlazeResources.PORT_FROM_CROSSING_NODES[i],
								j, topSlrCell);
						net.createPortInst(
								LinkBlazeResources.SLR_CROSS_NODE_PORTS[i], j,
								slrCrossingCell);
					}
				}

			} else// Connection with the node at the bottom of the slr crossing
					// node
			{

				if (slrCrossingCell.getPort(
						LinkBlazeResources.SLR_CROSS_NODE_PORTS[i]).getWidth() == 1) // "valid"
																						// port
				{
					String netName = bottomSlrCell.getName() + "_"
							+ slrCrossingCell.getName() + "_"
							+ LinkBlazeResources.SLR_CROSS_NODE_PORTS[i];
					EDIFNet net = topCell.createNet(netName);
					net.createPortInst(
							LinkBlazeResources.PORT_FROM_CROSSING_NODES[i],
							bottomSlrCell);
					net.createPortInst(
							LinkBlazeResources.SLR_CROSS_NODE_PORTS[i],
							slrCrossingCell);
				} else {
					for (int j = 0; j < slrCrossingCell.getPort(
							LinkBlazeResources.SLR_CROSS_NODE_PORTS[i])
							.getWidth(); j++) {
						// System.out.println("##### We handle node5->node3-- the rest");
						String netName = bottomSlrCell.getName() + "_"
								+ slrCrossingCell.getName() + "_"
								+ LinkBlazeResources.SLR_CROSS_NODE_PORTS[i]
								+ j;
						EDIFNet net = topCell.createNet(netName);
						net.createPortInst(
								LinkBlazeResources.PORT_FROM_CROSSING_NODES[i],
								j, bottomSlrCell);
						net.createPortInst(
								LinkBlazeResources.SLR_CROSS_NODE_PORTS[i], j,
								slrCrossingCell);
					}
				}
			}
			count++;
		}
	}// end of function

	public static void addMasterNode(Design d1, String nodeName) {
		Module master = createModule("/home/jmandebi/node_stitching/master_routed.dcp");
		// Module master =
		// createModule("/home/jmandebi/node_stitching/archive/master_routed_nott_contained.dcp");

		// ModuleInstance mi = d1.createModuleInstance(nodeName,
		// nodeTypeMap.get("master"));
		ModuleInst mi = d1.createModuleInst(nodeName, master);
		EDIFCell top = d1.getNetlist().getTopCell();
		mi.getModule().getNetlist().getTopCell()
				.createCellInst(mi.getName(), top);
		isNodeAdded.put(nodeName, 1);// to mark that the node has been added to
										// the design
	}

	/**
	 * Function that connects two node already created in the design. The
	 * connection direction is : firstNode <--> secondNode, with
	 * "firstNode on top and "secondNode" at the bottom
	 * 
	 * @param d1
	 *            Design object that will be modified
	 * @param firstNode
	 *            node that is on top in LinkBlaze interconnect
	 * @param secondNode
	 *            node that is below in the LinkBlaze interconnect
	 */
	public static void connectNode(Design d1, String firstNode,
			String secondNode) {
		EDIFCell topCell = d1.getTopEDIFCell();
		EDIFCellInst firstCell = topCell.getCellInst(firstNode);
		EDIFCellInst secondCell = topCell.getCellInst(secondNode);

		for (int i = 0; i < LinkBlazeResources.NODE_ON_TOP.length; i++) {

			if (firstCell.getPort(LinkBlazeResources.NODE_ON_TOP[i]).getWidth() == 1) {
				String netName = firstCell.getName() + "_"
						+ secondCell.getName() + "_"
						+ LinkBlazeResources.NODE_ON_TOP[i];
				EDIFNet net = topCell.createNet(netName);
				net.createPortInst(LinkBlazeResources.NODE_ON_TOP[i], firstCell);
				net.createPortInst(LinkBlazeResources.NODE_AT_BOTTOM[i],
						secondCell);
			} else {
				for (int j = 0; j < firstCell.getPort(
						LinkBlazeResources.NODE_ON_TOP[i]).getWidth(); j++) {
					String netName = firstCell.getName() + "_"
							+ secondCell.getName() + "_"
							+ LinkBlazeResources.NODE_ON_TOP[i] + j;
					EDIFNet net = topCell.createNet(netName);
					net.createPortInst(LinkBlazeResources.NODE_ON_TOP[i], j,
							firstCell);
					net.createPortInst(LinkBlazeResources.NODE_AT_BOTTOM[i], j,
							secondCell);
				}
			}
		}
	}

	/**
	 * Function that connect all the remaining ports to the top module as either inputs or outputs
	 * (AXI ports, reset ports, and clock ports)
	 * 
	 * @param d1
	 *            Design object that will be modified
	 */
	public static void connectRemainingPorts(Design d1) {
		EDIFCell topCell = d1.getTopEDIFCell();
		EDIFNetlist n = d1.getNetlist();
		String routerClock = "clk_rtr";
		String clientClock = "clk_client";
		String routerReset = "rstn_rtr";
		String clientReset = "rstn_client";
		String globalReset="rstn_global";
		String pllRSTNetName = "pll_RST_net";
		String bufgceInstName ="clk_rtr_buffer";
		String resetLUTName="reset_lut";
		String plle4advInstName ="pll";
		String clkName = "BUFGCE_out_net";
		String clkInName ="BUFGCE_in_net";
		String pllLockedPortName = "locked";
		String pllLockedPortNetName = "locked";
		EDIFPort port=null;
		boolean connectPort = false;
		EDIFPort globalResetPort = topCell.createPort(globalReset, EDIFDirection.INPUT, 1);
		EDIFNet globalResetNet = topCell.createNet(globalReset);
		EDIFNet clkInNet = topCell.createNet(clkInName);
		//EDIFPort clientClkPort = topCell.createPort(clientClock, EDIFDirection.INPUT, 1);
		//EDIFNet clientClkNet = topCell.createNet(clientClock);		
		EDIFNet groundNet = EDIFTools.getStaticNet(NetType.GND, topCell,d1.getNetlist(), "GND");
		EDIFNet pllRSTNet = topCell.createNet(pllRSTNetName);
		
		 // Create the reset LUT with O=!I0
		 EDIFCellInst resetLUT = Design.createUnisimInst(topCell, resetLUTName, Unisim.LUT1);
		 Cell lutCell = new Cell(resetLUT.getName(), resetLUT);
		 LUTTools.configureLUT(lutCell, "O=!I0");
		 globalResetNet.createPortInst("I0", resetLUT);
		 EDIFPortInst globalResetPortInst = new EDIFPortInst(globalResetPort, globalResetNet);
		 
		 //connect the output of the reset LUT to the net that will sink on RST of the PLL
		 pllRSTNet.createPortInst("O", resetLUT);
		
		// Create BUFGCE in netlist and connect it
		EDIFCellInst bufgce = Design.createUnisimInst(topCell, bufgceInstName, Unisim.BUFGCE);
		clkInNet.createPortInst("I", bufgce);
		EDIFNet clkNet = topCell.createNet(clkName);
		clkNet.createPortInst("O", bufgce);
		
		/*We create a port to drive the "clk_rtr" net out of the whole module 
		(it will be used as an input for  the "rst_tree")*/
		EDIFPort outRtRClock = topCell.createPort(routerClock, EDIFDirection.OUTPUT, 1);
		 new EDIFPortInst(outRtRClock, clkNet);
		 
		EDIFNet vccNet = EDIFTools.getStaticNet(NetType.VCC, topCell, n);
		vccNet.createPortInst("CE", bufgce);
		
		// Create PLL in netlist and connect it
		//EDIFCellInst pll = Design.createUnisimInst(topCell, plle4advInstName, Unisim.PLLE4_ADV);
		Cell pllCell = d1.createAndPlaceCell("pll", Unisim.PLLE4_ADV, "PLL_X0Y12/PLL");
		EDIFCellInst pll =pllCell.getEDIFCellInst();
		pllCell.getSiteInst().addSitePIP("PWRDWNINV", "PWRDWN_PREINV", "PWRDWN");
		pllCell.getSiteInst().addSitePIP("CLKFBININV", "CLKFBIN_PREINV", "CLKFBIN");
		pllCell.getSiteInst().addSitePIP("RSTINV", "RST_PREINV", "RST");
		pllCell.getSiteInst().addSitePIP("CLKININV", "CLKIN_PREINV", "CLKIN");
		
		clkInNet.createPortInst("CLKOUT0", pll);
		//clientClkNet.createPortInst("CLKIN", pll);
		/*groundNet.createPortInst("CLKFBIN", pll);
		groundNet.createPortInst("CLKOUTPHYEN", pll);
		for (int i = 0; i <  pll.getPort("DADDR").getWidth(); i++)
			groundNet.createPortInst("DADDR",i, pll);
		
		groundNet.createPortInst("DCLK", pll);
		groundNet.createPortInst("DEN", pll);
		for (int i = 0; i <  pll.getPort("DI").getWidth(); i++)
			groundNet.createPortInst("DI",i, pll);
		
		groundNet.createPortInst("DWE", pll);
		groundNet.createPortInst("PWRDWN", pll);*/
		//connect RST from the pll to the net coming from the result LUT
		pllRSTNet.createPortInst("RST", pll);
		
		/*We create a port to drive the "LOCKED" port of the pll out of the whole module 
		(it will be used as an input for  the "rst_tree")*/
		EDIFPort pllLOCKEDPort = topCell.createPort(pllLockedPortName, EDIFDirection.OUTPUT, 1); 
		EDIFNet pllLockedPortNet = topCell.createNet(pllLockedPortNetName);
		pllLockedPortNet.createPortInst("LOCKED", pll);
		new EDIFPortInst(pllLOCKEDPort, pllLockedPortNet);
		
		for (EDIFCellInst cell : topCell.getCellInsts()) {
			// System.out.println("  ### CellName = "+cell.getName());
		
			if ((!cell.getName().contains("VCC")) && (!cell.getName().contains("GND"))) {
				if (cell.getPort(routerClock) != null) 
					clkNet.createPortInst(routerClock,cell);

				// some nodes might not have the client clock like in the SLR crossing
				if (cell.getPort(clientClock) != null) {
					EDIFPort clientClockPort = topCell.createPort(cell.getName()+"_"+clientClock, EDIFDirection.INPUT, 1);
					EDIFNet  clientClockNet = topCell.createNet(cell.getName()+"_"+clientClock);
					
					clientClockNet.createPortInst(clientClock,cell);
					EDIFPortInst portInst = new EDIFPortInst(clientClockPort, clientClockNet);
					
					//connecting the clk_pci to the entry point of the pll
					if (cell.getName().equals(LinkBlazeResources.NODE_ARRAY[2]))
						 clientClockNet.createPortInst("CLKIN", pll);	
					
					/*clientClkNet.createPortInst(clientClkPort,cell);
					EDIFPortInst portInst = new EDIFPortInst(clientClkPort, clientClkNet);*/
				}
				if (cell.getPort(routerReset) != null) {
					
					EDIFPort ResetPort = topCell.createPort(cell.getName()+"_"+routerReset, EDIFDirection.INPUT, 1);
					EDIFNet  ResetNet = topCell.createNet(cell.getName()+"_"+routerReset);
					
					ResetNet.createPortInst(routerReset,cell);
					EDIFPortInst portInst = new EDIFPortInst(ResetPort, ResetNet);	
				}
				if (cell.getPort(clientReset) != null) {
					
					EDIFPort ResetPort = topCell.createPort(cell.getName()+"_"+clientReset, EDIFDirection.INPUT, 1);
					EDIFNet  ResetNet = topCell.createNet(cell.getName()+"_"+clientReset);
					
					ResetNet.createPortInst(clientReset,cell);
					
					EDIFPortInst portInst = new EDIFPortInst(ResetPort, ResetNet);	
				}
				
				
				for (Map.Entry<String, EDIFPort> entry : cell.getCellType()
						.getPortMap().entrySet()) {//we look at the ports of the node
					
					//System.out.println("--->> "+cell.getName()+" entry.getValue().getName()="+entry.getValue().getName());
					 if (entry.getValue().getName().contains("AXI") ){
						 connectPort = true;
							if ((entry.getValue().getDirection() == EDIFDirection.INPUT)) {//for input ports
								if (entry.getValue().getName().contains("AXI"))
								   port = topCell.createPort(LinkBlazeResources.NODE_TYPE[getNodeIndex(cell.getName())]+entry.getValue().getName().subSequence(2, entry.getValue().getName().length()), EDIFDirection.INPUT, entry.getValue().getWidth());
								else{
								   port = topCell.createPort(cell.getName()+"_"+entry.getValue().getName(), EDIFDirection.INPUT, entry.getValue().getWidth());	
									
								}
							 }
							else{//for output ports
								if (entry.getValue().getName().contains("AXI")){
								  port = topCell.createPort(LinkBlazeResources.NODE_TYPE[getNodeIndex(cell.getName())]+entry.getValue().getName().subSequence(2, entry.getValue().getName().length()), EDIFDirection.OUTPUT, entry.getValue().getWidth());
								 
								  /******* SIMPLE HACK JUST TO THROUGH CLOSING THE FLOW IN THE DSA DEPLOYMENT(remove the "if" section when done with closing the flow)     ******/
								  /*if (entry.getValue().getWidth() == 1){
									  EDIFPortInst portInst = new EDIFPortInst(port, groundNet);  
								  }
								  else{
									  for (int i = 0; i < entry.getValue().getWidth(); i++){
										  EDIFPortInst portInst = new EDIFPortInst(port, groundNet,i);
									  }
								  }*/
								  
								 }
								else
									port = topCell.createPort(cell.getName()+"_"+entry.getValue().getName(), EDIFDirection.OUTPUT, entry.getValue().getWidth());		
							 }	
							
							
							
						
						} 
							/******* SIMPLE HACK JUST TO THROUGH CLOSING THE FLOW IN THE DSA DEPLOYMENT
							 * (remove the "if" section when done with closing the flow and handle "dest" ports properly) ******/	
						
							//for now we ground input "dest" ports for each node || 	
							if (entry.getKey().equals("dest")) {
								connectPort = true;
								if ((entry.getValue().getDirection() == EDIFDirection.INPUT)) {//for input ports
									   port = topCell.createPort(cell.getName()+"_"+entry.getValue().getName(), EDIFDirection.INPUT, entry.getValue().getWidth());		
									}
								 
								/*if (entry.getValue().getWidth() == 1)
									groundNet.createPortInst(entry.getKey(),
											cell);
								else {
									for (int i = 0; i < entry.getValue()
											.getWidth(); i++)
										groundNet.createPortInst(entry.getKey(),
												i, cell);
								}*/
							}	
							
						// We attach the "up_a" port of the node on top of the
						// interconnect to the ground since it isn't used
						if (cell.getName().equals(topNode)) {
							if (entry.getValue().getName().contains("up_a")) {
								connectPort = true;
								if ((entry.getValue().getDirection() == EDIFDirection.INPUT)) {//for input ports
									   port = topCell.createPort(cell.getName()+"_"+entry.getValue().getName(), EDIFDirection.INPUT, entry.getValue().getWidth());		
									}
								/*if (entry.getValue().getWidth() == 1)
									groundNet.createPortInst(entry.getKey(),
											cell);
								else {
									for (int i = 0; i < entry.getValue()
											.getWidth(); i++)
										groundNet.createPortInst(entry.getKey(),
												i, cell);
								}*/
							}
						} else
						// We attach the "dn_b" port of the node on bottom of
						// the interconnect to the ground since it isn't used
						if (cell.getName().equals(bottomNode)) {
							if (entry.getValue().getName().contains("dn_b")) {
								connectPort = true;
								if ((entry.getValue().getDirection() == EDIFDirection.INPUT)) {//for input ports
									   port = topCell.createPort(cell.getName()+"_"+entry.getValue().getName(), EDIFDirection.INPUT, entry.getValue().getWidth());		
									}
								/*if (entry.getValue().getWidth() == 1)
									groundNet.createPortInst(entry.getKey(),
											cell);
								else {
									for (int i = 0; i < entry.getValue()
											.getWidth(); i++)
										groundNet.createPortInst(entry.getKey(),
												i, cell);
								}*/
							}
						}
				
						if(connectPort){
							//Putting "null" just for initialization purposes
							EDIFNet logicalNet = null;
							EDIFPortInst portInst =null;
							if (entry.getValue().getWidth() == 1){	
								logicalNet = topCell.createNet(port.getName());	
								logicalNet.createPortInst(entry.getKey(), cell);
								portInst = new EDIFPortInst(port, logicalNet);
							    }
							else {
								
								for (int i = 0; i < entry.getValue().getWidth(); i++){
									logicalNet = topCell.createNet(port.getName()+i);	
									logicalNet.createPortInst(entry.getKey(),i, cell);
									portInst = new EDIFPortInst(port, logicalNet,i);
								}
						    }
	
						     //finally create the physical net out of the logical net
							 d1.createNet(logicalNet);
							 //System.out.println("->> Added the net :"+logicalNet.getName());
						}
                     connectPort = false;
				}
			}
		}
	}

	public static void updateLUTEquation(Design d1) {
		EDIFCell topCell = d1.getTopEDIFCell();
		
		for (EDIFCellInst cell : topCell.getCellInsts()) {
			rapidWrightTokenTransferDnCounter = 0;
			rapidWrightTokenTransferUpCounter = 0;
			rapidWrightTokenToRtr = 0;
			getRapidWrightLUTs(cell.getCellType().getCellInsts(),cell.getName());
		}

	}

	/**
	 * Function that goes through all the cells within a node, looking for LUT
	 * with the name containing the "RapidWright" marker
	 * 
	 * @param collection
	 *            Collection of cells within each of the LinkBlaze nodes
	 * @param nodeName
	 *            name of the node (node1, node2,... in the context of
	 *            LinkBlaze)
	 * @return Returns a String at the moment not really used (maybe it won't
	 *         just return anything in the final version)
	 */
	public static String getRapidWrightLUTs(Collection<EDIFCellInst> collection, String nodeName) {
		String equation;
		int LUTCounter;

		for (EDIFCellInst cellInstance : collection) {
			if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_LUT_KEY_WORD)) {
				Cell cell = new Cell(cellInstance.getName(), cellInstance);

				if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_EJECT_DN)
						|| cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_EJECT_UP)) {
					equation = generateEjectUpDownLUTEquation(cellInstance,LUTTools.getLUTSize(cell), nodeName);
					LUTTools.configureLUT(cell, equation);
				}

				if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_INJECT_DIR)) {
					equation = generateInjectDirLUTEquation(cellInstance,LUTTools.getLUTSize(cell), nodeName);
					LUTTools.configureLUT(cell, equation);
				}

				if (cellInstance.getCellType().getName().contains(LinkBlazeResources.DUMMY_KEY_WORD)) {
					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_SRC_ADDR)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						 LUTCounter = 0;

						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								System.out.println("dummy.getName()="+dummy.getName()+" -- nodeName="+nodeName);
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generateSrcAddrLUTEquation(nodeName,LUTCounter);
								LUTTools.configureLUT(dummyCell, equation);
								LUTCounter++;
						    }
						}
					}

					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_PCI_TOKENS_OUT)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						LUTCounter = 0;

						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generatePCITokensOutEquation(LUTCounter);
								LUTTools.configureLUT(dummyCell, equation);
								LUTCounter++;
							}
						}

					}
					
					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_TOKEN_TRANSFER_DN)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						
						//System.out.println("\n>>> node name ="+nodeName+" dummyLUT.size()="+dummyLUT.size()+" -- cellInstance.getName()="+cellInstance.getName());
						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generateTokenTransferDnLUTEquation(nodeName,rapidWrightTokenTransferDnCounter);
								LUTTools.configureLUT(dummyCell, equation);
								rapidWrightTokenTransferDnCounter++;
							}
						}		
					}
					
					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_TOKEN_TRANSFER_UP)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						
						//System.out.println("\n>>> node name ="+nodeName+" dummyLUT.size()="+dummyLUT.size()+" -- cellInstance.getName()="+cellInstance.getName());
						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generateTokenTransferUpLUTEquation(nodeName,rapidWrightTokenTransferUpCounter);
								LUTTools.configureLUT(dummyCell, equation);
								rapidWrightTokenTransferUpCounter++;
							}
						}		
					}
					
					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_TOKENS_OUT)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						LUTCounter = 0;
						//System.out.println("\n>>> node name ="+nodeName+" dummyLUT.size()="+dummyLUT.size()+" -- cellInstance.getName()="+cellInstance.getName());
						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generateTokensOutLUTEquation(nodeName, LUTCounter, dummyCell);
								LUTTools.configureLUT(dummyCell, equation);
								LUTCounter++;
							}
						}
						
					}	
					
					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_TOKENS_IN)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						LUTCounter = 0;
						//System.out.println("\n>>> node name ="+nodeName+" dummyLUT.size()="+dummyLUT.size()+" -- cellInstance.getName()="+cellInstance.getName());
						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generateTokensInLUTEquation(nodeName,LUTCounter);
								LUTTools.configureLUT(dummyCell, equation);
								LUTCounter++;
							}
						}
						
					} 
					
					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_TOKEN_TO_RTR)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						LUTCounter = 0;
						//System.out.println("\n>>> node name ="+nodeName+" dummyLUT.size()="+dummyLUT.size()+" -- cellInstance.getName()="+cellInstance.getName());
						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generateTokensToRtrLUTEquation(nodeName,rapidWrightTokenToRtr);
								LUTTools.configureLUT(dummyCell, equation);
								rapidWrightTokenToRtr++;
							}
						}
						
					}
					
					if (cellInstance.getName().contains(LinkBlazeResources.RAPIDWRIGHT_ADDR_TO_IDX)) {
						Collection<EDIFCellInst> dummyLUT = cellInstance.getCellType().getCellInsts();
						LUTCounter = 0;
						
						for (EDIFCellInst dummy : dummyLUT) {
							if (!dummy.getName().contains("GND")){
								System.out.println(">>> node name ="+nodeName+" LUTCounter="+LUTCounter+" dummy.getName()="+dummy.getName());
								Cell dummyCell = new Cell(dummy.getName(), dummy);
								equation = generateAddToIdxLUTEquation(nodeName, LUTCounter,dummyCell);
								LUTTools.configureLUT(dummyCell, equation);
								LUTCounter++;
							}
						}			
					}
					
					
				}

			}
			if ((cellInstance.getCellType() != null)
					&& (cellInstance.getCellType().getCellInsts() != null)
					&& (!cellInstance.getCellType().getCellInsts().isEmpty()))
				getRapidWrightLUTs(cellInstance.getCellType().getCellInsts(), nodeName);
		}

		return null;
	}
	
	public static String generateAddToIdxLUTEquation(String nodeName, int LUTCounter, Cell dummyCell){
		String equation = "";
		int lutSize = LUTTools.getLUTSize(dummyCell);
		TruthTableStructure table = new TruthTableStructure(lutSize);
		int [] truthTableResult = new int[(int)Math.pow(2, lutSize)];
		int i,destAddr;
		int[] binaryRepresentation;
		String [] labels = new String[lutSize];
		
		//labels that will be used as LUT equation variables
		for (i = 0; i < labels.length; i++)
			labels[i] = "I"+i;
		
		//we will generate the truth table for each individual LUT and extract its equation
		table.truthTableGenerator();
		
		/*now we need to generate the result column of the current LUT truth table
		  based on destination indexes (see LinkBlazeResources.DEST_ADDRS)
		  */
		for(i = 0; i < truthTableResult.length; i++)//initialization
			truthTableResult[i] = 0;
		
		/*for each destination address of the current node, we only consider the LUTCounter_th bit 
		 of the binary representation of the destination address
		  
		*/
		for(i = 0; i < LinkBlazeResources.DEST_ADDRS[getNodeIndex(nodeName)].length; i++)
		{
			destAddr = LinkBlazeResources.DEST_ADDRS[getNodeIndex(nodeName)][i];
			binaryRepresentation = decimalToBinaryConverter(i, lutSize);
			truthTableResult[destAddr] = binaryRepresentation[LUTCounter];
			//System.out.println(">>> destAddr = "+destAddr+" -- i= "+i+" -- node name ="+nodeName+" -- LUTCounter="+LUTCounter);
		}
		
		table.addResultsToTruthTable(truthTableResult);
		//table.displayTruthTable();
		equation+=table.generateBooleanEquation(labels);
		//System.out.println("equation = "+equation);
		
		return equation;
	}		
	
	public static String generateTokensToRtrLUTEquation(String nodeName, int LUTCounter){
		String equation = "O=";	
		
		if(LinkBlazeResources.CONFIG_TOKEN_UP[getNodeIndex(nodeName)][LUTCounter] == 1 )
		    equation+=LinkBlazeResources.I1_INPUT;
		else
		  if(LinkBlazeResources.CONFIG_TOKEN_DN[getNodeIndex(nodeName)][LUTCounter] == 1 )	
			  equation+=LinkBlazeResources.I0_INPUT;
		  else
			  equation+="0";
		
		/*System.out.println("nodeName= "+nodeName+" -- LUTCounter= "+LUTCounter+" -- equation: "+equation+" ## CONFIG_TOKEN_UP="
				+LinkBlazeResources.CONFIG_TOKEN_UP[getNodeIndex(nodeName)][LUTCounter]+ " ## CONFIG_TOKEN_DN="
				+LinkBlazeResources.CONFIG_TOKEN_DN[getNodeIndex(nodeName)][LUTCounter]
				);*/
		
		return equation;
	}
	
	
	
	public static String generateTokensInLUTEquation(String nodeName, int LUTCounter){
		String equation = "O=";	
		boolean found = false;
		
		/*
		 * if TOKEN_CH[i] == LUTCounter, then O=Ii otherwise O=0
		 * 
		 * */
		for(int i=0; i < LinkBlazeResources.TOKEN_CH[getNodeIndex(nodeName)].length ; i++  ){
			if( LinkBlazeResources.TOKEN_CH[getNodeIndex(nodeName)][i] == LUTCounter )	
			{
				equation+="I"+i;
				found = true;
				break;
			}	
			
		}
		if(found == false)
		    equation+="0";
		
		return equation;
	}
	
	
	
	public static String generateTokensOutLUTEquation(String nodeName, int LUTCounter, Cell dummyCell){
		String equation = "O=";	
		int inputNumber = LUTTools.getLUTSize(dummyCell);
		boolean found = false;
		
		/*for each of the 1 (master),2(slave), or 4 (PCI master) inputs 
		 * if TOKEN_CH[i] == LUTCounter, then O=Ii otherwise O=0
		 * 
		 * */
		for(int i=0; i < inputNumber; i++) 
		{
			if( LinkBlazeResources.TOKEN_CH[getNodeIndex(nodeName)][i] == LUTCounter )	
			{
				equation+="I"+i;
				found = true;
				break;
			}	
			
		}
		if(found == false)
		    equation+="0";
		
		return equation;
	}
	
	public static String generateTokenTransferUpLUTEquation(String nodeName, int LUTCounter){
		String equation = "O=";
		
		if( LinkBlazeResources.CONFIG_TOKEN_UP[getNodeIndex(nodeName)][LUTCounter] == 1 )
			equation+=LinkBlazeResources.I1_INPUT;
		else
		  if( LinkBlazeResources.CONFIG_TOKEN_DN[getNodeIndex(nodeName)][LUTCounter] == 1 )	
			  equation+="0";
		  else
			  equation+=LinkBlazeResources.I0_INPUT; 
		return equation;
	}
	
	
	/**
	 * Function that generates the RapidWright_token_transfer_dn LUT equation
	 * @param nodeName name of the node
	 * @param LUTCounter identifier of the LUT (in the current SDAccel LinkBlaze, there are 4 of them since there are 4 channels)
	 * @return String representing the LUT equation
	 */
	public static String generateTokenTransferDnLUTEquation(String nodeName, int LUTCounter){
		String equation = "O=";
		
		if( LinkBlazeResources.CONFIG_TOKEN_DN[getNodeIndex(nodeName)][LUTCounter] == 1 )
			equation+=LinkBlazeResources.I1_INPUT;
		else
		  if( LinkBlazeResources.CONFIG_TOKEN_UP[getNodeIndex(nodeName)][LUTCounter] == 1 )	
			  equation+="0";
		  else
			  equation+=LinkBlazeResources.I0_INPUT; 
		return equation;
	}
	
	

	public static String generateSrcAddrLUTEquation(String nodeName,
			int LUTCounter) {
		String equation = "O=";

		equation += LinkBlazeResources.NODE_LOCAL_ADDRESS[getNodeIndex(nodeName)][LUTCounter];
		// System.out.println("  >>> generateSrcAddrLUTEquation<<< ");
		// System.out.println("    >> NAME="+cellInstance.getName()+" --- TYPE="+cellInstance.getCellType().getName()+" --- Equation:"+equation);
		return equation;
	}

	/**
	 * Function that generate the equation for the RapidWright_PCI_tokens_out
	 * LUT. In the current SDAccel deployment, there are 4 of them because the
	 * PCI Master node has 4 possible destination addresses corresponding to the
	 * 4 slaves it can communicate with.
	 * 
	 * @param LUTCounter
	 *            identifier of one of the 4 possible LUTs. Depending of the LUT
	 *            identified, LinkBlazeResources.PCI_DEST_ADDRS[][] is used to
	 *            generate the appropriate LUT equation.
	 * @return The equation of the corresponding LUT.
	 */
	public static String generatePCITokensOutEquation(int LUTCounter) {
		String equation = "O=";

		for (int i = 0; i < (LinkBlazeResources.PCI_DEST_ADDRS[LUTCounter].length); i++) {
			if (i > 0)
				equation += " &";

			if (LinkBlazeResources.PCI_DEST_ADDRS[LUTCounter][i] == 1)
				equation += " I" + i;
			else
				equation += " !I" + i;
		}

		return equation;
	}

	/**
	 * Function used to generate the LUT equation for "RapidWright_eject_up" and
	 * "RapidWright_eject_dn" LUTs for a specific node
	 * 
	 * @param cellInstance
	 *            LUT For which we will generate a new equation based on its
	 *            location in the LinkBlaze topology
	 * @param LUTSize
	 *            Number of inputs of the LUT
	 * @param nodeName
	 *            name of the node in which the LUT is found
	 * @return Returns the new LUT equation as a String
	 */
	public static String generateEjectUpDownLUTEquation(
			EDIFCellInst cellInstance, int LUTSize, String nodeName) {
		String equation = "O=";
		int inputCounter = 0;

		for (Map.Entry<String, EDIFPortInst> entry : cellInstance
				.getPortInstMap().entrySet()) {
			// System.out.println(" key = "+entry.getKey() + " <<-->> value = "
			// +
			// entry.getValue().getName()+" <<-->> Net name="+entry.getValue().getNet().getName());

			// Dealing with the "valid" signal of the LUT
			if (entry.getValue().getNet().getName().contains("valid")) {
				if (inputCounter == 0)// if it's the first element in the
										// equation, we don't put the "&" in
										// front
					equation += entry.getKey();
				else
					equation += " & " + entry.getKey();

				inputCounter++;
			}

			// Dealing with the remaining input signals of the LUT
			for (int i = 0; i < (LUTSize - 1); i++) {
				if (entry.getValue().getNet().getName().contains("[" + i + "]")) {
					if (inputCounter == 0) {// if it's the first element in the
											// equation, we don't put the "&" in
											// front
						if (LinkBlazeResources.NODE_LOCAL_ADDRESS[getNodeIndex(nodeName)][i] == 1)
							equation += entry.getValue().getName();
						else
							equation += "!" + entry.getValue().getName();
					} else {
						if (LinkBlazeResources.NODE_LOCAL_ADDRESS[getNodeIndex(nodeName)][i] == 1)
							equation += " & " + entry.getValue().getName();
						else
							equation += " & !" + entry.getValue().getName();
					}

					inputCounter++;
					break;
				}
			}
		}
		return equation;
	}

	public static String generateInjectDirLUTEquation(
			EDIFCellInst cellInstance, int LUTSize, String nodeName) {
		String equation = "";
		TruthTableStructure table = new TruthTableStructure(LUTSize);

		table.truthTableGenerator();
		table.addResultsToTruthTable(LinkBlazeResources.injectDirTruthTable[getNodeIndex(nodeName)]);
		equation = table
				.generateBooleanEquation(LinkBlazeResources.injectDirLUTInputName[getNodeIndex(nodeName)]);
		// System.out.println("##### LUT EQUATION = "+equation+"\n");
		return equation;
	}

	/**
	 * Function used to get the index of a node in the table
	 * "LinkBlazeResources.nodeArray" based on its name
	 * 
	 * @param nodeName
	 *            name of the node
	 * @return index of the node
	 */
	public static int getNodeIndex(String nodeName) {
		int i;

		for (i = 0; i < (LinkBlazeResources.NODE_ARRAY.length); i++)
			if (LinkBlazeResources.NODE_ARRAY[i].equals(nodeName))
				break;
		return i;
	}
	
	/**
	 * Function that converts a decimal value to its binary representation over "numberOfBits" bits
	 * @return returns an array of integers containing the binary representation
	 */
	 
	public static int [] decimalToBinaryConverter(int decimal, int numberOfBits) {
		int [] result = new int[numberOfBits];
		int temp = decimal;
		for(int i = 0; i < numberOfBits; i++){
			if(temp != 0){
				result[i] = temp % 2;
				temp = temp / 2;
			}
			else
				result[i] = 0;	
		}
		
		/*System.out.print(decimal+" = (");
		for(int i = (numberOfBits-1); i >= 0; i--)
			System.out.print(result[i]);
		System.out.println(")");*/
		
		return result;
	}
	
	

	/**
	 * Main function of the class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String linkBlazeTopology;

		if (args.length != 4)
			System.out
					.println("Enter 4 digits ('1' for DDR present, or '0' otherwise) separated by spaces as paramaters to generate a LinkBlaze topology");
		else {

			linkBlazeTopology = args[3] + args[1] + args[2] + args[0];
			System.out.println("\n Your LinkBlaze configuration is:\n");
			System.out
					.println("                                    DDR0 Present:"
							+ args[0] + "\n");
			System.out
					.println("                                    DDR1 Present:"
							+ args[1] + "\n");
			System.out
					.println("                                    DDR2 Present:"
							+ args[2] + "\n");
			System.out
					.println("                                    DDR3 Present:"
							+ args[3] + "\n");
			initializeLinkBlazeGenerator();
			generateLinkBlaze(linkBlazeTopology);
		}

	}
}// end of the class

/*
 * for (Map.Entry<String, EDIFPortRef> entry :
 * cellInstance.getPortRefMap().entrySet() ) {
 * System.out.println("key = "+entry.getKey() + " <<--->> value = " +
 * entry.getValue().getName()); //System.out.println("KEY= "+entry.getKey());
 * 
 * }
 */

/*
 * System.out.println("\n\n\n ########## master"); for
 * (Map.Entry<String,EDIFPort> entry : master_cell.getCellType().ports
 * .entrySet() ) { System.out.println("key = "+entry.getKey() +
 * " <<------>> value = " + entry.getValue().getName());
 * //System.out.println("KEY= "+entry.getKey());
 * 
 * }
 * 
 * System.out.println("\n\n\n ########## slave"); for
 * (Map.Entry<String,EDIFPort> entry : slave_cell.getCellType().ports
 * .entrySet() ) { System.out.println("key = "+entry.getKey() +
 * " <<------>> value = " + entry.getValue().getName());
 * //System.out.println("KEY= "+entry.getKey());
 * 
 * }
 */

/*
 * System.out.println("\n\n\n ########## Module Instance:"+mi.getName()); for
 * (Map.Entry<String,EDIFCell> entry : work.getCellMap().entrySet() ) {
 * System.out.println("key = "+entry.getKey() + " <<------>> value = " +
 * entry.getValue().getName()); //System.out.println("KEY= "+entry.getKey());
 * 
 * }
 */

/*
 * Reference example EDIFNet net = top_cell.createNet("KRNL3_AXI_arready");
 * net.createPortRef("KRNL3_AXI_arready", master_cell);
 * net.createPortRef("DDR3_AXI_arready", slave_cell);
 */

/*
 * System.out.println("\nmaster_port = "+master_cell.getCellType().getPort(
 * "KRNL3_AXI_arready").getName());
 * System.out.println("slave_port = "+slave_cell
 * .getCellType().getPort("DDR3_AXI_arready").getName());
 * 
 * System.out.println("\ntop_cell = "+top_cell.getName());
 * System.out.println("master_cell = "+master_cell.getName());
 * System.out.println("slave_cell = "+slave_cell.getName());
 */
