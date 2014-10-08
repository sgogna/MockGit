package com.sabre.sabresonic.mockserver.frg.generator;

import com.sabre.sabresonic.mockserver.frg.exception.FdgException;
import com.sabre.sabresonic.mockserver.frg.object.FrgRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Freemarker generator implementation
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class FreemarkerGenerator extends TemplateBasedGenerator {

    protected Configuration configuration;

    @Override
    public String process() throws FdgException {
        try {
            Object model = this.getDataModel();
            if (model instanceof FrdRequestAware) {
                ((FrdRequestAware) model).setRequest(getRequest());
            }

            StringWriter out = new StringWriter();
            Template temp = getTemplate();
            temp.process(model, out);
            out.flush();
            return out.toString();
        } catch (Exception ex) {
            throw new FdgException(ex);
        }
    }

    public Template getTemplate() throws IOException {
        return configuration.getTemplate(getTemplatePath());
    }

    /**
     *
     * @return Freemearker configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     *
     * @param configuration Freemearker configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
