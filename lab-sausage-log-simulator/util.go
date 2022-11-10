package main

import "os"

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
	file, err := os.OpenFile(path, os.O_CREATE|os.O_RDONLY, os.ModePerm)
	if err != nil {
		return err
	}
	file.Close()
	return nil
}
