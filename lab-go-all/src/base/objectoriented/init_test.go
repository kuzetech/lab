package objectoriented

import "testing"

type Student struct {
	Name string
	Age  int
}

func Test_init(t *testing.T) {
	s1 := Student{"1", 1}
	t.Logf("%T", s1)

	s2 := Student{Name: "2", Age: 2}
	t.Logf("%T", s2)

	s3 := new(Student) // 注意返回的是指针
	t.Logf("%T", s3)
	s3.Name = "3"
	s3.Age = 3

}
