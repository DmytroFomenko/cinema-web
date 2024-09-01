package dfomenko.service;

import java.io.IOException;
import java.util.Map;


public interface PdfService {

    public byte[] convertHtmlToPdf(final String htmlString) throws IOException;
    public byte[] convertTemplatedHtmlToPdf(String templateFilePath,
                                            Map<String, Object> parameters) throws IOException;
}
