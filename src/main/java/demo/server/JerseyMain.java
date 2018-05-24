package demo.server;

import java.nio.file.Paths;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.Common;

public class JerseyMain
{
    private static final Logger LOG = LoggerFactory.getLogger(JerseyMain.class);

    public static void main(String[] args) throws Exception
    {
        int httpPort = 9090;
        int httpsPort = 9443;

        Server server = new Server();

        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(httpsPort);

        ServerConnector httpConnector = new ServerConnector(server);
        httpConnector.setPort(httpPort);
        server.addConnector(httpConnector);

        // SSL Context Configuration
        SslContextFactory sslContextFactory = Common.loadSslContextFactory();

        // SSL HTTP Configuration
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector httpsConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        httpsConnector.setPort(httpsPort);

        server.addConnector(httpsConnector);

        // Artifacts Location
        String baseResource = System.getProperty("user.dir");

        if (args.length > 0)
        {
            baseResource = args[0];
        }

        Artifacts artifacts = new Artifacts(Paths.get(baseResource));

        // Handlers
        HandlerCollection handlers = new HandlerCollection();

        ServletContextHandler context = new ServletContextHandler();
        context.setAttribute(Artifacts.class.getName(), artifacts);
        ServletHolder jaxServletContainer = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jaxServletContainer.setInitParameter("javax.ws.rs.Application", DemoApplication.class.getName());

        handlers.addHandler(context);
        handlers.addHandler(new DefaultHandler());

        server.setHandler(handlers);

        // Server start
        server.start();
        server.join();
    }
}
