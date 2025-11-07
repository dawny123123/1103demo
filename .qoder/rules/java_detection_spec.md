---
trigger: manual
---

# Java语言代码探测文档规范

## 文档结构

- 采用三级以内的目录结构
- 第一级目录应包含该探测声明、探测目的、探测范围、探测日期、探测结果
- 探测结果中包括结果概述、具体列表

## 探测类型

- 有三类探测：项目探测、模块探测和Java包探测

## 探测方法

- 根据用户的提示词要求一次只执行一种类型的探测
- 项目探测和模块探测不要过多阅读具体的文件内容，阅读目录名和文件名即可

## 文档内容

- 探测声明要说明这是AI生成的探测结果，使用的AI工具的名称和版本
- 探测目的要说明探测文档是为了辅助测试用例生成限定上下文使用的
- 探测范围要说明探测了哪些内容，根据探测类型探测指定范围内的目录和文件，包括项目、模块、包、源文件、配置文件、模板文件等
- 探测日期只需要保留最新一次探测的年月日，就是当前的日期
- 项目探测的结果概述中介绍整个项目的功能、文件类型情况、模块情况、外部依赖情况

### 项目探测的具体列表

- 项目探测的具体列表中要包括三个部分文件类型列表、模块列表、外部依赖列表，每个部分都要用表格形式来组织
- 第一部分文件类型列表，要有文件类型模式、主要作用、作用分类、路径举例，不要执行命令，通过阅读扫描生成，类型模式要包括源文件、测试文件、配置文件、构建文件、模板文件等
- 第二部分模块列表中要有模块名、模块的主要功能和作用、模块是否包含测试、是否建议对模块进行测试，要递归扫描子模块
- 第三部分外部依赖的完整包名、版本号、依赖的主要功能和作用、依赖的规模

### 模块探测的具体列表

- 模块探测的结果概述中介绍整个模块的功能、依赖情况（模块间和外部）、Java包情况
- 模块探测的具体列表按表格列举每个包的全名、包功能简述、是否有测试，要递归扫描子包

### Java包探测的具体列表

- Java包探测的结果概述中介绍Java包的功能、包之间的依赖
- Java包探测的具体列表中按表格列举每个类的全名、是否建议测试、类功能简述、依赖的功能简述、备注（不生成测试的原因、生成测试的注意事项）
- Java包探测的具体列表中不要有模块探测具体列表的内容，只生成一个表就好了

### 类的分类规则

- 实体类：大多数方法都是getter和setter，但有少量方法重载
- 工具类：多数方法都是static，没有面向对象封装
- 接口类：interface关键字定义的类
- 抽象类：abstract关键字定义的类
- 实现类：除了接口类、抽象类之外的其他类
- 构建类：用于创建和管理对象的生命周期，比如工厂方法
- 行为类：用于描述对象的行为或功能，比如访问者模式

### 测试用例生成规则

- 纯粹的接口声明没有任何逻辑实现的类不进行测试
- 纯粹的常量类没有任何方法实现的不进行测试

## 文档格式

- 采用markdown格式
- 文档名称为AI_探测文档_<项目或者模块或者Java包>.md
- 将项目的探测文档放在根目录下
- 将各模块的探测文档放在模块的根目录下
- 将各Java包的探测文档放在跟源代码和测试代码评级的ai_docs目录中对应的Java包下

------

<example probe_type="project">
# AI_探测文档_Dubbo项目

## 探测声明

本探测文档由AI工具生成，使用的AI工具为Cursor，版本为1.2.4 (Universal)。探测结果仅用于辅助测试用例生成的限定上下文使用。

## 探测目的

本探测文档旨在为Dubbo项目的测试用例生成提供全面的项目结构和依赖信息，帮助开发者了解项目的整体架构、模块划分、文件类型分布以及外部依赖情况，从而更好地制定测试策略和生成针对性的测试用例。

## 探测范围

本次探测覆盖了Dubbo项目的根目录及其所有子模块，包括：

- 项目根目录下的所有文件和目录
- 所有Maven模块的pom.xml配置文件
- 源代码目录结构（src/main/java、src/test/java）
- 资源文件目录（src/main/resources、src/test/resources）
- 构建脚本和配置文件
- 文档文件（README.md、CHANGES.md等）
- 代码风格配置文件（codestyle目录）

## 探测日期

2025年07月24日

## 探测结果

### 结果概述

