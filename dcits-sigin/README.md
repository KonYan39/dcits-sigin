git push heroku main
#application.yml
## 时区统一按东八区标准
```yaml
userInfoList:                                   #数组可配置多个
    - openId: ovfnh5G6wcX0dDzowQZC7T7YeZ-I        #必填
      userName: xxxx                              #必填
      atPhoneNum: xxxxxxxxxxx                     #必填 钉钉机器人会@你
      firstCardHour: 8                            #必填 初次打卡的小时
      firstCardMinute: 0                          #必填 初次打卡的分钟数 程序会在设置的此参数后30分随机一分钟执行打卡操作
      lastCardHour: 19                            #必填 末次打卡的小时
      lastCardMinute: 0                           #必填 末次打卡的分钟数 程序会在设置的此参数后30分随机一分钟执行打卡操作
      currentDayCardCounts: 1                     #必填 1|2 总结 ：自行判断 打卡时间范围内启动 1 打卡时间范围外 2
                                                  #1 表示当天已打卡一次 程序运行后会在设置的末次打卡时间执行打卡 （若当前时间已超过设置的firstCardHour:firstCardMinute+30则任务调度会立刻执行，
                                                  #若没超过任务调度会在随机在firstCardHour:firstCardMinute+30以内任选一个时刻进行打卡操作。）
                                                  #2 表示当天已打卡两次 程序运行后会在隔天打卡 同上
                                                  # 
```