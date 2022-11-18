package main

import (
	"github.com/rs/zerolog/log"
	"strconv"
	"sync"
)

type statistics struct {
	mu    sync.Mutex
	count uint64
}

func (s *statistics) addCount(size uint64) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.count += size
	log.Info().Msg("一共发送数据数量为：" + strconv.FormatUint(s.count, 10))
}

func (s *statistics) getCount() uint64 {
	s.mu.Lock()
	defer s.mu.Unlock()
	return s.count
}
