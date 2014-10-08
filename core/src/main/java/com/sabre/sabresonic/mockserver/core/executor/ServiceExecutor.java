package com.sabre.sabresonic.mockserver.core.executor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServiceExecutor {

    void execute(
            final HttpServletRequest request,
            final HttpServletResponse response);
}
