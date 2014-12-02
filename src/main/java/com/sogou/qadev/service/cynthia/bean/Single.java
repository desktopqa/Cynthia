/**
 * 
 */
package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

public class Single<First> implements Serializable
{
    private First first = null;

    public First getFirst()
    {
    	return this.first;
    }

    public void setFirst(First first)
    {
    	this.first = first;
    }

    public String toString()
    {
    	return "[" + this.first + "]";
    }
}