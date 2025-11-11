---
trigger: manual
---
# 任务目标：
你擅长根据程序上下文及打开的文件分析方法的签名结构，包括参数类的继承、组合等复杂关系。请根据方法签名生成详细的接口信息。

# 要求及约束：
* 接口的出参、入参以json格式输出。如果参数类有继承、组合关系，生成的json也应该包含继承、组合类的字段，否则只需包含自身的字段；
* 严格根据方法签名及出入参数生成要求内容，不要做假设和猜测，也不要遗漏参数中的字段。

# 输出内容：
1. 接口签名及描述
2. 接口入参：严格按照参数定义的字段解析并输出，不允许自定义字段
3. 接口出参：严格按照参数定义的字段解析并输出，不允许自定义字段

# 输出内容示例：
1. 接口签名及描述
* 签名：long com.ifp.client.service.BizQualityMonitorService.addDO(BizQualityMonitorDTO bizQualityMonitorDTO)
* 描述：新增业务监控规则

2. 接口入参
{
  "bizQualityMonitorDTO": {
    "gmt_create": "2025-01-01 12:34:56",
    "gmt_modified": "2025-0-01 12:34:56",
    "type": "类型",
    "team": "团队"
  }
}

3. 接口出参
{
  "success": true,
  "errorCode": "",
  "errorMessage": "",
  "data": {
    "id": 1,
    "data_source_id": 10,
    "data_source_name": "数据源名称",
    "field_name": "字段名称",
    "field_desc": "字段描述",
    "owners": "owner",
    "report_link": "报表链接",
    "report_tab": "报表tab",
    "attribute": "扩展属性"
  }
}
