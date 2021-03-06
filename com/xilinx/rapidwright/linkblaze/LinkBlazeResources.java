/*
 * 
 * Copyright (c) 2018 Xilinx, Inc. 
 * All rights reserved.
 *
 * Author: Joel Mandebi, Research intern at Xilinx Research Labs.
 *
 * This file is part of RapidWright. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */


package com.xilinx.rapidwright.linkblaze;


/**
 * Class containing resources needed to generate the LinkBlaze NoC
 * @author joel mandebi
 *
 */
public class LinkBlazeResources {
	
	public static final String RAPIDWRIGHT_LUT_KEY_WORD = "Rapid";
	public static final String DUMMY_KEY_WORD = "dummy";
	public static final String RAPIDWRIGHT_EJECT_DN = "RapidWright_eject_dn";
	public static final String RAPIDWRIGHT_EJECT_UP = "RapidWright_eject_up";
	public static final String RAPIDWRIGHT_INJECT_DIR = "RapidWright_inject_dir";
	public static final String RAPIDWRIGHT_SRC_ADDR = "RapidWright_src_addr";
	public static final String RAPIDWRIGHT_PCI_TOKENS_OUT = "RapidWright_PCI_tokens_out";
	public static final String RAPIDWRIGHT_ADDR_TO_IDX = "RapidWright_addr_to_idx";
	public static final String RAPIDWRIGHT_TOKEN_TRANSFER_DN ="RapidWright_token_transfer_dn";
	public static final String RAPIDWRIGHT_TOKEN_TRANSFER_UP ="RapidWright_token_transfer_up";
	public static final String RAPIDWRIGHT_TOKENS_OUT ="RapidWright_tokens_out";
	public static final String RAPIDWRIGHT_TOKENS_IN ="RapidWright_tokens_in";
	public static final String RAPIDWRIGHT_TOKEN_TO_RTR ="RapidWright_token_to_rtr";
	
	
	public static final String I0_INPUT = "I0";
	public static final String I1_INPUT = "I1";
	
	/**
	 * This attribute count the number of communication direction in each direction
	 * which is 8 (4 incoming + 4 outgoing)
	 * */
	public static final int PORT_COUNT_PER_INTERFACE = 8;
	
	/**
	 * List of node types supported in the current version of LinkBlaze
	 */
	enum NodeType
	{
	    MASTER, PCIMASTER,SLAVE,SLRCROSS;
	}
	
	/**
	 * List of names of the node in the current implementation of LinkBlaze deployed on the
	 * SDAccel platform
	 */
	public static final String[] NODE_ARRAY={"node0","node1","node2","node3","node4","node5"
		                                    ,"node6","node7","node8","node9","node10","node11"
		                                    ,"node12"
	                                        };
	/**
	 * Knowing each node's local address allows to automatically generate LUT equations dealing with the 
	 * LOCAL_ADDR parameter from the design
	 * 
	 * For instance check the function:
	 * public static String generateEjectUpDownLUTEquation (EDIFCellInstance cellInstance, int LUTSize, String nodeName)
	 * in the LinkBlazeGenerator class
	 * 
	 * Read each entry from the end to the beginning. e.g 5=(0101) in binary but is reversed when stored in the table
	 * */
	public static final int[][] NODE_LOCAL_ADDRESS={{1,0,1,0} //node0 -> 5
                                                 ,{0,0,0,0} //node1 -> 0
                                                 ,{0,0,1,0} //node2 ->4
                                                 ,{} //node3 -> -1
                                                 ,{} //node4 -> -1
                                                 ,{1,1,1,0} //node5 -> 7
                                                 ,{0,1,0,0} //node6 -> 2
                                                 ,{1,0,0,0} //node7 -> 1
                                                 ,{}//node8 -> -1
                                                 ,{}//node9 -> -1
                                                 ,{0,1,1,0} //node10 -> 6
                                                 ,{1,1,0,0} //node11 -> 3
                                                 ,{0,0,0,1} //node12 -> 8
	                                             };
   	
	
	
	
	/**
	 * This parameters allows to update the RapidWright_PCI_tokens_out LUT equation. 
	 * See the function: LinkBlazeGenerator.generatePCITokensOutEquation()
	 */
	public static final int [][] PCI_DEST_ADDRS = { 
												    {0,0,0,0} //0
		                                           ,{0,1,0,0} //2
		                                           ,{1,0,0,0} //1
		                                           ,{1,1,0,0} //3
												   };
	
	
	public static final int [][] DEST_ADDRS = {
		                                        {0} //node0
		                                       ,{5,4} //node1
		                                       ,{0,2,1,3} //node2 
		                                       ,{} //node3
		                                       ,{} //node4
		                                       ,{2} //node5
		                                       ,{4,7} //node6
		                                       ,{4,6} //node7
		                                       ,{} //node8
		                                       ,{} //node9
		                                       ,{1} //node10
		                                       ,{4,8} //node11
		                                       ,{3} //node12
	                                          };
	/* 
	 * These 2 next attributes allow to define the connectivity between ports 
	 * for slr crossing. This way, if port name changes, one just needs to do some update here  
	 */
	
