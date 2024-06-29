package filewatch

import (
	"github.com/fsnotify/fsnotify"
	"log"
	"testing"
)

func Test_fsnotify(t *testing.T) {
	watcher, err := fsnotify.NewWatcher()
	if err != nil {
		log.Fatal(err)
	}

	defer watcher.Close()

	done := make(chan bool)

	//1、创建新的 goroutine，等待管道中的事件或错误
	go func() {
		for {
			select {
			case event, ok := <-watcher.Events:
				if !ok {
					return
				}
				log.Printf("监听到文件 %s 变化| ", event.Name)
				switch event.Op {
				case fsnotify.Create:
					log.Println("创建事件", event.Op)
				case fsnotify.Write:
					log.Println("写入事件", event.Op)
				case fsnotify.Remove:
					log.Println("删除事件", event.Op)
				case fsnotify.Rename:
					log.Println("重命名事件", event.Op)
				case fsnotify.Chmod:
					log.Println("属性修改事件", event.Op)
				default:
					log.Println("some thing else")
				}
			case watchErr, ok := <-watcher.Errors:
				if !ok {
					return
				}
				log.Println("error:", watchErr)
			}
		}
	}()

	// 2、使用 watcher 的 Add 方法增加需要监听的文件或目录到监听队列中
	err = watcher.Add("/Users/huangsw/code/study/study-golang/uberZap/logs")
	if err != nil {
		log.Fatal(err)
	}

	// 3、主线程无限等待
	<-done
}
