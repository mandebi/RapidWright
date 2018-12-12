package com.xilinx.rapidwright.linkblaze;

/**
 * Class containing resource purposed to manipulate a truth table
 * @author Joel
 */

public class TruthTableStructure {
	
	 public int variableNumber;
	    public int rows;
	    public int columns;
	    public int[][]table;

	    public TruthTableStructure(int variableNumber) {
	        this.variableNumber = variableNumber;
	        this.rows = (int) Math.pow(2, variableNumber);
	        this.columns = variableNumber + 1;
	        this.table = new int[rows][columns];
	    }
	   
	   /**
	    * Function thats generates the content of the truth table(not the result, but just 
	    * the different input values
	    */
	   public void truthTableGenerator()
	   {
	       int iterator = (int) Math.pow(2,(variableNumber-1));
	       int temp;
	       
	       for(int i=0; i< rows; i++)
	       {   
	          /*
	                gives  the frequency at which values must
	               switch from 0 to 1 (and from 1 to 0)on column "j"
	               */
	               //temp =iterator/(j+1);
	               temp =iterator;
	          for(int j=0; j<(columns-1);j++) 
	           { 
	               
	              if(i == 0 )//first row
	                 table[i][j] = 0;
	              else
	               {
	                 if( (i%temp)==0 )
	                 {
	                   if(table[i-1][j] == 0)
	                       table[i][j] = 1;
	                   else
	                       table[i][j] = 0; 
	                 } 
	                 else
	                   table[i][j] = table[i-1][j];
	               }  
	              
	              //System.out.println(">> i="+i +"-- j ="+j+ "-- temp="+temp+"-- table[i][j]="+table[i][j]);
	              temp = temp/2;
	           }
	       }
	   }      
	    
	   public void displayTruthTable()
	   {
	     for(int j=0; j<(columns);j++)
	         if(j<(columns-1))
	              System.out.print("I"+(columns-2-j)+" ");
	         else
	             System.out.print("Output");
	     System.out.print("\n");
	     
	     for(int i=0; i< rows; i++)
	     {  
	          for(int j=0; j<(columns);j++)
	              System.out.print(table[i][j]+"  ");
	         System.out.print("\n");   
	     } 
	     //System.out.print("over");
	   } 
	   
	   /**
	    * Takes as input a vector and adds it as the result column to the truth table represented by
	    * the field "table" of this object
	    * @param vec result column for the truth table
	    */
	   public void addResultsToTruthTable(int[] vec)
	   {
	       for(int i=0; i< rows; i++)
	          table[i][columns-1] = vec[i];
	       
	   }
	   
	   /**
	    * Generation of the "non-optimized" boolean equation from the truth table
	    * @return
	    */
	   public String generateBooleanEquation()
	   {
	      String equation="";
	      int literalCounter = 0;
	      boolean isAlwaysZero = true;//if no ouput = 1 then the LUT will always return 0
	      
	      for(int i=0; i< rows; i++)
	        if(table[i][columns-1]==1) 
	        {   
	        	isAlwaysZero = false;
	            if(literalCounter > 0)//first element added into the equation
	               equation+=" + ";   
	            equation+="("; 
	          for(int j=0; j<(columns-1);j++)
	          { 
	        	 if(j == 0)
	        	  {
	        		if(table[i][j]== 1 )
		                 equation+=""+(columns-2-j);
		            else
		                equation+="!"+(columns-2-j); 
	        	  }
	        	 else
	        	  {
	        		 if(table[i][j]== 1 )
		                 equation+=" & "+(columns-2-j);
		            else
		                equation+=" & !"+(columns-2-j); 
	        	  }
	            literalCounter++;  
	          }
	          equation+=")";
	        }
	      
	      if (isAlwaysZero)
	    	  return "0";
	      else
	          return equation;
	   }   
	   
	   
	   /**
	    * Generation of the "non-optimized" boolean equation from the truth table
	    * @param labels vector of String containing the label that will be used for each input of 
	    * the truth table
	    * @return returns the boolean equation devised from the truth table
	    */
	    
	   public String generateBooleanEquation(String[] labels)
	   {
	      String equation="o=";
	      int literalCounter = 0;
	      boolean isAlwaysZero = true;//if no ouput = 1 then the LUT will always return 0
	      
	      for(int i=0; i< rows; i++)
	        if(table[i][columns-1]==1) 
	        {   
	        	isAlwaysZero = false;
	            if(literalCounter > 0)//first element added into the equation
	               equation+=" + ";   
	            equation+="("; 
	          for(int j=0; j<(columns-1);j++)
	          { 
	        	 if(j == 0)
	        	  {
	        		if(table[i][j]== 1 )
		                 equation+=""+labels[(columns-2-j)];
		            else
		                equation+="!"+labels[(columns-2-j)]; 
	        	  }
	        	 else
	        	  {
	        		 if(table[i][j]== 1 )
		                 equation+=" & "+labels[(columns-2-j)];
		            else
		                equation+=" & !"+labels[(columns-2-j)]; 
	        	  }
	            literalCounter++;  
	          }
	          equation+=")";
	        }
	      
	      if (isAlwaysZero)
	    	  return equation+"0";
	      else
	          return equation;
	   }
	   

}
