---
trigger: manual
---
---
description: 此规则定义了项目中Java单元测试的编写标准和最佳实践，确保测试质量和一致性。适用于创建新单元测试、重构现有测试及代码审查过程中的测试评估。规则涵盖了测试结构组织、命名规范、测试独立性、代码覆盖、边界条件测试、异常处理、断言方法选择及测试数据管理等方面的详细要求。遵循此规则有助于提高代码可靠性、可维护性，并简化问题定位和修复过程。此规则应在编写*Test.java文件时强制执行，尤其适用于业务逻辑复杂、频繁变更或关键功能模块的测试。
globs:
alwaysApply: false
---
# Java单元测试编码规范

## 关键规则

### 学习当前工程使用的单测框架，并保持一致

### 测试结构与组织原则
- 测试结构必须遵循AAA模式（Arrange-Act-Assert）
    - Arrange：准备测试数据和环境
    - Act：执行被测试的代码逻辑
    - Assert：验证执行结果是否符合预期
- 每个测试用例必须专注于验证一个明确的功能点或代码路径
- 严格禁止在单个测试中验证多个不相关的行为
- 在每个测试类开始，使用@Before/@BeforeEach设置通用前置条件
- 在每个测试类结束，使用@After/@AfterEach清理测试环境

### 测试命名与可读性
- 测试方法必须使用清晰描述性的命名，格式：test_<方法名>_<测试场景>_<预期结果>
- 测试类名应与被测试类对应，一般格式为：AI_<被测试类名>Test
- 测试方法名应明确表达测试的意图和预期结果
- 测试注释应说明测试的目的、验证点和特殊条件

### 测试独立性与隔离
- 测试用例之间必须相互独立，执行顺序不得影响结果
- 每个测试必须有完整的前置条件设置和后置清理逻辑
- 相同测试用例在相同环境下必须产生相同结果
- 必须避免依赖外部状态（如全局变量、文件系统）或随机因素
- 当依赖外部系统（如数据库、API）时必须使用Mock
- 明确指定Mock对象的行为和预期调用

### 代码覆盖与分支测试
- 确保代码中的每一行语句至少被执行一次
- 特别关注条件语句（if/else）、循环体和异常处理块
- 验证所有条件分支（true/false）都被测试
- 检查复合条件表达式中的每个逻辑分支
- 必须验证try-catch-finally代码块的逻辑
- 必须验证Diamond配置开关的不同配置值对代码的影响

### 边界条件测试
- 必须测试输入的边界值（最小值、最大值、空值、空集合等）
- 验证边界条件下的行为是否符合预期（如数组越界、数值溢出）
- 关注特殊输入值（如0、-1、null、空字符串、最大值）
- 测试集合类的边界情况（空集合、单元素集合、大量元素）

### 异常测试
- 显式验证预期的异常是否被抛出（使用@Test(expected=...)或assertThrows）
- 指定异常类型和错误信息的关键部分
- 验证异常消息内容是否符合预期
- 测试异常处理代码路径的正确性

### 断言最佳实践
- 使用具体的断言方法而非模糊匹配（如assertEquals而非assertTrue）
- 避免使用assertTrue(true)等无实际验证意义的断言
- 在断言失败消息中提供详细的上下文信息
- 验证方法执行后的副作用（如状态变更、数据持久化）
- 使用间谍（Spy）或存根（Stub）监控外部交互

### 测试数据管理
- 避免在测试代码中硬编码重复数据，使用常量或辅助方法创建测试数据
- 每个测试使用独立的数据副本，避免测试数据之间的污染
- 参数化测试：对同一逻辑使用多组不同输入数据进行测试
- 使用框架提供的参数化测试功能（如JUnit的@ParameterizedTest）

### 测试性能与可维护性
- 单元测试必须在秒级时间内完成
- 避免在测试中执行耗时操作（如网络请求、文件IO）
- 测试应能在任何环境下一致执行
- 测试代码应遵循与生产代码相同的质量标准
- 保持测试代码的简洁性和可读性
- 避免过度测试：不测试框架或库的内部实现、不测试编译器或语言特性保证的行为

## 示例

