package edu.uwpr.protinfer.filter;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    public static <T extends Filterable> List<T> filter(List<T> filterables, FilterCriteria<? super T> filterCriteria) {
        List<T> accepted = new ArrayList<T>(filterables.size());
        for (T filterable: filterables) {
            if (filterCriteria.filter(filterable)) {
                filterable.setAccepted(true);
                accepted.add(filterable);
            }
            else
                filterable.setAccepted(false);
        }
        return accepted;
    }
    
}
