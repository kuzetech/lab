package _reflect

import (
	"github.com/stretchr/testify/assert"
	"reflect"
	"testing"
)

// 属性的话必须是大写才能访问
type Dog struct {
	Name string `format:"test"`
	Age  int
}

/*
	方法名必须是大写才能访问
	并且明确区分了是绑定在实体类上还是指针上
	如果绑定在 实体，传入的是指针类型的话只需要调用 Elem 方法获取实体就可以访问，但是由于值传递的原因，如果更新了值，原对象实际是没有变化的
	如果绑定在 指针，传入的是对象类型的话，是没有办法访问该方法的
*/

func (d *Dog) UpdateAge(age int) {
	d.Age = age
}

func Test_get_field_Value(t *testing.T) {
	d := &Dog{"test", 1}
	dogReflectValue := reflect.ValueOf(d)
	// 传入 reflect 的是指针的话，就必须调用 Elem 获取到真实对象
	ageFieldValue := dogReflectValue.Elem().FieldByName("Age")
	assert.Equal(t, 1, ageFieldValue.Int())
}

func Test_get_field_Tag(t *testing.T) {
	d := Dog{"test", 1}
	dogReflectType := reflect.TypeOf(d)
	nameField, exist := dogReflectType.FieldByName("Name")
	assert.Equal(t, true, exist)
	tagValue := nameField.Tag.Get("format")
	t.Log(tagValue)
}

func Test_method_call(t *testing.T) {
	d := &Dog{"test", 1}

	// 由于方法绑定在指针上，所以传入的参数也必须是指针
	dogTypeReflect := reflect.TypeOf(d)
	method, exist := dogTypeReflect.MethodByName("UpdateAge")
	assert.Equal(t, true, exist)

	t.Log(method.Type) // func(*_reflect.Dog, int)

	// 从上面的方法中可以看到还必须传入 *_reflect.Dog
	inputs := []reflect.Value{
		reflect.ValueOf(d),
		reflect.ValueOf(30),
	}
	method.Func.Call(inputs)

	dogReflect := reflect.ValueOf(*d)
	ageFieldValue := dogReflect.FieldByName("Age")
	assert.Equal(t, int64(30), ageFieldValue.Int())

}
