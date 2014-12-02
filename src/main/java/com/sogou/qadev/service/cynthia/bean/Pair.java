package com.sogou.qadev.service.cynthia.bean;

public class Pair<First, Second> extends Single<First>
{
    private Second second = null;

    public Pair()
    {
    }

    public Pair(First first, Second second)
    {
	    setFirst(first);
	    setSecond(second);
    }

    public Second getSecond()
    {
    	return this.second;
    }

    public void setSecond(Second second)
    {
    	this.second = second;
    }

    public String toString()
    {
    	return "[" + getFirst() + "," + this.second + "]";
    }

    public boolean equals(Object arg0)
    {
	    Pair pair = null;
	    try
	    {
	      pair = (Pair)arg0;
	    }
	    catch (ClassCastException cce)
	    {
	      return false;
	    }
	
	    return (getFirst().equals(pair.getFirst())) && (this.second.equals(pair.getSecond()));
    }

    public int hashCode()
    {
    	return getFirst().hashCode() ^ getSecond().hashCode();
    }
}