Apache Dubbo是一个功能强大且用户友好的Web和RPC框架，支持多种语言实现。项目采用Maven多模块架构，包含核心功能模块、扩展插件模块、Spring Boot集成模块、测试模块和演示模块等。项目主要使用Java语言开发，支持JDK 1.8及以上版本，采用Maven作为构建工具。项目包含丰富的文件类型，包括Java源代码、XML配置文件、YAML配置文件、Markdown文档等。项目依赖Spring Boot、Netty、Zookeeper、Nacos等外部框架和组件，是一个企业级的微服务框架。

### 具体列表

#### 文件类型列表

| 文件类型模式 | 主要作用 | 作用分类 | 路径举例 |
|-------------|---------|---------|---------|
| **/*.java | Java源代码文件 | 源文件 | dubbo-common/src/main/java/org/apache/dubbo/common/utils/CIDRUtils.java |
| **/*Test.java | Java测试文件 | 测试文件 | dubbo-rpc/dubbo-rpc-api/src/test/java/org/apache/dubbo/rpc/filter/DeprecatedFilterTest.java |
| **/pom.xml | Maven项目配置文件 | 构建文件 | pom.xml, dubbo-common/pom.xml |
| **/*.xml | XML配置文件 | 配置文件 | dubbo-config/dubbo-config-spring/src/test/resources/webapps/test/WEB-INF/web.xml |
| **/*.yml, **/*.yaml | YAML配置文件 | 配置文件 | .github/workflows/build-and-test-pr.yml |
| **/*.md | Markdown文档 | 文档文件 | README.md, CHANGES.md |
| **/*.properties | 属性配置文件 | 配置文件 | 项目中包含properties文件 |
| **/*.sh, **/*.cmd | Shell脚本和批处理文件 | 构建文件 | build, build.cmd, mvnw |
| **/*.yml | CI/CD配置文件 | 配置文件 | codecov.yml, .licenserc.yaml |
| **/*.xml | 代码风格配置 | 配置文件 | codestyle/checkstyle.xml |

#### 模块列表

| 模块名 | 模块的主要功能和作用 | 是否包含测试 | 是否建议对模块进行测试 |
|--------|-------------------|-------------|---------------------|
| dubbo-common | 提供Dubbo框架的通用工具类和基础功能 | 是 | 是，核心基础模块 |
| dubbo-remoting | 提供网络通信功能，支持多种协议 | 是 | 是，网络通信核心 |
| dubbo-rpc | 提供RPC调用功能，支持多种RPC协议 | 是 | 是，RPC核心功能 |
| dubbo-cluster | 提供集群管理和负载均衡功能 | 是 | 是，集群管理核心 |
| dubbo-registry | 提供服务注册与发现功能 | 是 | 是，服务发现核心 |
| dubbo-configcenter | 提供配置中心功能 | 是 | 是，配置管理核心 |
| dubbo-config | 提供配置管理功能 | 是 | 是，配置核心 |
| dubbo-serialization | 提供序列化功能 | 是 | 是，序列化核心 |
| dubbo-compatible | 提供兼容性支持 | 是 | 是，兼容性重要 |
| dubbo-metadata | 提供元数据管理功能 | 是 | 是，元数据管理 |
| dubbo-metrics | 提供监控指标功能 | 是 | 是，监控核心 |
| dubbo-plugin | 提供各种扩展插件 | 是 | 是，扩展功能重要 |
| dubbo-spring-boot-project | 提供Spring Boot集成 | 是 | 是，Spring Boot集成 |
| dubbo-test | 提供测试支持 | 是 | 是，测试支持模块 |
| dubbo-demo | 提供示例代码 | 是 | 是，示例代码 |
| dubbo-dependencies-bom | 管理依赖版本 | 否 | 否，依赖管理 |
| dubbo-distribution | 提供发布包 | 否 | 否，发布相关 |
| dubbo-maven-plugin | 提供Maven插件 | 是 | 是，构建工具 |

#### 外部依赖列表

| 完整包名 | 版本号 | 依赖的主要功能和作用 | 依赖的规模 |
|---------|--------|-------------------|-----------|
| org.springframework.boot:spring-boot | 3.5.0 | Spring Boot框架支持 | 大型 |
| org.springframework:spring-core | 6.2.8 | Spring核心功能 | 大型 |
| org.springframework.security:spring-security | 6.5.1 | Spring Security安全框架 | 中型 |
| io.netty:netty-all | 4.1.x | Netty网络框架 | 大型 |
| org.apache.zookeeper:zookeeper | 3.x | Zookeeper服务注册 | 中型 |
| com.alibaba.nacos:nacos-client | 2.x | Nacos服务发现 | 中型 |
| org.junit.jupiter:junit-jupiter | 5.x | JUnit 5测试框架 | 中型 |
| org.mockito:mockito-core | 5.x | Mock测试框架 | 小型 |
| org.apache.maven:maven-plugin-api | 3.x | Maven插件API | 小型 |
| com.google.protobuf:protobuf-java | 3.22.3 | Protocol Buffers支持 | 中型 |
| io.grpc:grpc-netty | 1.54.0 | gRPC网络支持 | 中型 |
| org.apache.curator:curator-framework | 5.x | Curator Zookeeper客户端 | 中型 |
| io.prometheus:simpleclient | 最新 | Prometheus监控 | 小型 |
| io.zipkin.brave:brave | 最新 | Zipkin链路追踪 | 小型 |
| org.apache.skywalking:apm-toolkit-trace | 最新 | SkyWalking链路追踪 | 小型 |
| com.alibaba.fastjson2:fastjson2 | 最新 | JSON序列化 | 小型 |
| com.caucho:hessian | 4.x | Hessian序列化 | 小型 |
| org.apache.sentinel:sentinel-core | 最新 | Sentinel限流熔断 | 中型 |
| io.seata:seata-spring-boot-starter | 最新 | Seata分布式事务 | 中型 |
| org.apache.logging.log4j:log4j-core | 2.x | Log4j2日志框架 | 小型 |

</example>

<example probe_type="module">
# AI_探测文档_dubbo-common模块

## 探测声明

本探测文档由AI工具生成，使用的AI工具为Cursor，版本为1.2.4 (Universal)。探测结果仅用于辅助测试用例生成的限定上下文使用。

## 探测目的

本探测文档旨在为dubbo-common模块的测试用例生成提供全面的模块结构和依赖信息，帮助开发者了解该模块的整体功能、包结构、依赖关系以及测试覆盖情况，从而更好地制定测试策略和生成针对性的测试用例。

## 探测范围

本次探测覆盖了dubbo-common模块的所有内容，包括：

- 模块根目录下的所有文件和目录
- Maven配置文件（pom.xml）
- 源代码目录结构（src/main/java、src/test/java）
- 资源文件目录（src/main/resources、src/test/resources）
- 所有Java包和类的结构
- 测试文件和测试用例
- 配置文件和其他资源文件

## 探测日期

2025年07月24日

## 探测结果

### 结果概述

dubbo-common模块是Dubbo框架的核心基础模块，提供了Dubbo框架运行所需的各种通用功能。该模块包含配置管理、工具类、缓存、线程池、序列化、JSON处理、URL处理、扩展机制等核心功能。模块采用Maven构建，主要依赖包括日志框架（slf4j、log4j）、JSON处理库（fastjson2、gson、jackson）、字节码处理（javassist）、IO工具（commons-io）等。模块内部包含多个功能包，每个包都有相应的测试覆盖，测试覆盖率较高。该模块是其他Dubbo模块的基础依赖，提供了丰富的工具类和基础设施支持。

### 具体列表

#### 包功能列表

| 包全名 | 包功能简述 | 是否有测试 |
|--------|-----------|-----------|
| org.apache.dubbo.common.aot | 原生镜像支持，提供AOT编译相关功能 | 是 |
| org.apache.dubbo.common.beans | Bean相关工具类，提供Bean操作功能 | 是 |
| org.apache.dubbo.common.beanutil | Bean工具类，提供Bean属性操作 | 是 |
| org.apache.dubbo.common.bytecode | 字节码处理，提供动态字节码生成功能 | 是 |
| org.apache.dubbo.common.cache | 缓存功能，提供文件缓存存储 | 是 |
| org.apache.dubbo.common.compact | 紧凑化功能，提供数据压缩处理 | 是 |
| org.apache.dubbo.common.compiler | 编译器功能，提供动态编译支持 | 是 |
| org.apache.dubbo.common.concurrent | 并发工具，提供并发处理功能 | 是 |
| org.apache.dubbo.common.config | 配置管理，提供多种配置源支持 | 是 |
| org.apache.dubbo.common.constants | 常量定义，提供系统常量 | 是 |
| org.apache.dubbo.common.context | 上下文管理，提供运行时上下文 | 是 |
| org.apache.dubbo.common.convert | 类型转换，提供数据转换功能 | 是 |
| org.apache.dubbo.common.deploy | 部署管理，提供应用部署功能 | 是 |
| org.apache.dubbo.common.extension | 扩展机制，提供SPI扩展支持 | 是 |
| org.apache.dubbo.common.function | 函数式编程，提供函数式工具 | 是 |
| org.apache.dubbo.common.infra | 基础设施，提供基础服务支持 | 是 |
| org.apache.dubbo.common.io | IO操作，提供IO工具类 | 是 |
| org.apache.dubbo.common.json | JSON处理，提供JSON序列化功能 | 是 |
| org.apache.dubbo.common.lang | 语言工具，提供语言相关功能 | 是 |
| org.apache.dubbo.common.logger | 日志管理，提供日志功能 | 是 |
| org.apache.dubbo.common.profiler | 性能分析，提供性能监控功能 | 是 |
| org.apache.dubbo.common.reference | 引用管理，提供对象引用功能 | 是 |
| org.apache.dubbo.common.resource | 资源管理，提供资源加载功能 | 是 |
| org.apache.dubbo.common.serialization | 序列化，提供序列化支持 | 是 |
| org.apache.dubbo.common.ssl | SSL支持，提供安全连接功能 | 是 |
| org.apache.dubbo.common.status | 状态管理，提供状态监控功能 | 是 |
| org.apache.dubbo.common.store | 存储管理，提供数据存储功能 | 是 |
| org.apache.dubbo.common.stream | 流处理，提供流操作功能 | 是 |
| org.apache.dubbo.common.system | 系统工具，提供系统相关功能 | 是 |
| org.apache.dubbo.common.threadlocal | 线程本地变量，提供线程隔离功能 | 是 |
| org.apache.dubbo.common.threadpool | 线程池管理，提供线程池功能 | 是 |
| org.apache.dubbo.common.timer | 定时器，提供定时任务功能 | 是 |
| org.apache.dubbo.common.url | URL处理，提供URL解析功能 | 是 |
| org.apache.dubbo.common.utils | 工具类，提供通用工具方法 | 是 |
| org.apache.dubbo.config | 配置类，提供各种配置对象 | 是 |
| org.apache.dubbo.metadata | 元数据，提供元数据管理功能 | 是 |
| org.apache.dubbo.rpc | RPC相关，提供RPC基础功能 | 是 |

</example>

<example probe_type="java_package">
# AI_探测文档_org.apache.dubbo.common.cache包

## 探测声明

本探测文档由AI工具生成，使用的AI工具为Cursor，版本为1.2.4 (Universal)。探测结果仅用于辅助测试用例生成的限定上下文使用。

## 探测目的

本探测文档旨在为org.apache.dubbo.common.cache包的测试用例生成提供全面的包结构和依赖信息，帮助开发者了解该包的整体功能、类结构、依赖关系以及测试覆盖情况，从而更好地制定测试策略和生成针对性的测试用例。

## 探测范围

本次探测覆盖了org.apache.dubbo.common.cache包的所有内容，包括：
- 包目录下的所有Java源文件
- 所有类的结构和方法
- 依赖关系和引用
- 测试文件和测试用例

## 探测日期

2025年07月24日

## 探测结果

### 结果概述

org.apache.dubbo.common.cache包是Dubbo框架的缓存包，提供了文件缓存存储功能。该包主要用于文件缓存的管理和存储，包括文件缓存存储、文件缓存存储工厂等功能。包内包含FileCacheStore、FileCacheStoreFactory等核心类。该包依赖dubbo-common模块的其他包，特别是io包和utils包。该包是Dubbo框架缓存管理的重要组件。

### 具体列表

| 类全名 | 是否建议测试 | 类功能简述 | 依赖的功能简述 | 备注 |
|--------|-------------|-----------|---------------|------|
| org.apache.dubbo.common.cache.FileCacheStore | 是 | 文件缓存存储，提供文件缓存功能 | 依赖文件IO操作 | 实现类，建议生成测试 |
| org.apache.dubbo.common.cache.FileCacheStoreFactory | 是 | 文件缓存存储工厂，提供缓存工厂功能 | 依赖FileCacheStore | 工厂类，建议生成测试 | 
</example>

