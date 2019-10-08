package com.rbkmoney.fistful.magista.endpoint;

import com.rbkmoney.fistful.fistful_stat.FistfulStatisticsSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet(urlPatterns = {"/stat", "/fistful/stat"})
public class FistfulStatisticsServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private FistfulStatisticsSrv.Iface requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(FistfulStatisticsSrv.Iface.class, requestHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
