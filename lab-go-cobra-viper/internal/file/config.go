package file

import "errors"

type Config struct {
	GeneratePath  string
	RatePerSecond int
	EventSize     int
	ServerSize    int
}

func (c *Config) checkParameters() error {
	result := c.RatePerSecond % c.EventSize
	if result != 0 {
		return errors.New("RatePerSecond % EventSize must = 0")
	}
	return nil
}
