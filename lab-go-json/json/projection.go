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
	case "array":
		return buildArrayProject(schema["items"].(map[string]interface{}))
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

func buildArrayProject(items map[string]interface{}) projector {
	p := buildProjector(items)
	return func(v interface{}) interface{} {
		vm := v.([]interface{})
		obj := make([]interface{}, 0, len(vm))
		for _, val := range vm {
			obj = append(obj, p(val))
		}
		return obj
	}
}
