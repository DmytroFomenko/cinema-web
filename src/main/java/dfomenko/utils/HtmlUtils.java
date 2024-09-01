package dfomenko.utils;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;

import java.util.Map;

@AllArgsConstructor
@Component
public class HtmlUtils {

    private SpringTemplateEngine templateEngine;

    private ApplicationContext applicationContext;


    public String makeHtmlFromTemplate(String templateFilePath,
                                       Map<String, Object> parameters) {

        Context templateContext = new Context();

        // Copy all needed variables from thymeleaf environments context to mailTemplateContext
        ThymeleafEvaluationContext thymeleafEvaluationContext = new ThymeleafEvaluationContext(applicationContext, null);
        templateContext.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                                    thymeleafEvaluationContext);

        // Copy model variables to mailTemplateContext
        if (parameters != null) {
            templateContext.setVariables(parameters);
        }

        // Build new mail Html
        return templateEngine.process(templateFilePath, templateContext);
    }
}
