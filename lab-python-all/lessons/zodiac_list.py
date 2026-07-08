# 列表测试
# 列表元素可以更改

zodiac_name_list = [
    u'摩羯座', u'水瓶座', u'双鱼座',
    u'白羊座', u'金牛座', u'双子座',
    u'巨蟹座', u'狮子座', u'处女座',
    u'天秤座', u'天蝎座', u'射手座'
]

# 增加元素
zodiac_name_list.append('X')
print(zodiac_name_list)

# 删除元素
zodiac_name_list.remove('X')
print(zodiac_name_list)

# 连接操作
print(zodiac_name_list + zodiac_name_list)

# 重复操作
print(zodiac_name_list * 3)

# 切片用法
print(zodiac_name_list[0:4])  # 左闭右开
print(zodiac_name_list[-1])  # 倒序
