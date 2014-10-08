package com.sabre.sabresonic.mockserver.frg.dataModel.ext;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class RotateIdGenerator {
    protected long max = 10l;
    protected long value = 0l;

    public long getId() {
        if(value < max){
            value++;
        }else{
            value = 0;
        }
        return value;
    }
    
    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
    
}
