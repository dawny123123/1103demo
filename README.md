# Excel数据分析工具使用指南

## 📖 概述

本工具结合了 `markitdown` 和 `pandas` 两个强大的Python库，为Excel文件提供全面的数据分析功能。

## 🛠 安装与环境配置

### 1. 安装 markitdown
```bash
pip install 'markitdown[all]'
```

### 2. 必需的依赖包
- pandas: Excel文件读取和数据分析
- openpyxl: Excel文件处理
- markitdown: 文档转换为Markdown格式

## 📊 功能特点

### MarkItDown功能
- 🔄 将Excel文件转换为易读的Markdown表格格式
- 📋 保持原始数据结构和格式
- 🎯 支持多工作表Excel文件
- 📝 生成结构化的表格输出

### Pandas分析功能
- 📈 数值列统计分析（平均值、中位数、标准差等）
- 📝 文本列分析（唯一值统计、频次分析）
- ⚠️ 缺失值检测和报告
- 🔍 多工作表分析支持
- 📊 数据质量评估

## 🚀 使用方法

### 方法1: 交互式使用
```python
import sys
sys.path.insert(0, '/path/to/myenv/lib/python3.13/site-packages')

from markitdown import MarkItDown
import pandas as pd

# 使用MarkItDown转换
md = MarkItDown()
result = md.convert('your_excel_file.xlsx')
print(result.text_content)

# 使用Pandas分析
df = pd.read_excel('your_excel_file.xlsx')
print(df.describe())
```

### 方法2: 命令行工具
```bash
python3 excel_analyzer.py your_file.xlsx
```

### 方法3: 直接运行代码
```bash
python3 -c "
import sys
sys.path.insert(0, '/path/to/myenv/lib/python3.13/site-packages')
# ... 分析代码
"
```

## 📋 分析输出示例

### MarkItDown输出
```markdown
## Sheet1
| 产品名称 | 单价 | 库存 | 销量 | 收入 |
| --- | --- | --- | --- | --- |
| 苹果 | 5.5 | 100 | 50 | 275 |
| 香蕉 | 3.0 | 80 | 65 | 195 |
```

### Pandas分析输出
```
🔍 分析工作表: Sheet1
• 数据形状: 5 行 × 5 列
• 列名: 产品名称, 单价, 库存, 销量, 收入
✅ 无缺失值

📈 数值列分析:
    单价:
      平均值: 7.30
      中位数: 5.50
      标准差: 4.82
      范围: 3.00 ~ 15.00
```

## 🎯 应用场景

### 商业数据分析
- 📊 销售数据统计和趋势分析
- 💰 财务报表数据解析
- 📈 业绩指标分析
- 🎯 客户数据洞察

### 学术研究
- 📚 研究数据预处理
- 📊 实验结果统计分析
- 📝 数据质量评估
- 🔍 探索性数据分析

### 数据清洗
- ⚠️ 缺失值识别
- 🔍 异常值检测
- 📊 数据分布分析
- ✅ 数据完整性检查

## 💡 使用技巧

### 1. 处理大文件
- 对于大型Excel文件，考虑分块处理
- 使用 `chunksize` 参数读取部分数据

### 2. 多工作表处理
```python
excel_file = pd.ExcelFile('file.xlsx')
for sheet_name in excel_file.sheet_names:
    df = pd.read_excel('file.xlsx', sheet_name=sheet_name)
    # 分析每个工作表
```

### 3. 自定义分析
```python
# 添加自定义统计
def custom_analysis(df):
    # 相关性分析
    correlation = df.corr()
    
    # 数据分布
    distribution = df.describe(percentiles=[.1, .25, .5, .75, .9])
    
    return correlation, distribution
```

## 🔧 故障排除

### 常见问题

1. **ModuleNotFoundError**: 确保虚拟环境路径正确
2. **文件读取错误**: 检查Excel文件格式和路径
3. **编码问题**: 使用UTF-8编码保存文件
4. **内存不足**: 处理大文件时考虑分批读取

### 解决方案

```python
# 解决路径问题
import sys
sys.path.insert(0, '/correct/path/to/packages')

# 解决编码问题
df = pd.read_excel('file.xlsx', encoding='utf-8')

# 处理大文件
chunks = pd.read_excel('large_file.xlsx', chunksize=1000)
```

## 📈 扩展功能

### 1. 添加可视化
```python
import matplotlib.pyplot as plt
import seaborn as sns

# 创建图表
df.plot(kind='bar')
plt.savefig('analysis_chart.png')
```

### 2. 导出报告
```python
# 生成详细报告
report = f"""
# 分析报告
## 数据概览
{df.info()}

## 统计摘要
{df.describe()}
"""

with open('report.md', 'w', encoding='utf-8') as f:
    f.write(report)
```

### 3. 批量处理
```python
import glob

excel_files = glob.glob('*.xlsx')
for file in excel_files:
    analyze_excel(file)
```

## 📞 技术支持

如果遇到问题或需要定制功能，可以：
1. 检查依赖包版本兼容性
2. 确认Python环境配置
3. 验证Excel文件格式完整性
4. 查看错误日志详细信息

---

**🎉 现在你已经具备了使用这个Excel数据分析工具的所有知识！开始分析你的数据吧！**