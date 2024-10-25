package pipeline

import (
	"errors"
)

var SumFilterWrongFormatError = errors.New("input data should be []int")

type SumFilter struct {
}

func NewSumFilter() *SumFilter {
	return &SumFilter{}
}

func (f *SumFilter) Process(data Request) (Response, error) {
	parts, ok := data.([]int)
	if !ok {
		return nil, SumFilterWrongFormatError
	}
	ret := 0
	for _, i := range parts {
		ret = ret + i
	}
	return ret, nil
}
