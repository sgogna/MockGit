package com.sabre.sabresonic.mockserver.frg.generator;

import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;

/**
 * Abstract class that generates response based on template
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public abstract class TemplateBasedGenerator implements Generator, FrdRequestAware {

    /**
     * path to template
     */
    protected String templatePath;
    
    protected Object dataModel;
    
    protected FrgRequest request;
    /**
     *
     * @return path to template
     */
    public String getTemplatePath() {
        return templatePath;
    }
    
    /**
     * path to template
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public void setDataModel(Object dataModel) {
        this.dataModel = dataModel;
    }

    public Object getDataModel() {
        return dataModel;
    }
    
    @Override
    public void setRequest(FrgRequest request) {
        this.request = request;
    }

    public FrgRequest getRequest() {
        return request;
    }
    
}
