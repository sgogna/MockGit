package com.sabre.sabresonic.mockserver.core.servlet;

import com.sabre.sabresonic.mockserver.core.executor.DefaultServiceExecutor;
import com.sabre.sabresonic.mockserver.core.factory.ServiceRegistryFactory;
import com.sabre.sabresonic.mockserver.core.util.SpringBeanContainer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class GenericServicesServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doHead(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        SpringBeanContainer.setApplicationContext(webApplicationContext);
    }

    protected void process(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            final DefaultServiceExecutor executor = new DefaultServiceExecutor();
            executor.setServiceRegistry(ServiceRegistryFactory.createServiceRegistry());
            executor.execute(req, resp);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
