package demo;

import java.io.FileNotFoundException;
import java.net.URL;

import org.eclipse.jetty.util.ssl.SslContextFactory;

public class Common
{
    public static SslContextFactory loadSslContextFactory() throws FileNotFoundException
    {
        URL keystoreUrl = findResource("keystore");
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreUrl.toExternalForm());
        sslContextFactory.setKeyStorePassword("storepwd");
        sslContextFactory.setKeyManagerPassword("keypwd");

        return sslContextFactory;
    }

    private static URL findResource(String resourceName) throws FileNotFoundException
    {
        ClassLoader cl = Common.class.getClassLoader();
        URL url = cl.getResource(resourceName);
        if (url == null)
            throw new FileNotFoundException("Unable to find resource: " + resourceName);
        return url;
    }
}
