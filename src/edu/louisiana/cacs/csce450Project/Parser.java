package edu.louisiana.cacs.csce450Project;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.TreeMap;

public class Parser {
	String fileName;
	byte b[]=null;
	ArrayList<FinalData> output;
	public Parser(String fileName)
	{
		if(fileName!=null)
		{
		try
	    {
			FileInputStream fiss=new FileInputStream(fileName);
			b=new byte[fiss.available()];
			fiss.read(b);
			//out.println(new String(b));
			
	    }catch(Exception e)
	    {
	    	System.out.println(e);
	    }
		}
	}
	public void printParseTree()
	{
	
	for(FinalData finaldata:output)
	{	
		System.out.println(finaldata.getStack()+" "+finaldata.getIpTokens()+" "+finaldata.getLookUp()+" "+finaldata.getActionValue()+" "+finaldata.getValueOfLhs()+" "+finaldata.getLengthOfRhs()+" "+finaldata.getTempStack()+" "+finaldata.getGotoLookUp()+" "+finaldata.getGotoValue()+" "+finaldata.getStackAction()+" "+finaldata.getParseTreeStack());
		
	}
	}
	public void parse()
	{
		RefTable rt=new RefTable();
	Formulae formulae=new Formulae();
	String stack="", ipTokens="",lookUp="",actionValue="",valueOfLhs="",lengthOfRhs="",tempStack="",gotoLookUp="",gotoValue="",stackAction="",parseTreeStack="";
	//String input="id+id*id$";
	String input=new String(b);
	int lookup_first;
	String lookup_second;
	int gotolookup_first;
	String gotolookup_second;
	output=new ArrayList<FinalData>();
	String actions[]={"id","+","*","(",")","$"};
	String goto_actions[]={"E","T","F"};
	//----- first data
	//oldstack values
	String oldstack_s="";
	String old_tempstack_s="";
	stack="0";
	ipTokens=input;
	TreeMap<Integer,String> nexttokens_index=new TreeMap<Integer,String>();
	//finding all actions keys indexes in the i/p
	for(int i=0;i<actions.length;i++)
	{	int index=input.indexOf(actions[i]);
		if(index!=-1)
			nexttokens_index.put(index,actions[i]);
	}
	// map to store all index nbrs and action tokens
	TreeMap<Integer,String> splittokens=nexttokens_index;
	lookup_first=0;
	lookup_second=nexttokens_index.get(nexttokens_index.firstKey());
	lookUp=lookup_first+","+lookup_second;
	//System.out.println(lookUp);
	//getting the index lookup_second key in array
	for(int i=0;i<actions.length;i++)
	{
		if(actions[i].equalsIgnoreCase(lookup_second))
			actionValue=rt.arr[lookup_first][i];
	}
	//System.out.println(actionValue);
	if(actionValue.indexOf("S")==0)
	{
			oldstack_s=stack;
			valueOfLhs="";
			lengthOfRhs="";
			tempStack="";
			old_tempstack_s="0";
			gotoLookUp="";
			gotoValue="";
			stackAction=lookup_second+actionValue.substring(actionValue.indexOf("S")+1);
			parseTreeStack=lookup_second;
	}
	else if(actionValue.indexOf("R")==0)
	{
		
		valueOfLhs=formulae.formula_lhs.get(actionValue);
		lengthOfRhs=formulae.formula_length_of_rhs.get(actionValue)+"";
		tempStack=old_tempstack_s;
		gotolookup_first=Integer.parseInt(tempStack.substring(tempStack.length()-1));
		gotolookup_second=valueOfLhs;
		gotoLookUp=gotolookup_first+","+gotolookup_second;
		//getting the index gotolookup_second key in array
		for(int i=0;i<goto_actions.length;i++)
		{
			if(goto_actions[i].equalsIgnoreCase(gotolookup_second))
				gotoValue=rt.arr[gotolookup_first][i+5];  // 5 for actions array last index
		}
		
		stackAction=valueOfLhs+gotoValue;
		parseTreeStack="["+valueOfLhs+"]";
	}
	FinalData fd=new FinalData(stack, ipTokens, lookUp, actionValue, valueOfLhs, lengthOfRhs, tempStack, gotoLookUp, gotoValue, stackAction, parseTreeStack);
	//System.out.println(fd);
	output.add(fd);// adding first value
	splittokens.remove(splittokens.firstKey());
	//next data
	String actiontoken=""; // for other action lookups

	while(ipTokens.length()!=1)
	{
		//retrieving last value
		FinalData previous=output.get(output.size()-1);
		//System.out.println("previous columns:\t"+previous);			
		if(previous.getActionValue().indexOf("S")==0)
		{ 	stack=oldstack_s+previous.getStackAction();	//updating stack
			//shifiting to next  action token
			//System.out.println("input:\t"+ipTokens+"split"+splittokens);
			old_tempstack_s=oldstack_s;
			//splittokens.remove(splittokens.firstKey());	// updating to next highest value
			splittokens.clear();
			//finding all actions keys indexes in the i/p
			for(int i=0;i<actions.length;i++)
			{	int index=ipTokens.indexOf(actions[i]);
				if(index!=-1)
					splittokens.put(index,actions[i]);
			}
			
			ipTokens=ipTokens.substring(splittokens.firstKey()+splittokens.get(splittokens.firstKey()).length());
			//System.out.println("after caluculating for next split & before removeining firstkey input:\t"+ipTokens+"split"+splittokens);
			splittokens.remove(splittokens.firstKey());
			actiontoken=splittokens.get(splittokens.firstKey());
			//System.out.println("after caluculating for next split input:\t"+ipTokens+"split"+splittokens);
			//getting action look up value
			if(previous.getStackAction().indexOf("id")!=-1)
			{
				lookup_first=Integer.parseInt((previous.getStackAction()).substring(2));
				lookup_second=actiontoken;
			}
			else
			{
				// for other tokens other than id
				lookup_first=Integer.parseInt((previous.getStackAction()).substring(1));
				lookup_second=actiontoken;
			}
			lookUp=lookup_first+","+lookup_second;
			//System.out.println(lookUp);
			//getting actionValue for the updated action look up
			for(int i=0;i<actions.length;i++)
			{
				if(actions[i].equalsIgnoreCase(lookup_second))
					actionValue=rt.arr[lookup_first][i];
			}
			//System.out.println(actionValue);
			// updating remaining data
			if(actionValue.indexOf("S")==0)
			{
				    oldstack_s=stack;
					valueOfLhs="";
					lengthOfRhs="";
					tempStack="";
					gotoLookUp="";
					gotoValue="";
					stackAction=lookup_second+actionValue.substring(actionValue.indexOf("S")+1);
					if(lookup_second.equalsIgnoreCase("id"))
						parseTreeStack=lookup_second+previous.getParseTreeStack();
					else
						parseTreeStack=previous.getParseTreeStack();
			}
			else if(actionValue.indexOf("R")==0)
			{
				valueOfLhs=formulae.formula_lhs.get(actionValue);
				lengthOfRhs=formulae.formula_length_of_rhs.get(actionValue)+"";
				tempStack=old_tempstack_s;
				gotolookup_first=Integer.parseInt(tempStack.substring(tempStack.length()-1));
				gotolookup_second=valueOfLhs;
				gotoLookUp=gotolookup_first+","+gotolookup_second;
				//getting the index gotolookup_second key in array
				for(int i=0;i<goto_actions.length;i++)
				{
					if(goto_actions[i].equalsIgnoreCase(gotolookup_second))
						gotoValue=rt.arr[gotolookup_first][i+6];  // 5 for actions array last index
				}
				stackAction=valueOfLhs+gotoValue;
				parseTreeStack="["+valueOfLhs+" "+previous.getParseTreeStack()+"]";
			}
			FinalData fd1=new FinalData(stack, ipTokens, lookUp, actionValue, valueOfLhs, lengthOfRhs, tempStack, gotoLookUp, gotoValue, stackAction, parseTreeStack);
			//System.out.println(fd1);
			output.add(fd1);
		}
		else if(previous.getActionValue().indexOf("R")==0)
		{
			 stack=oldstack_s+previous.getStackAction();	//updating stack
				//shifiting to next  action token
				//same input token maintains
				//getting action look up value
				if(previous.getStackAction().indexOf("id")!=-1)
				{
					lookup_first=Integer.parseInt((previous.getStackAction()).substring(2));
					lookup_second=actiontoken;
				}
				else
				{
					// for other tokens other than id
					lookup_first=Integer.parseInt((previous.getStackAction()).substring(1));
					lookup_second=actiontoken;
				}
				lookUp=lookup_first+","+lookup_second;
				//System.out.println(lookUp);
				//getting actionValue for the updated action look up
				for(int i=0;i<actions.length;i++)
				{
					if(actions[i].equalsIgnoreCase(lookup_second))
						actionValue=rt.arr[lookup_first][i];
				}
				//System.out.println(actionValue);
				// updating remaining data
				if(actionValue.indexOf("S")==0)
				{
					oldstack_s=stack;
						valueOfLhs="";
						lengthOfRhs="";
						tempStack="";
						gotoLookUp="";
						gotoValue="";
						stackAction=lookup_second+actionValue.substring(actionValue.indexOf("S")+1);
						//parseTreeStack=lookup_second+previous.getParseTreeStack();
						if(lookup_second.equalsIgnoreCase("id"))
							parseTreeStack=lookup_second+previous.getParseTreeStack();
						else
							parseTreeStack=previous.getParseTreeStack();
				}
				else if(actionValue.indexOf("R")==0)
				{
					valueOfLhs=formulae.formula_lhs.get(actionValue);
					lengthOfRhs=formulae.formula_length_of_rhs.get(actionValue)+"";
					tempStack=old_tempstack_s;
					gotolookup_first=Integer.parseInt(tempStack.substring(tempStack.length()-1));
					gotolookup_second=valueOfLhs;
					gotoLookUp=gotolookup_first+","+gotolookup_second;
					//getting the index gotolookup_second key in array
					for(int i=0;i<goto_actions.length;i++)
					{
						if(goto_actions[i].equalsIgnoreCase(gotolookup_second))
							gotoValue=rt.arr[gotolookup_first][i+6];  // 5 for actions array last index
					}
					stackAction=valueOfLhs+gotoValue;
					parseTreeStack="["+valueOfLhs+" "+previous.getParseTreeStack()+"]";
				}
				FinalData fd1=new FinalData(stack, ipTokens, lookUp, actionValue, valueOfLhs, lengthOfRhs, tempStack, gotoLookUp, gotoValue, stackAction, parseTreeStack);
				//System.out.println(fd1);
				output.add(fd1);					
		}
		if(output.size()==10)
		break;
	}
	printParseTree();
	}
	}

