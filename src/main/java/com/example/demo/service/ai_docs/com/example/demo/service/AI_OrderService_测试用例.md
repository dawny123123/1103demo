# AI_OrderService_测试用例

## 使用声明

本测试用例文档由AI工具生成，用于辅助生成单元测试保障测试用例数量和覆盖而用的。

## 类的功能

OrderService是订单服务类，负责处理订单相关的业务逻辑。该类提供了订单的创建、查询、更新、删除以及按用户ID查询订单列表等业务功能。OrderService类依赖于OrderDAO进行数据访问操作，并在业务逻辑中添加了相应的校验规则，如订单数量必须大于0、订单金额必须大于0、已完成订单不能修改、已支付订单不能删除等。该类使用Spring框架的@Service注解标记为服务层组件，并通过@Autowired注解注入OrderDAO依赖。

## 测试用例

| 测试用例编号 | 测试用例名称 | 测试用例的描述 | 待测试的函数 | 传入的参数取值 | 预期返回值或者异常 |
|-------------|-------------|---------------|-------------|---------------|-------------------|
| TC001 | testCreateOrder_ValidOrder_ReturnsTrue | 测试创建有效订单 | createOrder(Order order) | order: 包含orderId="12345", userId="user123", productId="product456", quantity=2, totalAmount=new BigDecimal("318.00")的订单对象 | 返回true，订单成功创建 |
| TC002 | testCreateOrder_QuantityLessThanOrEqualToZero_ThrowsException | 测试创建数量<=0的订单，应该抛出异常 | createOrder(Order order) | order: 包含quantity=0的订单对象 | 抛出IllegalArgumentException异常，消息为"购买数量必须大于0" |
| TC003 | testCreateOrder_TotalAmountLessThanOrEqualToZero_ThrowsException | 测试创建金额<=0的订单，应该抛出异常 | createOrder(Order order) | order: 包含totalAmount=new BigDecimal("0.00")或负数的订单对象 | 抛出IllegalArgumentException异常，消息为"订单金额必须大于0" |
| TC004 | testGetOrder_WhenOrderExists_ShouldReturnOrder | 测试获取订单 | getOrder(String orderId) | orderId: "12345"（已存在的订单ID） | 返回对应的Order对象，字段值正确 |
| TC005 | testUpdateOrder_ValidOrder_ReturnsTrue | 测试更新订单 | updateOrder(Order order) | order: 包含已存在orderId="12345"的订单对象，status设置为2（已发货） | 返回true，订单成功更新 |
| TC006 | testUpdateOrder_CompletedOrderCannotBeModified_ReturnsFalse | 测试更新已完成订单应返回false | updateOrder(Order order) | order: 包含已存在orderId="12345"的订单对象，状态为3（已完成） | 返回false，已完成订单不能修改 |
| TC007 | testDeleteOrder_ValidOrder_ReturnsTrue | 测试删除订单 | deleteOrder(String orderId) | orderId: "12345"（已存在的订单ID，状态为0待支付） | 返回true，订单成功删除 |
| TC008 | testDeleteOrder_PaidOrderCannotBeDeleted_ReturnsFalse | 测试删除已支付订单应返回false | deleteOrder(String orderId) | orderId: "12345"（已存在的订单ID，状态为1已支付） | 返回false，已支付订单不能删除 |
| TC009 | testGetOrdersByUserId_ValidUserId_ReturnsOrderList | 测试按用户ID查询订单列表 - 正常查询 | getOrdersByUserId(String userId) | userId: "user001" | 返回该用户的所有订单列表，按创建时间降序排列 |
| TC010 | testGetOrdersByUserId_NullUserId_ThrowsException | 测试按用户ID查询订单列表 - userId为null | getOrdersByUserId(String userId) | userId: null | 抛出IllegalArgumentException异常，消息为"用户ID不能为空" |
| TC011 | testGetOrdersByUserId_EmptyUserId_ThrowsException | 测试按用户ID查询订单列表 - userId为空字符串 | getOrdersByUserId(String userId) | userId: "" | 抛出IllegalArgumentException异常，消息为"用户ID不能为空" |
| TC012 | testGetOrdersByUserId_WhitespaceUserId_ThrowsException | 测试按用户ID查询订单列表 - userId为空白字符串 | getOrdersByUserId(String userId) | userId: "   " | 抛出IllegalArgumentException异常，消息为"用户ID不能为空" |
| TC013 | testGetOrdersByUserId_NoOrders_ReturnsEmptyList | 测试按用户ID查询订单列表 - 用户无订单 | getOrdersByUserId(String userId) | userId: "user999"（不存在订单的用户ID） | 返回空列表 |
| TC014 | testGetAllOrders_ReturnsAllOrders | 测试获取所有订单列表 | getAllOrders() | 无参数 | 返回所有订单列表，按创建时间降序排列 |
| TC015 | testTotalAmountCalculation_QuantityOne | 测试总金额自动计算功能 - 数量为1 | createOrder(Order order) | order: 包含quantity=1, totalAmount=new BigDecimal("159.00")的订单对象 | 订单创建成功，验证总金额为159.00 |
| TC016 | testTotalAmountCalculation_QuantityThree | 测试总金额自动计算功能 - 数量为3 | createOrder(Order order) | order: 包含quantity=3, totalAmount=new BigDecimal("477.00")的订单对象 | 订单创建成功，验证总金额为477.00 |
| TC017 | testTotalAmountCalculation_QuantityFive | 测试总金额自动计算功能 - 数量为5 | createOrder(Order order) | order: 包含quantity=5, totalAmount=new BigDecimal("795.00")的订单对象 | 订单创建成功，验证总金额为795.00 |
| TC018 | testTotalAmountCalculation_QuantityTen | 测试总金额自动计算功能 - 数量为10 | createOrder(Order order) | order: 包含quantity=10, totalAmount=new BigDecimal("1590.00")的订单对象 | 订单创建成功，验证总金额为1590.00 |