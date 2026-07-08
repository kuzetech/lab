# 序列

chinese_zodiac = '猴鸡狗猪鼠牛虎兔龙蛇马羊'

# 切片用法
print(chinese_zodiac[0:4]) # 左闭右开
print(chinese_zodiac[-1]) # 倒序

# 根据用户输入的年份判断生肖
year = 2018
print(chinese_zodiac[year % 12]) # 取余数，得到生肖的索引

# 成员操作符
print('狗' in chinese_zodiac) # 判断是否在序列中
print('狗' not in chinese_zodiac) # 判断是否在序列中