	/**
	 * Port names of the SLR crossing node
	 */
	public static final String[] SLR_CROSS_NODE_PORTS={/*node performing the slr crossing*/
		"din_north[valid]","din_north[dest]","din_north[flit]","din_north[tokens]",
		//"up_a[valid]","up_a[dest]","up_a[flit]","up_a[tokens]",
		"dout_north[valid]","dout_north[dest]","dout_north[flit]","dout_north[tokens]",
		//"up_b[valid]","up_b[dest]","up_b[flit]","up_b[tokens]",
		"dout_south[valid]","dout_south[dest]","dout_south[flit]","dout_south[tokens]",
		"din_south[valid]","din_south[dest]","din_south[flit]","din_south[tokens]"
	 };
	
	/**
	 * Port names of the nodes at the top and bottom of the SLR crossing node.
	 * The first 8 entries represent the ports from the node at the top.
	 */
	public static final String[] PORT_FROM_CROSSING_NODES={
		"dn_a[valid]","dn_a[dest]","dn_a[flit]","dn_a[tokens]",
		"dn_b[valid]","dn_b[dest]","dn_b[flit]","dn_b[tokens]",
		"up_a[valid]","up_a[dest]","up_a[flit]","up_a[tokens]",
		"up_b[valid]","up_b[dest]","up_b[flit]","up_b[tokens]"
	 };
	

	/** 
	 * These 2 next attributes allow to define the connectivity between ports of 
	 * two adjacent nodes "nodeOnTop<->nodeAtBottom". 
	 * This way, if port name changes, one just needs to do some update here  
	 */
	public static final String[] NODE_ON_TOP={/*node on top in the LinkBlaze topology*/
		"dn_a[valid]","dn_a[dest]","dn_a[flit]","dn_a[tokens]",
		"dn_b[valid]","dn_b[dest]","dn_b[flit]","dn_b[tokens]",
	 };
	
	public static final String[] NODE_AT_BOTTOM={/*node at the bottom in the LinkBlaze topology*/
		"up_a[valid]","up_a[dest]","up_a[flit]","up_a[tokens]",
		"up_b[valid]","up_b[dest]","up_b[flit]","up_b[tokens]"
	 };
	
   /**
    * List of LUTs that need to be updated within nodes
    */
    	
   public static final String [] LUTToUpdate ={
	"RapidWright_eject_up","RapidWright_eject_dn","RapidWright_inject_direction","RapidWright_src_addr"
 
	   
   
   };	

