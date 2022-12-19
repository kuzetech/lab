package json

type projector func(interface{}) interface{}

func copyProjector(v interface{}) interface{} {
	return v
}

func buildProjector(schema map[string]interface{}) projector {
	t := schema["type"]
	switch t {
	case "object":
		return buildObjectProjector(schema["properties"].(map[string]interface{}))
	//case "array":
	//	return buildArrayProject(schema["items"].(map[string]interface{}))
	default:
		return copyProjector
	}
}

func buildObjectProjector(properties map[string]interface{}) projector {
	projectors := map[string]projector{}

	for field, schema := range properties {
		projectors[field] = buildProjector(schema.(map[string]interface{}))
	}

	return func(v interface{}) interface{} {
		obj := map[string]interface{}{}

		for f, p := range projectors {
			obj[f] = p(v.(map[string]interface{})[f])
		}

		return obj
	}
}

//func buildArrayProject(items map[string]interface{}) projector {
//
//}

func buildMapProjector(fieldsToKeep []string, valueProjector projector) projector {
	return func(v interface{}) interface{} {
		if v == nil {
			return nil
		}
		m, ok := v.(map[string]interface{})
		if !ok {
			return nil
		}

		copyOfM := map[string]interface{}{}

		for _, f := range fieldsToKeep {
			copyOfM[f] = valueProjector(m[f])
		}

		return copyOfM
	}
}

func buildSliceProjector(elementProjector projector) projector {
	return func(v interface{}) interface{} {
		if v == nil {
			return nil
		}
		l, ok := v.([]interface{})
		if !ok {
			return nil
		}

		copyOfL := make([]interface{}, 0)
		for _, elem := range l {
			projectedElem := elementProjector(elem)
			if projectedElem == nil {
				continue
			}
			copyOfL = append(copyOfL, projectedElem)
		}
		return copyOfL
	}
}
