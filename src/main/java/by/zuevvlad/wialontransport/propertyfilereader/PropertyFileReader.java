package by.zuevvlad.wialontransport.propertyfilereader;

import java.io.File;
import java.util.Properties;

@FunctionalInterface
public interface PropertyFileReader {
    Properties read(final File file);
}
