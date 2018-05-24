package demo.server;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("largefile")
public class LargeFileResource
{
    private Artifacts artifacts;

    @Context
    public void setServletContext(ServletContext context)
    {
        artifacts = (Artifacts) context.getAttribute(Artifacts.class.getName());
    }

    @GET
    @Path("/{filename}")
    public Response getFile(@PathParam("filename") String filename) throws IOException
    {
        Artifact artifact = artifacts.getArtifact(filename);

        if (artifact == null)
            return Response.status(404).build();

        return artifact.toResponse();
    }
}
