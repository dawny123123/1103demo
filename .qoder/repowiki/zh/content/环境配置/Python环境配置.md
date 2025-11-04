# Python环境配置

<cite>
**本文档引用的文件**  
- [pyvenv.cfg](file://myenv/pyvenv.cfg)
- [activate.csh](file://myenv/bin/activate.csh)
- [Activate.ps1](file://myenv/bin/Activate.ps1)
- [markitdown_mcp_example.py](file://markitdown_mcp_example.py)
- [comprehensive_excel_analysis.py](file://comprehensive_excel_analysis.py)
- [excel_analysis.py](file://excel_analysis.py)
- [excel_analyzer.py](file://excel_analyzer.py)
- [create_sample_excel.py](file://create_sample_excel.py)
</cite>

## 目录

1. [简介](#简介)
2. [Python虚拟环境创建与激活流程](#python虚拟环境创建与激活流程)
3. [pyvenv.cfg配置文件解析](#pyvenvcfg配置文件解析)
4. [跨平台环境激活方法](#跨平台环境激活方法)
5. [依赖包说明与安装](#依赖包说明与安装)
6. [虚拟环境工具脚本功能说明](#虚拟环境工具脚本功能说明)
7. [环境验证方法](#环境验证方法)
8. [常见问题排查](#常见问题排查)
9. [结论](#结论)

## 简介

本项目包含一个完整的Python虚拟环境配置，用于处理Excel和PDF文档分析任务。项目中已预置`myenv`虚拟环境，包含必要的依赖包和工具脚本。本文档详细说明虚拟环境的配置、激活、使用及维护方法。

## Python虚拟环境创建与激活流程

Python虚拟环境通过`venv`模块创建，隔离项目依赖，避免与系统Python环境冲突。创建流程如下：

1. 使用`python -m venv myenv`命令创建名为`myenv`的虚拟环境
2. 系统自动生成`pyvenv.cfg`配置文件和`bin`（或`Scripts`）目录
3. `bin`目录包含不同操作系统的激活脚本
4. 激活后，`pip`和`python`命令指向虚拟环境中的可执行文件

虚拟环境创建后，所有包安装都将限定在该环境中，确保项目依赖的独立性和可移植性。

**Section sources**
- [pyvenv.cfg](file://myenv/pyvenv.cfg)
- [activate.csh](file://myenv/bin/activate.csh)
- [Activate.ps1](file://myenv/bin/Activate.ps1)

## pyvenv.cfg配置文件解析

`pyvenv.cfg`文件定义了虚拟环境的核心配置，决定其行为和Python版本绑定关系：

```ini
home = /opt/homebrew/opt/python@3.13/bin
include-system-site-packages = false
version = 3.13.5
executable = /opt/homebrew/Cellar/python@3.13/3.13.5/Frameworks/Python.framework/Versions/3.13/bin/python3.13
command = /opt/homebrew/opt/python@3.13/bin/python3.13 -m venv /Users/yuxiao/Downloads/0713demo /myenv
```

关键配置项说明：

- **home**: 指向系统Python解释器的安装路径，虚拟环境基于此版本创建
- **include-system-site-packages**: 设为`false`表示不包含系统站点包，确保环境纯净
- **version**: 记录虚拟环境对应的Python版本（3.13.5）
- **executable**: 记录创建虚拟环境时使用的完整Python可执行路径
- **command**: 完整的创建命令，便于追溯环境创建方式

此配置确保虚拟环境与特定Python版本绑定，保证跨平台一致性。

**Section sources**
- [pyvenv.cfg](file://myenv/pyvenv.cfg)

## 跨平台环境激活方法

不同操作系统使用不同的激活脚本进入虚拟环境：

### Unix/Linux/macOS系统

使用`source`命令激活csh脚本：

```bash
source myenv/bin/activate.csh
```

激活后，命令行提示符将显示`(myenv)`前缀，表示当前处于虚拟环境中。

### Windows系统

使用PowerShell执行激活脚本：

```powershell
myenv\bin\Activate.ps1
```

首次使用可能需要设置执行策略：

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

激活脚本会：
- 将虚拟环境的`bin`目录添加到`PATH`环境变量前端
- 设置`VIRTUAL_ENV`环境变量指向环境目录
- 修改命令行提示符以显示当前环境名称
- 提供`deactivate`命令退出虚拟环境

**Section sources**
- [activate.csh](file://myenv/bin/activate.csh)
- [Activate.ps1](file://myenv/bin/Activate.ps1)

## 依赖包说明与安装

项目依赖以下核心Python包：

| 包名称 | 用途 |
|--------|------|
| pandas | 数据分析和处理，用于Excel文件读写与数据操作 |
| markitdown | 文档转换工具，将Excel等文件转换为Markdown格式 |
| PIL/OleFileIO | 图像处理和OLE文件支持，用于提取文档中的嵌入对象 |

### 安装方法

#### 使用requirements.txt（推荐）

```bash
pip install -r requirements.txt
```

#### 手动安装

```bash
pip install pandas markitdown pillow
```

`markitdown_mcp_example.py`脚本显示了如何在代码中导入和使用这些包，特别是`markitdown`包用于文档转换功能。

**Section sources**
- [markitdown_mcp_example.py](file://markitdown_mcp_example.py)

## 虚拟环境工具脚本功能说明

`myenv/bin/`目录包含多个实用工具脚本，用于文档处理：

### 主要工具脚本

- **dumppdf.py**: PDF结构分析工具，可提取和显示PDF文件的内部对象结构
- **pdf2txt.py**: PDF转文本工具，从PDF文件中提取纯文本内容
- **runxlrd.py**: Excel文件分析工具，使用xlrd库读取和显示Excel文件内容
- **vba_extract.py**: VBA宏提取工具，从Office文档中提取嵌入的VBA代码

### 调用方式

直接在激活的虚拟环境中调用：

```bash
# 提取PDF文本
pdf2txt.py document.pdf

# 分析PDF结构
dumppdf.py -i 1,2,3 document.pdf

# 提取Excel数据
runxlrd.py data.xlsx

# 提取VBA宏
vba_extract.py document.docm
```

这些脚本在虚拟环境中可直接执行，无需指定Python解释器，因为`bin`目录已包含可执行的包装脚本。

**Section sources**
- [myenv/bin/dumppdf.py](file://myenv/bin/dumppdf.py)
- [myenv/bin/pdf2txt.py](file://myenv/bin/pdf2txt.py)
- [myenv/bin/runxlrd.py](file://myenv/bin/runxlrd.py)
- [myenv/bin/vba_extract.py](file://myenv/bin/vba_extract.py)

## 环境验证方法

激活虚拟环境后，使用以下命令验证配置是否正确：

### Python版本验证

```bash
python --version
```

应显示与`pyvenv.cfg`中一致的版本号（如Python 3.13.5）。

### 已安装包验证

```bash
pip list
```

应列出项目所需的所有依赖包，包括pandas、markitdown等。

### 环境路径验证

```bash
which python
which pip
```

应指向`myenv/bin/`目录下的可执行文件，而非系统路径。

### 功能验证

运行示例脚本验证环境功能：

```bash
python markitdown_mcp_example.py
```

此脚本测试`markitdown`包的导入和基本功能，确认环境配置完整。

**Section sources**
- [markitdown_mcp_example.py](file://markitdown_mcp_example.py)

## 常见问题排查

### 权限拒绝

**问题**: 在Windows上无法执行`Activate.ps1`

**解决方案**:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

此命令允许用户运行本地编写的脚本，解决执行策略限制。

### 模块导入错误

**问题**: `ImportError: No module named 'pandas'`

**解决方案**:
1. 确认虚拟环境已激活
2. 检查`pip list`确认包是否已安装
3. 重新安装依赖：`pip install -r requirements.txt`

### 虚拟环境未生效

**问题**: `which python`仍指向系统Python

**解决方案**:
1. 确认激活命令正确执行
2. 检查`PATH`环境变量是否包含虚拟环境`bin`目录
3. 重新激活环境：`source myenv/bin/activate.csh`

### 脚本无法找到

**问题**: `command not found: pdf2txt.py`

**解决方案**:
1. 确认虚拟环境已激活
2. 检查`myenv/bin/`目录是否存在该脚本
3. 验证脚本是否有执行权限：`chmod +x myenv/bin/pdf2txt.py`

**Section sources**
- [Activate.ps1](file://myenv/bin/Activate.ps1)
- [activate.csh](file://myenv/bin/activate.csh)

## 结论

本项目提供了完整的Python虚拟环境配置，通过`pyvenv.cfg`文件确保Python版本一致性，使用跨平台激活脚本支持不同操作系统。环境预置了文档处理所需的工具脚本和依赖包，可通过标准流程验证和维护。遵循本文档的指导，用户可快速搭建和使用一致的开发环境，避免常见的配置问题。