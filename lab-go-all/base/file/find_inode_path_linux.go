package file

import (
	"fmt"
	"os"
)

// 该方法可能是错的
func FindInodePath(inode uint64) (string, error) {
	path, err := os.Readlink(fmt.Sprintf("/proc/self/fd/%d", inode))
	if err != nil {
		return fmt.Errorf("os Readlink error : %s", err)
	}
	return path, nil
}
