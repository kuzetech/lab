package main

import (
	"errors"
	"fmt"
	"github.com/dgraph-io/badger/v3"
	"log"
)

const (
	dbPath = "./badger"
)

var DB *badger.DB

func init() {
	db, openErr := badger.Open(badger.DefaultOptions(dbPath))
	if openErr != nil {
		log.Fatal(openErr)
	}
	DB = db
}

func closeDB() {
	err := DB.Close()
	if err != nil {
		log.Println("关闭数据库失败")
	}
}

func printAllKeys() {
	DB.View(func(txn *badger.Txn) error {
		opts := badger.DefaultIteratorOptions
		opts.PrefetchSize = 10
		it := txn.NewIterator(opts)
		defer it.Close()
		for it.Rewind(); it.Valid(); it.Next() {
			item := it.Item()
			k := item.Key()
			err := item.Value(func(v []byte) error {
				fmt.Printf("key=%s, value=%s\n", k, v)
				return nil
			})
			if err != nil {
				return err
			}
		}
		return nil
	})
}

func keyExist(key string) (bool, error) {
	err := DB.View(func(txn *badger.Txn) error {
		_, err := txn.Get([]byte(key))
		if err != nil {
			return err
		}
		return nil
	})

	if err == nil {
		return true, nil
	}

	if errors.As(err, &badger.ErrKeyNotFound) {
		return false, nil
	}

	// 出现意外的错误
	return false, err
}

func setKey(key string, value string) error {
	err := DB.Update(func(txn *badger.Txn) error {
		err := txn.Set([]byte(key), []byte(value))
		return err
	})
	return err
}

func getKey(key string) (string, error) {
	var valCopy []byte
	err := DB.View(func(txn *badger.Txn) error {
		item, err := txn.Get([]byte(key))
		if err != nil {
			return err
		}

		err = item.Value(func(val []byte) error {
			// 如果外部需要使用值一定要深度拷贝
			valCopy = append([]byte{}, val...)

			// 如果直接赋值可能会出现异常情况，如下
			// valCopy = val

			return nil
		})

		return err
	})

	if err != nil {
		return "", err
	}

	return string(valCopy), nil

}
