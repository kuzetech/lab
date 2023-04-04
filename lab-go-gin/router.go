package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"time"
)

// LoginForm
// form 针对 Content-Type 等于 x-www-form-urlencoded 或 form-data
// json 针对 Content-Type 等于 json
// 如果一个字段的 tag 加上了 binding:"required"，但绑定时是空值, Gin 会报错
// 关于校验的文档可以参考 https://pkg.go.dev/gopkg.in/go-playground/validator.v8#hdr-Baked_In_Validators_and_Tags
// 直接在 binding 中添加对应的 tag
type LoginForm struct {
	User     string `form:"user" json:"user" binding:"required,min=5,max=10"`
	Password string `form:"password" json:"password" binding:"required"`
}

// Person
// uri 从路由中获取参数
type Person struct {
	ID   string `uri:"id" binding:"required"`
	Name string `uri:"name" binding:"required"`
}

type JsonResponse struct {
	Code int    `json:"code"`
	Msg  string `json:"msg"`
}

func initGinRouter(router *gin.Engine) {
	v1Group := router.Group("/v1", ginConcurrenceLimitMiddleware, upstreamParameterMiddleware, downstreamParameterMiddleware, bodyContentByteLimitMiddleware)

	v1Group.POST("/test", func(c *gin.Context) {
		c.String(http.StatusOK, "test")
	})

	v1Group.GET("/my", func(c *gin.Context) {
		time.Sleep(time.Second * 2)
		c.String(http.StatusOK, "Hello")
	})

	// 解析 get 请求参数
	// 示例 URL： /welcome?firstname=Jane&lastname=Doe
	v1Group.GET("/welcome", func(c *gin.Context) {
		firstname := c.DefaultQuery("firstname", "Guest")
		lastname := c.Query("lastname") // c.Request.URL.Query().Get("lastname") 的一种快捷方式

		c.String(http.StatusOK, "Hello %s %s", firstname, lastname)
	})

	// 获取路由参数
	// 此 handler 将匹配 /user/john 但不会匹配 /user/ 或者 /user
	v1Group.GET("/user/:name", func(c *gin.Context) {
		name := c.Param("name")
		c.String(http.StatusOK, "Hello %s", name)
	})

	// 从路由中绑定参数
	v1Group.GET("/router/:name/:id", func(c *gin.Context) {
		var person Person
		// 如果要绑定路由参数必须使用 ShouldBindUri
		// ShouldBind 只能用来绑定 body
		if err := c.ShouldBindUri(&person); err != nil {
			c.JSON(400, gin.H{"msg": err.Error()})
			return
		}
		c.JSON(200, gin.H{"name": person.Name, "uuid": person.ID})
	})

	// 获取路由参数
	// 此 handler 将匹配 /user/john/ 和 /user/john/send
	// 如果没有其他路由匹配 /user/john，它将重定向到 /user/john/
	v1Group.GET("/user/:name/*action", func(c *gin.Context) {
		name := c.Param("name")
		action := c.Param("action")
		message := name + " is " + action
		c.String(http.StatusOK, message)
	})

	// 获取请求中的 map array 类型参数
	// POST /post?ids[a]=1234&ids[b]=hello HTTP/1.1
	// Content-Type: application/x-www-form-urlencoded
	// names[first]=thinkerou&names[second]=tianou
	v1Group.POST("/post_map", func(c *gin.Context) {
		ids := c.QueryMap("ids")

		// PostForm 仅适用于 x-www-form-urlencoded 或 form-data
		names := c.PostFormMap("names")

		// 如果是 array 则使用
		// c.QueryArray()
		// c.PostFormArray()
		fmt.Printf("ids: %v; names: %v", ids, names)
	})

	// 请求中使用 Goroutine
	// 特别注意不能使用原始的上下文，必须使用只读副本
	v1Group.GET("/long_async", func(c *gin.Context) {
		// 创建在 goroutine 中使用的副本
		cCp := c.Copy()
		go func() {
			// 用 time.Sleep() 模拟一个长任务。
			time.Sleep(5 * time.Second)

			// 请注意您使用的是复制的上下文 "cCp"，这一点很重要
			log.Println("Done! in path " + cCp.Request.URL.Path)
		}()
	})

	// 绑定 body 为 json 的请求数据
	// Content-Type: application/json
	v1Group.POST("/login", func(c *gin.Context) {
		var form LoginForm
		// ShouldBind 会根据 Content-Type 自动选择绑定格式
		err := c.ShouldBind(&form) // 等价于 c.ShouldBindBodyWith(&form, binding.JSON)  也等价于 c.ShouldBindJSON(&form)

		// c.MustBindWith() 和 c.ShouldBind() 的区别
		// ShouldBind 如果出现绑定错误 Gin 会返回错误并由开发者处理错误和请求
		// MustBindWith 如果发生绑定错误，则请求终止，并触发 c.AbortWithError(400, err).SetType(ErrorTypeBind)。
		// 响应状态码被设置为 400 并且 Content-Type 被设置为 text/plain; charset=utf-8

		if err != nil {
			c.String(http.StatusBadRequest, err.Error())
		}

		c.String(http.StatusOK, "user = %s, password = %s", form.User, form.Password)
	})

	// 返回一个对象自动转换称 json
	v1Group.GET("/json_response", func(c *gin.Context) {
		c.JSON(http.StatusOK, JsonResponse{Code: 100, Msg: "test"})
	})

	// 重定向
	v1Group.GET("/redirect", func(c *gin.Context) {
		c.Request.URL.Path = "/v1/test2"
		router.HandleContext(c)
	})

	// 设置 cookie
	router.GET("/cookie", func(c *gin.Context) {
		cookie, err := c.Cookie("gin_cookie")
		if err != nil {
			cookie = "NotSet"
			c.SetCookie("gin_cookie", "test", 3600, "/", "localhost", false, true)
		}
		fmt.Printf("Cookie value: %s \n", cookie)
	})

	// 上传单文件
	// 为 multipart forms 设置较低的内存限制 (默认是 32 MiB)
	router.MaxMultipartMemory = 8 << 20 // 8 MiB
	router.POST("/upload_one", func(c *gin.Context) {
		// 单文件
		file, _ := c.FormFile("file")
		log.Println(file.Filename)

		dst := "./" + file.Filename
		// 上传文件至指定的完整文件路径
		c.SaveUploadedFile(file, dst)

		c.String(http.StatusOK, fmt.Sprintf("'%s' uploaded!", file.Filename))
	})

	// 上传多文件
	// 为 multipart forms 设置较低的内存限制 (默认是 32 MiB)
	router.MaxMultipartMemory = 8 << 20 // 8 MiB
	router.POST("/upload_many", func(c *gin.Context) {
		// Multipart form
		form, _ := c.MultipartForm()
		files := form.File["upload[]"]

		for _, file := range files {
			log.Println(file.Filename)

			// 上传文件至指定目录
			dst := "./" + file.Filename
			c.SaveUploadedFile(file, dst)
		}
		c.String(http.StatusOK, fmt.Sprintf("%d files uploaded!", len(files)))
	})
}
