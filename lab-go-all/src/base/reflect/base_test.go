package _reflect

import (
	"github.com/stretchr/testify/assert"
	"reflect"
	"testing"
)

// 属性的话必须是大写才能访问
type Dog struct {
	Name string
	Age  int
}

// 这里方法必须是大写才能访问
// 并且明确区分了绑定在实体类上还是指针上
func (d *Dog) UpdateAge(age int) {
	d.Age = age
}

func Test_get_field_Value(t *testing.T) {
	d := Dog{"test", 1}
	// 这里必须在实体类上，不能使用指针
	dogReflect := reflect.ValueOf(d)
	ageFieldValue := dogReflect.FieldByName("Age")
	t.Log(ageFieldValue.Int())
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
	t.Log(ageFieldValue.Int())

}
