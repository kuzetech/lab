package main

func main() {
	// testNormal()
	// testPipelines()
	// incrementInTransaction("abc")
	// testSub()
	// testLuaScript()

	client := NewRedisClientByOption()
	client.scanKeysByPrefix("my*")

}
