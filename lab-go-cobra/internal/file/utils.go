package file

import (
	"fmt"
	"os"
	"time"
)

func HasFile(path string) (bool, error) {
	_, err := os.Stat(path)
	if err == nil {
		return true, nil
	}
	if os.IsNotExist(err) {
		return false, nil
	}
	return false, err
}

func RemoveFile(path string) error {
	exist, err := HasFile(path)
	if err != nil {
		return err
	}
	if exist {
		err = os.Remove(path)
		if err != nil {
			return err
		}
	}
	return nil
}

func CreateDir(path string) error {
	exist, err := HasFile(path)
	if err != nil {
		return err
	}
	if exist {
		return nil
	} else {
		mkdirErr := os.MkdirAll(path, os.ModePerm)
		if mkdirErr != nil {
			return mkdirErr
		}
		return nil
	}
}

func CreateFile(path string) error {
	file, err := os.OpenFile(path, os.O_APPEND|os.O_CREATE|os.O_WRONLY, os.ModePerm)
	if err != nil {
		return err
	}
	err = file.Close()
	if err != nil {
		return err
	}
	return nil
}

func createSpecifiedTimeParentDirAndFile(specifiedTime time.Time, logParentDir string, serverSize int) error {
	year, month, day := specifiedTime.Date()
	hour := specifiedTime.Hour()

	for i := 1; i <= serverSize; i++ {
		currentParentDirPath := fmt.Sprintf(dayLogDirPathFmt, logParentDir, i, year, month, day)
		err := CreateDir(currentParentDirPath)
		if err != nil {
			return err
		}

		currentHourLogPath := fmt.Sprintf(hourLogPathFmt, logParentDir, i, year, month, day, hour)
		err = CreateFile(currentHourLogPath)
		if err != nil {
			return err
		}
	}

	return nil
}
