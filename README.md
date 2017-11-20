# user-defined-responsebody
Spring boot/MVC Json返回参数过滤

# 配置
	---XML---
	<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
		<property name="customReturnValueHandlers">
			<list>
				<bean class="com.lwp.configuration.ResponseBodyJsonHandler" />
			</list>
		</property>
	</bean>
	---JavaConfig---
	@Autowired
	private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
	
	@Autowired
	ResponseBodyJsonHandler responseBodyJsonHandler;

	@PostConstruct
	public void init() {
		final List<HandlerMethodReturnValueHandler> originalHandlers = new ArrayList<HandlerMethodReturnValueHandler>(
				requestMappingHandlerAdapter.getReturnValueHandlers());
		final int deferredPos = obtainValueHandlerPosition(originalHandlers,
				DeferredResultMethodReturnValueHandler.class);
		originalHandlers.add(deferredPos + 1, responseBodyJsonHandler);
		//添加自己的ReturnValueHandler
		requestMappingHandlerAdapter.setReturnValueHandlers(originalHandlers);
	}

	private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers,
			Class<?> handlerClass) {
		for (int i = 0; i < originalHandlers.size(); i++) {
			final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
			if (handlerClass.isAssignableFrom(valueHandler.getClass())) {
				return i;
			}
		}
		return -1;
	}
	
	
# 使用(支持点语法)

@ResponseBodyJson(type=User.class,excludes= {"id","userInfo.number","cardList.cardNo"})

@RequestMapping("/filter")

public User filter() {

	return new User();
  
}
