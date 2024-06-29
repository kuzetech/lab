package array

import (
	"log"
	"testing"
)

// 形参
func deleteItem21(data []int64, index int) {
	data = append(data[:index], data[index+1:]...)
}

// 实参
func deleteItem2(data *[]interface{}, index int) {
	*data = append((*data)[:index], (*data)[index+1:]...)
}

// 形参
func deleteItem3(data interface{}, index int) {
	pd := data.([]interface{})
	data = append(pd[:index], pd[index+1:]...)
}

// 形参
func deleteItem4(data *interface{}, index int) {
	pd := (*data).([]interface{})
	*data = append(pd[:index], pd[index+1:]...)
}

func TestArray(t *testing.T) {

	a := make([]interface{}, 3)
	a[0] = 1
	a[1] = 2
	a[2] = 3

	var b interface{} = a
	deleteItem4(&b, 2)

	log.Println(a)

}

func TestDelete(t *testing.T) {
	var a = []int{1, 2, 3}
	var b = &a
	*b = append((*b)[:2], (*b)[3:]...)
	log.Println(a)
	log.Println(b)
}
