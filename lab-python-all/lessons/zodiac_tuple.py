# 元组测试
# 元组元素无法更改

zodiac_name = (
    u'摩羯座', u'水瓶座', u'双鱼座',
    u'白羊座', u'金牛座', u'双子座',
    u'巨蟹座', u'狮子座', u'处女座',
    u'天秤座', u'天蝎座', u'射手座'
)

# 星座所属年月
zodiac_day = (
    (1, 19), (2, 18), (3, 20),
    (4, 19), (5, 20), (6, 21),
    (7, 22), (8, 22), (9, 22),
    (10, 23), (11, 22), (12, 21)
)

# 两数组元组大小比较
print((1, 20) < (2, 18))  # True

# 根据用户输入判断所属星座
zodiac_input = (3, 21)  # 用户输入的月份和日期
for i in range(len(zodiac_day)):
    if zodiac_input <= zodiac_day[i]:
        print(zodiac_name[i])
        break