<example>
// 良好的单元测试示例

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderService orderService;
    private PaymentGateway paymentGateway;
    private OrderRepository orderRepository;
    
    @Before
    public void setUp() {
        // Arrange - 设置测试环境
        paymentGateway = mock(PaymentGateway.class);
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderService(paymentGateway, orderRepository);
    }
    
    @Test
    public void test_placeOrder_withValidOrder_returnsOrderId() {
        // Arrange
        Order order = new Order();
        order.setCustomerId("CUST-001");
        order.setAmount(100.0);
        
        when(orderRepository.save(any(Order.class))).thenReturn(42L);
        
        // Act
        Long orderId = orderService.placeOrder(order);
        
        // Assert
        assertEquals("订单ID应该是42", 42L, orderId.longValue());
        verify(orderRepository).save(order);
        verify(paymentGateway).processPayment(eq(order.getAmount()), eq(order.getCustomerId()));
    }
    
    @Test(expected = InvalidOrderException.class)
    public void test_placeOrder_withNegativeAmount_throwsException() {
        // Arrange
        Order order = new Order();
        order.setCustomerId("CUST-001");
        order.setAmount(-50.0);
        
        // Act
        orderService.placeOrder(order); // 应抛出异常
        
        // Assert - 通过expected属性验证
    }
    
    @Test
    public void test_calculateDiscount_withBoundaryValues() {
        // Arrange
        Order order = new Order();
        
        // Act & Assert - 测试边界值
        order.setAmount(0.0);
        assertEquals(0.0, orderService.calculateDiscount(order), 0.001);
        
        order.setAmount(99.99);
        assertEquals(0.0, orderService.calculateDiscount(order), 0.001);
        
        order.setAmount(100.0);
        assertEquals(5.0, orderService.calculateDiscount(order), 0.001);
        
        order.setAmount(1000.0);
        assertEquals(100.0, orderService.calculateDiscount(order), 0.001);
    }
}
</example>

<example type="invalid">
// 不良的单元测试示例

import org.junit.Test;
import static org.junit.Assert.*;

public class BadOrderServiceTest {

    @Test
    public void testOrderService() {
        // 问题1: 测试方法名称不具描述性
        // 问题2: 在一个测试中测试多个不相关功能
        // 问题3: 没有遵循AAA模式，代码混乱
        
        OrderService service = new OrderService();
        assertTrue(service != null);  // 无意义的断言
        
        Order order = new Order();
        order.setCustomerId("CUST-001");
        order.setAmount(100.0);
        
        Long id = service.placeOrder(order);
        assertTrue(id > 0);  // 使用模糊断言
        
        // 测试其他不相关功能
        assertEquals(5.0, service.calculateDiscount(order), 0.001);
        
        order.setAmount(-50.0);
        try {
            service.placeOrder(order);
            fail("Should throw exception");
        } catch(Exception e) {
            // 问题4: 捕获任何异常而不是具体的预期异常
            assertTrue(true);  // 无意义的断言
        }
    }
    
    @Test
    public void testWithExternalDependency() {
        // 问题5: 依赖外部系统，没有使用Mock
        OrderService service = new OrderService(new RealPaymentGateway(), new DatabaseOrderRepository());
        
        Order order = new Order();
        order.setCustomerId("CUST-001");
        order.setAmount(100.0);
        
        Long id = service.placeOrder(order);
        assertTrue(id > 0);
        
        // 问题6: 直接读取数据库验证结果，而不是验证行为
        DatabaseOrderRepository repo = new DatabaseOrderRepository();
        Order savedOrder = repo.findById(id);
        assertEquals(100.0, savedOrder.getAmount(), 0.001);
    }
    
    @Test
    public void testHardcodedValues() {
        // 问题7: 测试中硬编码重复数据
        OrderService service = new OrderService();
        
        Order order1 = new Order();
        order1.setCustomerId("CUST-001");
        order1.setAmount(100.0);
        Long id1 = service.placeOrder(order1);
        
        Order order2 = new Order();
        order2.setCustomerId("CUST-001");  // 重复硬编码数据
        order2.setAmount(100.0);          // 重复硬编码数据
        Long id2 = service.placeOrder(order2);
        
        // 问题8: 没有独立的测试数据，可能相互干扰
        assertTrue(id2 > id1);
    }
}
</example>
