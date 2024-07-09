package file

import (
	"bytes"
	"os/exec"
	"strconv"
)

func FindInodePath(inode uint64) (string, error) {
	cmd := exec.Command("find", "/Users/huangsw/code/study/study-golang/file", "-inum", strconv.FormatUint(inode, 10))
	var out bytes.Buffer
	cmd.Stdout = &out
	if err := cmd.Run(); err != nil {
		return "", err
	}
	return out.String(), nil
}
