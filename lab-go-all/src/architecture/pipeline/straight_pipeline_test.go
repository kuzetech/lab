package pipeline

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

/*
	垂直的 pipeline 架构，意味着没有分支

*/

func Test_straight_pipeline(t *testing.T) {
	pipeline := NewStraightPipeline(
		"p1",
		NewSplitFilter(","),
		NewToIntFilter(),
		NewSumFilter(),
	)

	response, err := pipeline.Process("1,2,3")
	assert.Nil(t, err)
	assert.Equal(t, 6, response)
}
