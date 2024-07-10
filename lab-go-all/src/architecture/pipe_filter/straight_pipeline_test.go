package pipe_filter

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

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
