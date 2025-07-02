package com.kuzetech.bigdata.json.processor;

import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.kuzetech.bigdata.json.domain.FastjsonAnnotationObject;

public class FastjsonAnnotationObjectExtraProcessor implements ExtraProcessor {

    @Override
    public void processExtra(Object object, String key, Object value) {
        if (object instanceof FastjsonAnnotationObject) {
            FastjsonAnnotationObject annotationObject = (FastjsonAnnotationObject) object;
            annotationObject.extraFields.put(key, value);
        }
    }
}