  /**
   * Contains the result column of the truth table of each node for the "RapidWright_inject_direction" LUT 
   */
  public static int[][] injectDirTruthTable={
	  {1,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0}//for node0
	 ,{0,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0}//for node1
	 ,{0,1,1,1,0,0,1,1,1,0,0,0,0,0,0,0}//for node2
	 ,{}//for node3
	 ,{}//for node4
	 ,{0,1,0,1,0,0,1,0,1,0,0,0,0,0,0,0}//for node5
	 ,{0,1,0,1,0,0,1,0,1,0,0,0,0,0,0,0}//for node6
	 ,{0,0,0,1,0,0,1,0,1,0,0,0,0,0,0,0}//for node7
	 ,{}//for node8
	 ,{}//for node9
	 ,{0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0}//for node10
	 ,{0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0}//for node11
	 ,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}//for node12
	  
  }; 
	
  public static String[][] injectDirLUTInputName={
	  {"I1","I3","I2","I0"} //for node0 -- actual master input names {"in1[7]","in1[8]","in[9]","in[10]"}
	 ,{"I3","I0","I2","I1"}//for node1 -- actual slave input names {"D[0]","D[1]","D[2]","D[3]"}
	 ,{"I1","I0","I3","I2"}//for node2 -- actual input names {"D[7]","D[8]","D[9]","D[10]"}
	 ,{}//for node3
	 ,{}//for node4
	 ,{"I1","I3","I2","I0"}//for node5
	 ,{"I3","I0","I2","I1"}//for node6
	 ,{"I3","I0","I2","I1"}//for node7
	 ,{}//for node8
	 ,{}//for node9
	 ,{"I1","I3","I2","I0"}//for node10
	 ,{"I3","I0","I2","I1"}//for node11
	 ,{"I1","I3","I2","I0"}//for node12
	  
  };
  
  /**
   * CONFIG_TOKEN_UP parameter for each node
   */
  public static final int [][] CONFIG_TOKEN_UP={
	  {0,0,0,1}//for node0
	 ,{0,0,1,0}//for node1
	 ,{1,1,0,1}//for node2
	 ,{}//for node3
	 ,{}//for node4
	 ,{0,0,1,0}//for node5
	 ,{0,0,0,0}//for node6
	 ,{0,0,0,1}//for node7
	 ,{}//for node8
	 ,{}//for node9
	 ,{0,0,0,0}//for node10
	 ,{0,0,0,1}//for node11
	 ,{0,0,0,0}//for node12
	 
  };
  
  /**
   * CONFIG_TOKEN_DN parameter for each node
   */
  public static final int [][] CONFIG_TOKEN_DN={
	  {0,0,0,0}//for node0
	 ,{0,0,0,1}//for node1
	 ,{0,0,1,0}//for node2
	 ,{}//for node3
	 ,{}//for node4
	 ,{0,0,0,0}//for node5
	 ,{0,0,1,1}//for node6
	 ,{0,1,0,0}//for node7
	 ,{}//for node8
	 ,{}//for node9
	 ,{0,0,0,1}//for node10
	 ,{1,0,0,0}//for node11
	 ,{0,0,0,1}//for node12
  };
  
  /**
   * TOKEN_CH parameter for each node
   */
  public static final int [][] TOKEN_CH={
	  {0}//for node0
	 ,{0,1}//for node1
	 ,{1,0,2,3}//for node2
	 ,{}//for node3
	 ,{}//for node4
	 ,{1}//for node5
	 ,{0,1}//for node6
	 ,{2,0}//for node7
	 ,{}//for node8
	 ,{}//for node9
	 ,{0}//for node10
	 ,{3,0}//for node11
	 ,{0}//for node12
  };
  /**
   * Table containing the prefix of AXI port names to match
   * external port names in the DSA flow
   */
  public static final String [] NODE_TYPE={
	  "KRNL0_"//for node0
	 ,"DDR0_"//for node1
	 ,"PCI_"//for node2
	 ,""//for node3
	 ,""//for node4
	 ,"KRNL2_"//for node5
	 ,"DDR2_"//for node6
	 ,"DDR1_"//for node7
	 ,""//for node8
	 ,""//for node9
	 ,"KRNL1_"//for node10
	 ,"DDR3_"//for node11
	 ,"KRNL3_"//for node12
  };
  
  
	

}
