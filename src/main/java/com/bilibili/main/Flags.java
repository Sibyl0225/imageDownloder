package com.bilibili.main;

public class Flags {
	
    private static Flags instance;
    private Flags() {}
    public static  Flags getInstance() {
        if (instance == null) { 
        	synchronized (Flags.class){       		
        		instance = new Flags();
        	}
        }
        return instance;
    }
	
	public boolean hasAnimations = false;
	
	public boolean reverseFlag = false;
	
	public boolean isHasAnimations() {
		return hasAnimations;
	}

	public void setHasAnimations(boolean hasAnimations) {
		this.hasAnimations = hasAnimations;
	}

	public boolean isReverseFlag() {
		return reverseFlag;
	}

	public void setReverseFlag(boolean reverseFlag) {
		this.reverseFlag = reverseFlag;
	}
	
	
}
