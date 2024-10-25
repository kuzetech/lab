package objectoriented

import (
	"fmt"
	"testing"
)

type Pet struct {
}

func (p *Pet) yell() {
	fmt.Print("...")
}

func (p *Pet) yellToOther(who string) {
	p.yell()
	fmt.Print(" to ", who)
}

type Cat struct {
	Pet
}

func (c *Cat) yell() {
	fmt.Print("miao!")
}

func Test_extend(t *testing.T) {
	c := new(Cat)
	c.yell()              // miao
	c.yellToOther("test") // ... to test

	// 虽然能够使用匿名嵌套类型的方法，但实际并不能重载方法
}
