package org.itech.locator.form.webapp.bean;

import lombok.Data;
import java.util.List;

@Data
public class ValueListHolder implements ValueHolder {
    
    private String value;
    
    private String label;
    
    private List<String> list;
}
