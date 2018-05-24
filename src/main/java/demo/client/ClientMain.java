package demo.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import demo.Common;

public class ClientMain
{
    public static void main(String[] args) throws Exception
    {
        String uriString = "http://localhost:9090/largefile/pom.xml";

        if (args.length >= 1)
            uriString = args[0];

        URI srcUri = URI.create(uriString);

        SslContextFactory ssl = Common.loadSslContextFactory();

        HttpClient client = new HttpClient(ssl);
        try
        {
            client.start();

            Request request = client.newRequest(srcUri);

            System.out.printf("Using HttpClient v%s%n", getHttpClientVersion());
            System.out.printf("Requesting: %s%n", srcUri);
            InputStreamResponseListener streamResponseListener = new InputStreamResponseListener();
            request.send(streamResponseListener);
            Response response = streamResponseListener.get(5, TimeUnit.SECONDS);

            if (response.getStatus() != HttpStatus.OK_200)
            {
                throw new IOException(
                        String.format("Failed to GET URI [%d %s]: %s",
                                response.getStatus(),
                                response.getReason(),
                                srcUri));
            }

            Path tmpFile = Files.createTempFile(Paths.get("target"), "tmp", ".dl");

            try (InputStream inputStream = streamResponseListener.getInputStream();
                 OutputStream outputStream = Files.newOutputStream(tmpFile))
            {
                IO.copy(inputStream, outputStream);
            }

            System.out.printf("Downloaded %s%n", srcUri);
            System.out.printf("Destination: %s (%,d bytes)%n", tmpFile.toString(), Files.size(tmpFile));
        }
        finally
        {
            client.stop();
        }
    }

    private static String getHttpClientVersion()
    {
        ClassLoader cl = HttpClient.class.getClassLoader();

        // Attempt to use maven pom properties first
        String pomResource = "/META-INF/maven/org/eclipse/jetty/jetty-client/pom.properties";
        URL url = cl.getResource(pomResource);
        if (url != null)
        {
            try (InputStream in = url.openStream())
            {
                Properties props = new Properties();
                props.load(in);
                String version = props.getProperty("version");
                if (StringUtil.isNotBlank(version))
                    return version;
            }
            catch (IOException ignore)
            {
            }
        }

        // Attempt to use META-INF/MANIFEST.MF
        String version = HttpClient.class.getPackage().getImplementationVersion();
        if (StringUtil.isNotBlank(version))
            return version;

        return "<unknown>";
    }
}
