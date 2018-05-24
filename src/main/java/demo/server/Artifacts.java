package demo.server;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Artifacts
{
    private static final Logger LOG = LoggerFactory.getLogger(Artifacts.class);

    private final Path base;

    public Artifacts(Path base)
    {
        this.base = base;
        LOG.info("Artifacts base: {}", base.toString());
    }

    public Artifact getArtifact(String filename)
    {
        Path path = base.resolve(filename);

        if (Files.exists(path) && Files.isRegularFile(path))
        {
            Artifact artifact = new Artifact();
            artifact.path = path;
            return artifact;
        }

        return null;
    }
}
