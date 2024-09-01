package dfomenko.service.impl;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import dfomenko.service.PdfService;
import dfomenko.utils.HtmlUtils;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@AllArgsConstructor
@Service
public class PdfServiceImpl implements PdfService {

    private HtmlUtils htmlUtils;

    @Override
    public byte[] convertHtmlToPdf(String htmlString) throws IOException {

        Document document = Jsoup.parse(htmlString, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().prettyPrint(false);


        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
            pdfRendererBuilder.toStream(outputStream);
            pdfRendererBuilder.useDefaultPageSize(297, 210, PdfRendererBuilder.PageSizeUnits.MM);

            String fontFilePath = "fonts/PTSans-Regular.ttf";
            InputStream fontInputStream = getClass().getClassLoader().getResourceAsStream(fontFilePath);
            if (fontInputStream != null) {
                FSSupplier<InputStream> fontSupplier = () -> fontInputStream;
                pdfRendererBuilder.useFont(fontSupplier, "PTSans");
            } else {
                throw new IOException("Font resource \"" + fontFilePath + "\" not founded");
            }

            pdfRendererBuilder.withProducer("FDV Cinema web App");
            pdfRendererBuilder.withW3cDocument(new W3CDom().fromJsoup(document), "/");
            pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.run();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error while converting HTML to PDF: " + e.getMessage());
        }

    }


    @Override
    public byte[] convertTemplatedHtmlToPdf(String templateFilePath,
                                            Map<String, Object> parameters) throws IOException {
        // Build new mail Html
        String htmlText = htmlUtils.makeHtmlFromTemplate(templateFilePath, parameters);
        return convertHtmlToPdf(htmlText);
    }


}


