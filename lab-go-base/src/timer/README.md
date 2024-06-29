## 定时器

go 中的定时器一共分为三种，timer、ticker、time.after

### timer

仅触发一次，需要使用 reset 重新设置

### ticker

无需重置，定时触发。但是如果不用了需要调用 Stop 方法关闭。

### time.after

底层使用 timer 实现，可以理解为 timer 的便捷封装形式。

