package logger

import (
	"github.com/rs/zerolog/log"
	"testing"
)

type ReadFileInfo struct {
	BeginByteOffset uint64
	EndByteOffset   uint64
}

func Test_map(t *testing.T) {

	var requestBatchReadFileInfo = make(map[string]ReadFileInfo)
	requestBatchReadFileInfo["123"] = ReadFileInfo{
		BeginByteOffset: 1,
		EndByteOffset:   2,
	}

	log.Info().Interface("test", requestBatchReadFileInfo).Msg("t")

}
