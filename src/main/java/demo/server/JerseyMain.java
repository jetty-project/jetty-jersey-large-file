package demo.server;

import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JerseyMain
{
    private static final Logger LOG = LoggerFactory.getLogger(JerseyMain.class);

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(9090);

        HandlerCollection handlers = new HandlerCollection();

        String baseResource = System.getProperty("user.dir");

        if (args.length > 0)
        {
            baseResource = args[0];
        }

        Artifacts artifacts = new Artifacts(Paths.get(baseResource));

        ServletContextHandler context = new ServletContextHandler();
        context.setAttribute(Artifacts.class.getName(), artifacts);
        ServletHolder jaxServletContainer = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jaxServletContainer.setInitParameter("javax.ws.rs.Application", DemoApplication.class.getName());

        handlers.addHandler(context);
        handlers.addHandler(new DefaultHandler());

        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
