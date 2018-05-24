package demo.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.jetty.util.IO;

public class Artifact implements StreamingOutput
{
    public Path path;

    public InputStream getInputStream() throws IOException
    {
        return Files.newInputStream(path);
    }

    public Response toResponse() throws IOException
    {
        return Response.ok(this, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Length", Files.size(path))
                .header("Content-Disposition", "attachment; filename=\"" + path.getFileName() + "\"")
                .build();
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, WebApplicationException
    {
        IO.copy(getInputStream(), outputStream);
    }
}
