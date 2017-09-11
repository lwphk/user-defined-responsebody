# user-defined-responsebody
Spring boot/MVC Json返回参数过滤

# 核心类

com.lwp.annotation.ResponseBodyJson

com.lwp.configuration.ResponseBodyJsonHandler

# 使用(支持点语法)

@ResponseBodyJson(type=User.class,excludes= {"id","userInfo.number","cardList.cardNo"})

@RequestMapping("/filter")

public User filter() {

	return new User();
  
}
