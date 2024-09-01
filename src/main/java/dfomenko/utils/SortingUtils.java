package dfomenko.utils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class SortingUtils {

    public Sort makeSort(String sortOrder) throws ParseException {
        if (sortOrder == null) {
            throw new NullPointerException("Source string \"sortOrder\" is null.");
        }
        if (sortOrder.equals("off")) {
            return Sort.unsorted();
        }
        String[] sortOrderParts = sortOrder.split("(?=[A-Z](?=[a-z]*(?>$)))");
        if (sortOrderParts.length != 2) {
            throw new ParseException("Source string must match \"sortOrderColumnNameAsc\" or \"sortOrderColumnNameDesc\", but present \"" + sortOrder + "\".", 0);
        }
        sortOrderParts[1] = sortOrderParts[1].toUpperCase();

        return Sort.by(Sort.Direction.valueOf(sortOrderParts[1]), sortOrderParts[0]);
    }

}
